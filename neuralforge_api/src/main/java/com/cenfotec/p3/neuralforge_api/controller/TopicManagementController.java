package com.cenfotec.p3.neuralforge_api.controller;

import com.cenfotec.p3.neuralforge_api.model.resource.CourseTopicResource;
import com.cenfotec.p3.neuralforge_api.model.resource.TeachingProjectResource;
import com.cenfotec.p3.neuralforge_api.service.TopicManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing teaching project topics.
 * 
 * @author Enrique Alpízar
 * @version 1.0
 */
@RestController
@RequestMapping("/topic-management")
public class TopicManagementController {

    @Autowired
    private TopicManagementService topicManagementService;

    /**
     * Toggles the locked state of a topic.
     *
     * @param topicId The ID of the topic to toggle lock state.
     * @return The updated topic resource.
     */
    @PutMapping("/topics/{topicId}/toggle-lock")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CourseTopicResource> toggleTopicLock(@PathVariable String topicId) {
        return ResponseEntity.ok(topicManagementService.toggleTopicLock(topicId));
    }
    
    /**
     * Moves a topic from one session to another.
     *
     * @param topicId The ID of the topic to move.
     * @param targetWeekNumber The target week number.
     * @param targetSessionId The target session ID.
     * @return The updated teaching project resource.
     */
    @PutMapping("/topics/{topicId}/move-to-session")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TeachingProjectResource> moveTopicToSession(
            @PathVariable String topicId,
            @RequestParam int targetWeekNumber,
            @RequestParam String targetSessionId) {
        return ResponseEntity.ok(topicManagementService.moveTopicToSession(topicId, targetWeekNumber, targetSessionId));
    }
    
    /**
     * Moves a topic up in its session (decrease order index).
     *
     * @param topicId The ID of the topic to move up.
     * @return The updated teaching project resource.
     */
    @PutMapping("/topics/{topicId}/move-up")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TeachingProjectResource> moveTopicUp(@PathVariable String topicId) {
        return ResponseEntity.ok(topicManagementService.moveTopicUp(topicId));
    }
    
    /**
     * Moves a topic down in its session (increase order index).
     *
     * @param topicId The ID of the topic to move down.
     * @return The updated teaching project resource.
     */
    @PutMapping("/topics/{topicId}/move-down")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TeachingProjectResource> moveTopicDown(@PathVariable String topicId) {
        return ResponseEntity.ok(topicManagementService.moveTopicDown(topicId));
    }
}
