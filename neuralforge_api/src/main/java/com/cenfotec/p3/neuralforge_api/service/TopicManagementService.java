package com.cenfotec.p3.neuralforge_api.service;

import com.cenfotec.p3.neuralforge_api.model.entity.ClassSessionEntity;
import com.cenfotec.p3.neuralforge_api.model.entity.CourseTopicEntity;
import com.cenfotec.p3.neuralforge_api.model.entity.CourseWeekEntity;
import com.cenfotec.p3.neuralforge_api.model.entity.TeachingProjectEntity;
import com.cenfotec.p3.neuralforge_api.model.mapper.CourseTopicMapper;
import com.cenfotec.p3.neuralforge_api.model.mapper.TeachingProjectMapper;
import com.cenfotec.p3.neuralforge_api.model.resource.CourseTopicResource;
import com.cenfotec.p3.neuralforge_api.model.resource.TeachingProjectResource;
import com.cenfotec.p3.neuralforge_api.repository.CourseTopicRepository;
import com.cenfotec.p3.neuralforge_api.repository.TeachingProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for managing teaching project topics - operations like moving, reordering, and locking topics.
 * 
 * @author Enrique Alpízar
 * @version 1.0
 */
@Service
public class TopicManagementService {

    @Autowired
    private TeachingProjectRepository teachingProjectRepository;

    @Autowired
    private CourseTopicRepository courseTopicRepository;

    @Autowired
    private TeachingProjectMapper teachingProjectMapper;

    @Autowired
    private CourseTopicMapper courseTopicMapper;

    @Autowired
    private TeachingProjectService teachingProjectService;

    /**
     * Toggles the locked state of a topic.
     *
     * @param topicId The ID of the topic to toggle lock state.
     * @return The updated topic resource.
     */
    @Transactional
    public CourseTopicResource toggleTopicLock(String topicId) {
        CourseTopicEntity topic = findTopicAndValidateAccess(topicId);
        
        // Toggle the locked state
        topic.setTeacherLocked(!topic.getTeacherLocked());
        
        // Save the updated topic
        CourseTopicEntity savedTopic = courseTopicRepository.save(topic);
        return courseTopicMapper.mapToResource(savedTopic);
    }
    
    /**
     * Moves a topic from one session to another.
     *
     * @param topicId The ID of the topic to move.
     * @param targetWeekNumber The target week number.
     * @param targetSessionId The target session ID.
     * @return The updated teaching project resource.
     */
    @Transactional
    public TeachingProjectResource moveTopicToSession(String topicId, int targetWeekNumber, String targetSessionId) {
        CourseTopicEntity topic = findTopicAndValidateAccess(topicId);
        
        // Check if topic is locked
        if (topic.getTeacherLocked()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Cannot move locked topic. Please unlock it first.");
        }
        
        // Get the current session and project
        ClassSessionEntity sourceSession = topic.getClassSession();
        CourseWeekEntity sourceWeek = sourceSession.getCourseWeek();
        TeachingProjectEntity project = sourceWeek.getTeachingProject();
        
        // Find the target session
        ClassSessionEntity targetSession = findClassSessionById(project, targetWeekNumber, targetSessionId);
        if (targetSession == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Target session not found");
        }
        
        // Set the new order index (add to the end of the target session)
        int newOrderIndex = targetSession.getTopics().size() + 1;
        topic.setOrderIndex(newOrderIndex);
        
        // Move the topic to the target session
        topic.setClassSession(targetSession);
        targetSession.getTopics().add(topic);
        
        // Remove the topic from the source session
        sourceSession.getTopics().remove(topic);
        
        // Reorder topics in source session
        reorderTopicsInSession(sourceSession);
        
        // Save the updated topic
        courseTopicRepository.save(topic);
        
        // Save the project and return the updated resource
        TeachingProjectEntity savedProject = teachingProjectRepository.save(project);
        return teachingProjectMapper.mapToResource(savedProject);
    }
    
