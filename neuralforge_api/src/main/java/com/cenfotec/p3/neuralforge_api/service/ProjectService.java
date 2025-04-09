package com.cenfotec.p3.neuralforge_api.service;

import com.cenfotec.p3.neuralforge_api.model.resource.UserProjectsResource;
import com.cenfotec.p3.neuralforge_api.model.resource.LearningProjectResource;
import com.cenfotec.p3.neuralforge_api.model.resource.ProgrammedGoalProjectResource;
import com.cenfotec.p3.neuralforge_api.model.resource.TeachingProjectResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service responsible for combined project operations.
 * This service aggregates projects from various project services.
 *
 * @author Enrique Alpízar
 * @version 1.0
 */
@Service
public class ProjectService {

    @Autowired
    private LearningProjectService learningProjectService;

    @Autowired
    private TeachingProjectService teachingProjectService;

    @Autowired
    private ProgrammedGoalProjectService programmedGoalProjectService;

    /**
     * Retrieves all types of projects for the current user in a single operation.
     * Aggregates learning, teaching, and programmed goal projects.
     *
     * @return An {@link UserProjectsResource} containing all the user's projects
     */
    public UserProjectsResource getAllUserProjects() {
        List<LearningProjectResource> learningProjects = learningProjectService.getCurrentUserLearningProjects();
        List<TeachingProjectResource> teachingProjects = teachingProjectService.getCurrentUserTeachingProjects();
        List<ProgrammedGoalProjectResource> programmedGoalProjects = programmedGoalProjectService.getCurrentUserProgrammedGoalProjects();

        return UserProjectsResource.builder()
                .learningProjects(learningProjects)
                .teachingProjects(teachingProjects)
                .programmedGoalProjects(programmedGoalProjects)
                .build();
    }

    /**
     * Retrieves all types of projects for all users
     *
     * @return An {@link UserProjectsResource} containing all projects
     */
    public UserProjectsResource getAllProjects() {
        List<LearningProjectResource> learningProjects = learningProjectService.getAllLearningProjects();
        List<TeachingProjectResource> teachingProjects = teachingProjectService.getAllTeachingProjects();
        List<ProgrammedGoalProjectResource> programmedGoalProjects = programmedGoalProjectService.getAllProgrammedGoalProjects();

        return UserProjectsResource.builder()
                .learningProjects(learningProjects)
                .teachingProjects(teachingProjects)
                .programmedGoalProjects(programmedGoalProjects)
                .build();
    }
} 