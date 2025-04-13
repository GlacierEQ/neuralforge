package com.cenfotec.p3.neuralforge_api.service;

import com.cenfotec.p3.neuralforge_api.model.entity.ClassSessionEntity;
import com.cenfotec.p3.neuralforge_api.model.entity.CourseTopicEntity;
import com.cenfotec.p3.neuralforge_api.model.entity.CourseWeekEntity;
import com.cenfotec.p3.neuralforge_api.model.entity.ProjectMaterialEntity;
import com.cenfotec.p3.neuralforge_api.model.entity.SelectedDaysEntity;
import com.cenfotec.p3.neuralforge_api.model.entity.TeachingProjectEntity;
import com.cenfotec.p3.neuralforge_api.model.entity.UserEntity;
import com.cenfotec.p3.neuralforge_api.model.enums.DynamicContentTypeEnum;
import com.cenfotec.p3.neuralforge_api.model.enums.UserRoleEnum;
import com.cenfotec.p3.neuralforge_api.model.mapper.ProjectMaterialMapper;
import com.cenfotec.p3.neuralforge_api.model.mapper.SelectedDaysMapper;
import com.cenfotec.p3.neuralforge_api.model.mapper.TeachingProjectMapper;
import com.cenfotec.p3.neuralforge_api.model.resource.TeachingProjectResource;
import com.cenfotec.p3.neuralforge_api.repository.TeachingProjectRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service class for managing teaching projects.
 * 
 * @author Enrique Alpízar
 * @version 1.0
 */
@Service
public class TeachingProjectService {

    @Value("${app.upload.dir:${user.home}/uploads}")
    private String uploadDir;

    private final String baseUploadDir = "uploads";
    private final String materialsDir = "materials";

    @Autowired
    private TeachingProjectRepository teachingProjectRepository;

    @Autowired
    private TeachingProjectMapper teachingProjectMapper;

    @Autowired
    private ProjectMaterialMapper projectMaterialMapper;
    
    @Autowired
    private SelectedDaysService selectedDaysService;

    @Autowired
    private SelectedDaysMapper selectedDaysMapper;

    @Autowired
    private DynamicContentService dynamicContentService;

    /**
     * Get the full path to the uploads directory.
     * @return The Path to the uploads directory.
     */
    private Path getUploadPath() {
        return Paths.get(baseUploadDir, materialsDir);
    }

    /**
     * Creates a new teaching project.
     *
     * @param teachingProject The teaching project resource to create.
     * @return The created teaching project resource.
     */
    @Transactional
    public TeachingProjectResource createTeachingProject(TeachingProjectResource teachingProject) {
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        teachingProject.setCreatorUserId(user.getId());

        SelectedDaysEntity savedSelectedDays = selectedDaysService.save(teachingProject.getSelectedDays());

        TeachingProjectEntity entity = teachingProjectMapper.mapToEntity(teachingProject);
        entity.setId(null); // Fix dumb error. We should have client specific types that don't include ids when creating projects.
        entity.setSelectedDays(savedSelectedDays);

        TeachingProjectEntity savedEntity = teachingProjectRepository.save(entity);
        return teachingProjectMapper.mapToResource(savedEntity);
    }