    /**
     * Moves a topic up in its session (decrease order index).
     *
     * @param topicId The ID of the topic to move up.
     * @return The updated teaching project resource.
     */
    @Transactional
    public TeachingProjectResource moveTopicUp(String topicId) {
        CourseTopicEntity topic = findTopicAndValidateAccess(topicId);
        
        // Check if topic is locked
        if (topic.getTeacherLocked()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Cannot move locked topic. Please unlock it first.");
        }
        
        ClassSessionEntity session = topic.getClassSession();
        TeachingProjectEntity project = session.getCourseWeek().getTeachingProject();
        
        // Check if topic can be moved up
        if (topic.getOrderIndex() <= 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Topic is already at the top of the session.");
        }
        
        // Find the topic above
        CourseTopicEntity topicAbove = session.getTopics().stream()
                .filter(t -> t.getOrderIndex() == topic.getOrderIndex() - 1)
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                    "Topic with order index " + (topic.getOrderIndex() - 1) + " not found."));
        
        // Check if topic above is locked
        if (topicAbove.getTeacherLocked()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Cannot move topic up because the topic above is locked.");
        }
        
        // Swap order indices
        int tempOrderIndex = topic.getOrderIndex();
        topic.setOrderIndex(topicAbove.getOrderIndex());
        topicAbove.setOrderIndex(tempOrderIndex);
        
        // Save the changes
        courseTopicRepository.save(topic);
        courseTopicRepository.save(topicAbove);
        
        // Save the project and return the updated resource
        TeachingProjectEntity savedProject = teachingProjectRepository.save(project);
        return teachingProjectMapper.mapToResource(savedProject);
    }
    
    /**
     * Moves a topic down in its session (increase order index).
     *
     * @param topicId The ID of the topic to move down.
     * @return The updated teaching project resource.
     */
    @Transactional
    public TeachingProjectResource moveTopicDown(String topicId) {
        CourseTopicEntity topic = findTopicAndValidateAccess(topicId);
        
        // Check if topic is locked
        if (topic.getTeacherLocked()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Cannot move locked topic. Please unlock it first.");
        }
        
        ClassSessionEntity session = topic.getClassSession();
        TeachingProjectEntity project = session.getCourseWeek().getTeachingProject();
        
        // Check if topic can be moved down
        int maxOrderIndex = session.getTopics().stream()
                .mapToInt(CourseTopicEntity::getOrderIndex)
                .max()
                .orElse(0);
        
        if (topic.getOrderIndex() >= maxOrderIndex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Topic is already at the bottom of the session.");
        }
        
        // Find the topic below
        CourseTopicEntity topicBelow = session.getTopics().stream()
                .filter(t -> t.getOrderIndex() == topic.getOrderIndex() + 1)
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                    "Topic with order index " + (topic.getOrderIndex() + 1) + " not found."));
        
        // Check if topic below is locked
        if (topicBelow.getTeacherLocked()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Cannot move topic down because the topic below is locked.");
        }
        
        // Swap order indices
        int tempOrderIndex = topic.getOrderIndex();
        topic.setOrderIndex(topicBelow.getOrderIndex());
        topicBelow.setOrderIndex(tempOrderIndex);
        
        // Save the changes
        courseTopicRepository.save(topic);
        courseTopicRepository.save(topicBelow);
        
        // Save the project and return the updated resource
        TeachingProjectEntity savedProject = teachingProjectRepository.save(project);
        return teachingProjectMapper.mapToResource(savedProject);
    }
    
    /**
     * Helper method to validate topic access and find it by ID.
     */
    private CourseTopicEntity findTopicAndValidateAccess(String topicId) {
        CourseTopicEntity topic = courseTopicRepository.findById(topicId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                    "Topic not found with id: " + topicId));
        
        // Validate that the current user has access to the project
        TeachingProjectEntity project = topic.getClassSession().getCourseWeek().getTeachingProject();
        teachingProjectService.validateProjectOwnership(project.getCreatorUserId());
        
        return topic;
    }
    
    /**
     * Helper method to reorder topics in a session after one has been removed.
     */
    private void reorderTopicsInSession(ClassSessionEntity session) {
        List<CourseTopicEntity> topics = session.getTopics();
        for (int i = 0; i < topics.size(); i++) {
            topics.get(i).setOrderIndex(i + 1);
        }
    }
    
    /**
     * Helper method to find a class session by week number and session ID.
     */
    private ClassSessionEntity findClassSessionById(TeachingProjectEntity project, int weekNumber, String sessionId) {
        return project.getWeeks().stream()
                .filter(week -> week.getWeekNumber() == weekNumber)
                .flatMap(week -> week.getClassSessions().stream())
                .filter(session -> session.getId().equals(sessionId))
                .findFirst()
                .orElse(null);
    }
}