    /**
     * Updates an existing teaching project.
     *
     * @param id The ID of the teaching project to update.
     * @param teachingProject The updated teaching project resource.
     * @return The updated teaching project resource.
     */
    @Transactional
    public TeachingProjectResource updateTeachingProject(String id, TeachingProjectResource teachingProject) {
        TeachingProjectEntity existingEntity = teachingProjectRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                    "Teaching project not found with id: " + id));

        validateProjectOwnership(existingEntity.getCreatorUserId());

        teachingProject.setId(id);

        
        if (teachingProject.getSelectedDays() != null) {
            var savedSelectedDays = selectedDaysService.save(teachingProject.getSelectedDays());
            teachingProject.setSelectedDays(selectedDaysMapper.toResource(savedSelectedDays));
        }
        
        TeachingProjectEntity updatedEntity = teachingProjectMapper.mapToEntity(teachingProject);
        updatedEntity.setWeeks(existingEntity.getWeeks());
        updatedEntity.setMaterials(existingEntity.getMaterials());

        TeachingProjectEntity savedEntity = teachingProjectRepository.save(updatedEntity);
        return teachingProjectMapper.mapToResource(savedEntity);
    }

    /**
     * Retrieves a teaching project by its ID.
     *
     * @param id The ID of the teaching project to retrieve.
     * @return The teaching project resource.
     */
    public TeachingProjectResource getTeachingProject(String id) {
        TeachingProjectEntity entity = teachingProjectRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                    "Teaching project not found with id: " + id));

        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!user.getId().equals(entity.getCreatorUserId()) && user.getRole().getName() != UserRoleEnum.ROLE_ADMINISTRATOR){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not allowed to visualize this project");
        }

        return teachingProjectMapper.mapToResource(entity);
    }

    /**
     * Retrieves all teaching projects.
     *
     * @return A list of teaching project resources.
     */
    public List<TeachingProjectResource> getAllTeachingProjects() {
        List<TeachingProjectEntity> entities = teachingProjectRepository.findAll();

        return entities.stream()
                .map(teachingProjectMapper::mapToResource)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all teaching projects created by the currently authenticated user.
     *
     * @return A list of teaching project resources created by the current user.
     */
    public List<TeachingProjectResource> getCurrentUserTeachingProjects() {
        UserEntity currentUser = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        return teachingProjectRepository.findByCreatorUserId(currentUser.getId()).stream()
                .map(teachingProjectMapper::mapToResource)
                .collect(Collectors.toList());
    }

    /**
     * Deletes a teaching project by its ID.
     *
     * @param id The ID of the teaching project to delete.
     */
    @Transactional
    public void deleteTeachingProject(String id) {
        TeachingProjectEntity existingEntity = teachingProjectRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                    "Teaching project not found with id: " + id));

        validateProjectOwnership(existingEntity.getCreatorUserId());
        
        teachingProjectRepository.deleteById(id);
    }

    /**
     * Deletes a material from a teaching project.
     *
     * @param projectId The ID of the teaching project.
     * @param materialId The ID of the material to delete.
     */
    @Transactional
    public void deleteMaterial(String projectId, String materialId) {
        TeachingProjectEntity project = teachingProjectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                    "Teaching project not found with id: " + projectId));

        validateProjectOwnership(project.getCreatorUserId());

        project.getMaterials().removeIf(material -> material.getId().equals(materialId));
        // No need to manually set version as we're using the entity retrieved from the database
        // which already has the correct version value
        teachingProjectRepository.save(project);
    }

    /**
     * Validates that the current user owns the project or is an administrator.
     *
     * @param projectOwnerId The ID of the project owner.
     * @throws ResponseStatusException if the user is not authorized.
     */
    private void validateProjectOwnership(String projectOwnerId) {
        UserEntity currentUser = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!currentUser.getId().equals(projectOwnerId) && currentUser.getRole().getName() != UserRoleEnum.ROLE_ADMINISTRATOR) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, 
                "Only the owner of the project or an admin is able to perform actions over this project");
        }
    }

    /**
     * Generates a teaching schedule for a project using DeepSeek AI.
     * This method:
     * 1. Retrieves the teaching project and its materials
     * 2. Extracts text from the materials
     * 3. Sends the prompt along with extracted text to DeepSeek AI
     * 4. Parses the response and creates course weeks, class sessions, and topics
     * 5. Saves the structured data and the raw response
     *
     * @param teachingProjectId The ID of the teaching project.
     * @return The updated teaching project resource with the generated schedule.
     * @throws ResponseStatusException if the project is not found, the user is not authorized,
     *         there is an issue with materials processing, or there is an error in AI response.
     */
    @Transactional
    public TeachingProjectResource generateTeachingSchedule(String teachingProjectId) {
        // Retrieve teaching project
        TeachingProjectEntity project = teachingProjectRepository.findById(teachingProjectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                    "Teaching project not found with id: " + teachingProjectId));

        validateProjectOwnership(project.getCreatorUserId());
        
        // Extract text from all materials
        String materialsText = extractTextFromMaterials(project);
        
        // Generate the prompt for DeepSeek API
        String prompt = generateCourseSchedulePrompt(project, materialsText);
        
        try {
            // Send prompt to DeepSeek API and get the response
            String responseJson = dynamicContentService.sendToDeepSeekAndGetRawResponse(prompt);
            
            // Parse the response and save the schedule
            return processAndSaveSchedule(project, responseJson);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "Error processing DeepSeek AI response: " + e.getMessage());
        }
    }
    
    /**
     * Extracts text content from all materials associated with a teaching project.
     * 
     * @param project The teaching project entity.
     * @return The extracted text from all materials.
     * @throws ResponseStatusException if there is an error extracting text or no content is available.
     */
    private String extractTextFromMaterials(TeachingProjectEntity project) {
        StringBuilder materialsTextBuilder = new StringBuilder();
        
        for (ProjectMaterialEntity material : project.getMaterials()) {
            if ("file".equals(material.getType()) && material.getFileUrl() != null) {
                extractTextFromFile(material, materialsTextBuilder);
            } else if ("text".equals(material.getType()) && material.getDescription() != null) {
                materialsTextBuilder.append(material.getDescription()).append("\n\n");
            }
        }
        
        String materialsText = materialsTextBuilder.toString();
        if (materialsText.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "No content available to generate a teaching schedule. Please add materials to the project.");
        }
        
        return materialsText;
    }
    
    /**
     * Extracts text from a file material and appends it to the provided StringBuilder.
     * 
     * @param material The material entity containing the file.
     * @param textBuilder The StringBuilder to append the extracted text to.
     * @throws ResponseStatusException if there is an error extracting text from the file.
     */
    private void extractTextFromFile(ProjectMaterialEntity material, StringBuilder textBuilder) {
        String fileName = material.getFileUrl().substring(material.getFileUrl().lastIndexOf("/") + 1);
        Path filePath = getUploadPath().resolve(fileName);
        
        try {
            if (Files.exists(filePath)) {
                try (PDDocument document = PDDocument.load(filePath.toFile())) {
                    PDFTextStripper pdfStripper = new PDFTextStripper();
                    textBuilder.append(pdfStripper.getText(document)).append("\n\n");
                }
            }
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "Error extracting text from project material: " + material.getFileName());
        }
    }
    
    /**
     * Processes the AI response, saves the raw JSON, creates the course structure, and persists everything.
     * 
     * @param project The teaching project entity.
     * @param responseJson The JSON response from DeepSeek AI.
     * @return The updated teaching project resource.
     * @throws IOException if there is an error parsing the JSON response.
     */
    private TeachingProjectResource processAndSaveSchedule(TeachingProjectEntity project, String responseJson) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode responseNode = objectMapper.readTree(responseJson);
        
        // Save the raw response to a file and print the path to console
        String filePath = saveRawResponseToFile(project, responseJson);
        System.out.println("Raw JSON response saved to: " + filePath);
        
        // Clear existing schedule data if any
        project.getWeeks().clear();
        
        // Create and save course weeks, class sessions and topics based on the response
        processCourseStructure(project, responseNode);
        
        // Save the project with its new schedule
        TeachingProjectEntity savedProject = teachingProjectRepository.save(project);
        return teachingProjectMapper.mapToResource(savedProject);
    }
    
    /**
     * Saves the raw JSON response from DeepSeek AI to a file.
     * 
     * @param project The teaching project entity.
     * @param responseJson The JSON response from DeepSeek AI.
     * @return The path where the file was saved.
     * @throws IOException if there's an error saving the file.
     */
    private String saveRawResponseToFile(TeachingProjectEntity project, String responseJson) throws IOException {
        // Create directory if it doesn't exist
        Path uploadsDir = Paths.get("uploads/teaching_schedules");
        Files.createDirectories(uploadsDir);
        
        // Create filename using project name and timestamp
        String safeName = project.getName().replaceAll("[^a-zA-Z0-9]", "_");
        String filename = safeName + "_" + System.currentTimeMillis() + ".json";
        Path filePath = uploadsDir.resolve(filename);
        
        // Write the JSON to file
        Files.writeString(filePath, responseJson);
        
        return filePath.toAbsolutePath().toString();
    }
    
    /**
     * Generates a prompt for DeepSeek AI to create a teaching schedule.
     *
     * @param project The teaching project entity.
     * @param materialsText The extracted text from project materials.
     * @return A prompt string for DeepSeek AI.
     */
    private String generateCourseSchedulePrompt(TeachingProjectEntity project, String materialsText) {
        List<String> selectedDaysList = getSelectedDaysList(project.getSelectedDays());
        int hoursPerWeek = selectedDaysList.size() * project.getDailyHours();
        
        StringBuilder promptBuilder = new StringBuilder();
        
        // Introduction
        promptBuilder.append("You are an expert educational planner with deep knowledge of curriculum design and course scheduling. ")
                    .append("I need you to create a detailed teaching schedule for a course with the following requirements:\n\n");
        
        // Course details section
        appendCourseDetails(promptBuilder, project, selectedDaysList, hoursPerWeek);
        
        // Course content section
        promptBuilder.append("Course Content:\n").append(materialsText).append("\n\n");
        
        // Instructions section
        appendInstructions(promptBuilder);
        
        // Response format section
        appendResponseFormat(promptBuilder);
        
        // Final reminders
        promptBuilder.append("IMPORTANT: The response must be a valid JSON that conforms exactly to the structure above. ")
                    .append("Ensure all fields are correctly formatted and the dayOfWeek values must be in uppercase to match the Java enum. ")
                    .append("Remember that orderIndex MUST restart at 1 for each class session.");
        
        return promptBuilder.toString();
    }
    
    /**
     * Gets a list of selected days of the week as strings.
     * 
     * @param selectedDays The SelectedDaysEntity containing day selections.
     * @return A list of day names as strings.
     */
    private List<String> getSelectedDaysList(SelectedDaysEntity selectedDays) {
        List<String> selectedDaysList = new ArrayList<>();
        if (selectedDays.isMonday()) selectedDaysList.add("Monday");
        if (selectedDays.isTuesday()) selectedDaysList.add("Tuesday");
        if (selectedDays.isWednesday()) selectedDaysList.add("Wednesday");
        if (selectedDays.isThursday()) selectedDaysList.add("Thursday");
        if (selectedDays.isFriday()) selectedDaysList.add("Friday");
        if (selectedDays.isSaturday()) selectedDaysList.add("Saturday");
        if (selectedDays.isSunday()) selectedDaysList.add("Sunday");
        return selectedDaysList;
    }
    
    /**
     * Appends course details to the prompt builder.
     * 
     * @param promptBuilder The StringBuilder to append to.
     * @param project The teaching project entity.
     * @param selectedDaysList The list of selected days.
     * @param hoursPerWeek The total hours per week.
     */
    private void appendCourseDetails(StringBuilder promptBuilder, TeachingProjectEntity project, 
                                    List<String> selectedDaysList, int hoursPerWeek) {
        promptBuilder.append("Course Details:\n")
                    .append("- Course Name: ").append(project.getName()).append("\n")
                    .append("- Course Description: ").append(project.getDescription()).append("\n")
                    .append("- Start Date: ").append(project.getStartDate()).append("\n")
                    .append("- End Date: ").append(project.getEndDate()).append("\n")
                    .append("- Total Weeks: ").append(project.getWeeksCount()).append("\n")
                    .append("- Teaching Days: ").append(String.join(", ", selectedDaysList)).append("\n")
                    .append("- Hours Per Session: ").append(project.getDailyHours()).append("\n")
                    .append("- Total Hours Per Week: ").append(hoursPerWeek).append("\n\n");
    }
    
    /**
     * Appends instructions to the prompt builder.
     * 
     * @param promptBuilder The StringBuilder to append to.
     */
    private void appendInstructions(StringBuilder promptBuilder) {
        promptBuilder.append("Instructions:\n")
                    .append("1. Create a comprehensive teaching schedule that covers the entire course content over the specified weeks.\n")
                    .append("2. Divide the content logically across the weeks, ensuring proper sequencing of topics.\n")
                    .append("3. For each week, assign specific topics to each class session based on the selected days.\n")
                    .append("4. Each topic should include a title and a brief description.\n")
                    .append("5. Ensure the distribution of content is balanced across sessions, with approximately equal workloads.\n")
                    .append("6. Consider the natural progression of learning, starting with fundamentals and building up to advanced concepts.\n")
                    .append("7. IMPORTANT: The orderIndex for topics should restart from 1 for each class session. For example, each session should have topics with orderIndex 1, 2, etc., rather than continuing from the previous session.\n\n");
    }
    
    /**
     * Appends the expected response format to the prompt builder.
     * 
     * @param promptBuilder The StringBuilder to append to.
     */
    private void appendResponseFormat(StringBuilder promptBuilder) {
        promptBuilder.append("Response Format:\n")
                    .append("Return a valid JSON object with the following structure that matches our entity model:\n\n")
                    .append("{\n")
                    .append("  \"weeks\": [\n")
                    .append("    {\n")
                    .append("      \"weekNumber\": 1,\n")
                    .append("      \"classSessions\": [\n")
                    .append("        {\n")
                    .append("          \"dayOfWeek\": \"MONDAY\",\n")
                    .append("          \"topics\": [\n")
                    .append("            {\n")
                    .append("              \"title\": \"Introduction to Topic\",\n")
                    .append("              \"description\": \"Brief description of the topic\",\n")
                    .append("              \"orderIndex\": 1,\n")
                    .append("              \"durationMinutes\": 30\n")
                    .append("            },\n")
                    .append("            {\n")
                    .append("              \"title\": \"Second Topic\",\n")
                    .append("              \"description\": \"Brief description of the second topic\",\n")
                    .append("              \"orderIndex\": 2,\n")
                    .append("              \"durationMinutes\": 30\n")
                    .append("            }\n")
                    .append("          ]\n")
                    .append("        },\n")
                    .append("        {\n")
                    .append("          \"dayOfWeek\": \"WEDNESDAY\",\n")
                    .append("          \"topics\": [\n")
                    .append("            {\n")
                    .append("              \"title\": \"Another Topic\",\n")
                    .append("              \"description\": \"Brief description of another topic\",\n")
                    .append("              \"orderIndex\": 1,\n") 
                    .append("              \"durationMinutes\": 30\n")
                    .append("            },\n")
                    .append("            {\n")
                    .append("              \"title\": \"Final Topic\",\n")
                    .append("              \"description\": \"Brief description of the final topic\",\n")
                    .append("              \"orderIndex\": 2,\n")
                    .append("              \"durationMinutes\": 30\n")
                    .append("            }\n")
                    .append("          ]\n")
                    .append("        }\n")
                    .append("      ]\n")
                    .append("    }\n")
                    .append("  ]\n")
                    .append("}\n\n");
    }
    
    /**
     * Processes the DeepSeek AI response and creates course structure entities.
     *
     * @param project The teaching project entity.
     * @param responseNode The JsonNode containing the parsed response.
     */
    private void processCourseStructure(TeachingProjectEntity project, JsonNode responseNode) {
        JsonNode weeksNode = responseNode.path("weeks");
        if (weeksNode.isArray()) {
            for (JsonNode weekNode : weeksNode) {
                CourseWeekEntity weekEntity = createCourseWeekEntity(project, weekNode);
                
                JsonNode sessionsNode = weekNode.path("classSessions");
                if (sessionsNode.isArray()) {
                    processClassSessions(weekEntity, sessionsNode);
                }
                
                project.getWeeks().add(weekEntity);
            }
        }
    }
    
    /**
     * Creates a CourseWeekEntity from a week node in the JSON response.
     * 
     * @param project The teaching project entity.
     * @param weekNode The JSON node representing a week.
     * @return The created CourseWeekEntity.
     */
    private CourseWeekEntity createCourseWeekEntity(TeachingProjectEntity project, JsonNode weekNode) {
        int weekNumber = weekNode.path("weekNumber").asInt();
        
        return CourseWeekEntity.builder()
                .teachingProject(project)
                .weekNumber(weekNumber)
                .classSessions(new ArrayList<>())
                .build();
    }
    
    /**
     * Processes class sessions from the JSON response and adds them to the week entity.
     * 
     * @param weekEntity The course week entity.
     * @param sessionsNode The JSON node containing class sessions.
     */
    private void processClassSessions(CourseWeekEntity weekEntity, JsonNode sessionsNode) {
        for (JsonNode sessionNode : sessionsNode) {
            String dayOfWeekStr = sessionNode.path("dayOfWeek").asText();
            
            try {
                DayOfWeek dayOfWeek = DayOfWeek.valueOf(dayOfWeekStr);
                ClassSessionEntity sessionEntity = createClassSessionEntity(weekEntity, dayOfWeek);
                
                JsonNode topicsNode = sessionNode.path("topics");
                if (topicsNode.isArray()) {
                    processTopics(sessionEntity, topicsNode);
                }
                
                weekEntity.getClassSessions().add(sessionEntity);
            } catch (IllegalArgumentException e) {
                // Skip invalid day of week
                continue;
            }
        }
    }
    
    /**
     * Creates a ClassSessionEntity for a specific day of the week.
     * 
     * @param weekEntity The course week entity.
     * @param dayOfWeek The day of the week for this session.
     * @return The created ClassSessionEntity.
     */
    private ClassSessionEntity createClassSessionEntity(CourseWeekEntity weekEntity, DayOfWeek dayOfWeek) {
        return ClassSessionEntity.builder()
                .courseWeek(weekEntity)
                .dayOfWeek(dayOfWeek)
                .topics(new ArrayList<>())
                .build();
    }
    
    /**
     * Processes topics from the JSON response and adds them to the session entity.
     * 
     * @param sessionEntity The class session entity.
     * @param topicsNode The JSON node containing topics.
     */
    private void processTopics(ClassSessionEntity sessionEntity, JsonNode topicsNode) {
        for (JsonNode topicNode : topicsNode) {
            String title = topicNode.path("title").asText();
            String description = topicNode.path("description").asText();
            int orderIndex = topicNode.path("orderIndex").asInt();
            int durationMinutes = topicNode.path("durationMinutes").asInt();
            
            CourseTopicEntity topicEntity = CourseTopicEntity.builder()
                    .classSession(sessionEntity)
                    .title(title)
                    .description(description)
                    .orderIndex(orderIndex)
                    .durationMinutes(durationMinutes)
                    .build();
            
            sessionEntity.getTopics().add(topicEntity);
        }
    }
}