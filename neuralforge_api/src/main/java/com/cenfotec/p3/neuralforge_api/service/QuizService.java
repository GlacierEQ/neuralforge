package com.cenfotec.p3.neuralforge_api.service;

import com.cenfotec.p3.neuralforge_api.model.entity.*;
import com.cenfotec.p3.neuralforge_api.model.mapper.QuizAnswerMapper;
import com.cenfotec.p3.neuralforge_api.model.mapper.QuizAttemptMapper;
import com.cenfotec.p3.neuralforge_api.model.mapper.QuizMapper;
import com.cenfotec.p3.neuralforge_api.model.mapper.QuizUserAnswerMapper;
import com.cenfotec.p3.neuralforge_api.model.resource.QuizAttemptResource;
import com.cenfotec.p3.neuralforge_api.model.resource.QuizResource;
import com.cenfotec.p3.neuralforge_api.model.resource.QuizUserAnswerResource;
import com.cenfotec.p3.neuralforge_api.repository.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service responsible for managing quiz operations.
 * Provides functionality for creating, retrieving, updating, and deleting quizzes,
 * as well as generating quiz questions using AI.
 *
 * @author Enrique Alpízar
 * @version 1.0
 */
@Service
public class QuizService {

    private static final String DEEPSEEK_API_URL = "https://api.deepseek.com/chat/completions";
    private static final String MODEL_NAME = "deepseek-chat";

    @Value("${deepseek.api.bearer-token}")
    private String bearerToken;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuizQuestionRepository questionRepository;

    @Autowired
    private QuizAnswerRepository answerRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectMaterialRepository materialRepository;

    @Autowired
    private QuizMapper quizMapper;
    
    @Autowired
    private QuizAttemptRepository attemptRepository;
    
    @Autowired
    private QuizUserAnswerRepository userAnswerRepository;
    
    @Autowired
    private QuizAttemptMapper attemptMapper;
    
    @Autowired
    private QuizUserAnswerMapper userAnswerMapper;
    
    @Autowired
    private QuizAnswerMapper answerMapper;

    /**
     * Creates a new quiz.
     *
     * @param quizResource The quiz resource to create.
     * @return The created quiz resource.
     */
    @Transactional
    public QuizResource createQuiz(QuizResource quizResource) {
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        quizResource.setCreatorUserId(user.getId());

        QuizEntity quizEntity = quizMapper.mapToEntity(quizResource);
        QuizEntity savedQuiz = quizRepository.save(quizEntity);

        return quizMapper.mapToResource(savedQuiz);
    }

    /**
     * Retrieves a quiz by its ID.
     *
     * @param id The ID of the quiz to retrieve.
     * @return The quiz resource if found.
     */
    public QuizResource getQuiz(String id) {
        QuizEntity quiz = quizRepository.findByIdAndNotDeleted(id);
        
        if (quiz == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz not found with id: " + id);
        }
        
        return quizMapper.mapToResource(quiz);
    }

    /**
     * Retrieves all non-deleted quizzes for a specific project.
     *
     * @param projectId The ID of the project to find quizzes for.
     * @return A list of quiz resources.
     */
    public List<QuizResource> getQuizzesByProject(String projectId) {
        List<QuizEntity> quizzes = quizRepository.findByProjectIdAndIsDeletedFalse(projectId);
        
        return quizzes.stream()
                .map(quizMapper::mapToResource)
                .collect(Collectors.toList());
    }

    /**
     * Deletes a quiz by marking it as deleted.
     *
     * @param id The ID of the quiz to delete.
     */
    @Transactional
    public void deleteQuiz(String id) {
        QuizEntity quiz = quizRepository.findByIdAndNotDeleted(id);
        
        if (quiz == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz not found with id: " + id);
        }
        
        UserEntity currentUser = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        // Ensure the user is the creator of the quiz or an admin
        if (!quiz.getCreatorUserId().equals(currentUser.getId()) && 
                !currentUser.getRole().getName().toString().equals("ROLE_ADMINISTRATOR")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to delete this quiz");
        }
        
        // Soft delete the quiz
        quiz.setDeleted(true);
        quizRepository.save(quiz);
    }

    /**
     * Generates a quiz for a project based on its materials using DeepSeek AI.
     *
     * @param projectId The ID of the project to generate a quiz for.
     * @param title The title of the quiz.
     * @param description The description of the quiz.
     * @param questionCount The number of questions to generate.
     * @return The generated quiz resource.
     */
    @Transactional
    public QuizResource generateQuiz(String projectId, String title, String description, int questionCount) {
        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                        "Project not found with id: " + projectId));
        
        List<ProjectMaterialEntity> materials = materialRepository.findByProjectId(projectId);
        
        if (materials.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "Project has no materials to generate quiz from");
        }
        
        // Extract text from materials
        StringBuilder materialText = new StringBuilder();
        for (ProjectMaterialEntity material : materials) {
            String text = extractTextFromMaterial(material);
            materialText.append(text).append("\n\n");
        }
        
        // Generate quiz questions using DeepSeek
        String quizContent = generateQuestionsFromDeepSeek(materialText.toString(), questionCount);
        
        // Create quiz entity
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        QuizEntity quizEntity = QuizEntity.builder()
                .creatorUserId(user.getId())
                .title(title)
                .description(description)
                .project(project)
                .isDeleted(false)
                .build();
        
        QuizEntity savedQuiz = quizRepository.save(quizEntity);
        
        // Parse the JSON response and create question and answer entities
        processQuizContent(savedQuiz, quizContent);
        
        // Return the complete quiz
        return quizMapper.mapToResource(savedQuiz);
    }
    
    /**
     * Extracts text content from a project material.
     *
     * @param material The project material to extract text from.
     * @return The extracted text.
     */
    private String extractTextFromMaterial(ProjectMaterialEntity material) {
        // This is a simplified version. In a real implementation, you would extract text from different file types
        if (material.getDescription() != null && !material.getDescription().isEmpty()) {
            return material.getDescription();
        }
        
        return "Material: " + material.getFileName();
    }
    
    /**
     * Generates quiz questions using DeepSeek AI.
     *
     * @param materialText The text content from project materials.
     * @param questionCount The number of questions to generate.
     * @return The JSON string containing generated questions and answers.
     */
    private String generateQuestionsFromDeepSeek(String materialText, int questionCount) {
        String prompt = buildQuizPrompt(materialText, questionCount);
        
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "user", "content", prompt));
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", MODEL_NAME);
        requestBody.put("messages", messages);
        requestBody.put("max_tokens", 4000);
        
        RestTemplate restTemplate = new RestTemplate(new SimpleClientHttpRequestFactory());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(bearerToken);
        
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        
        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    DEEPSEEK_API_URL, HttpMethod.POST, entity, Map.class);
            
            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null && responseBody.containsKey("choices")) {
                Object choicesObj = responseBody.get("choices");
                if (choicesObj instanceof List<?>) {
                    List<?> choicesList = (List<?>) choicesObj;
                    if (!choicesList.isEmpty() && choicesList.get(0) instanceof Map) {
                        Map<?, ?> choiceMap = (Map<?, ?>) choicesList.get(0);
                        Object messageObj = choiceMap.get("message");
                        if (messageObj instanceof Map) {
                            Map<?, ?> message = (Map<?, ?>) messageObj;
                            Object contentObj = message.get("content");
                            if (contentObj instanceof String) {
                                return (String) contentObj;
                            }
                        }
                    }
                }
            }
            
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                    "Failed to generate quiz from DeepSeek API");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                    "Error connecting to DeepSeek API: " + e.getMessage());
        }
    }
    
    /**
     * Builds the prompt for DeepSeek to generate quiz questions.
     *
     * @param materialText The text content from project materials.
     * @param questionCount The number of questions to generate.
     * @return The formatted prompt string.
     */
    private String buildQuizPrompt(String materialText, int questionCount) {
        StringBuilder promptBuilder = new StringBuilder();
        
        promptBuilder.append("Create a quiz based on the following material with exactly ")
                .append(questionCount)
                .append(" multiple-choice questions. Each question should have 4 possible answers, with only one being correct.\n\n")
                .append("Requirements:\n")
                .append("1. Questions should test understanding and not just memorization\n")
                .append("2. Each question must have 4 answer options labeled a, b, c, and d\n")
                .append("3. Only one answer should be correct\n")
                .append("4. Also provide a brief explanation for why the correct answer is correct\n")
                .append("5. Return the quiz in a valid JSON format as follows:\n\n")
                .append("{\n")
                .append("  \"questions\": [\n")
                .append("    {\n")
                .append("      \"question\": \"Question text goes here?\",\n")
                .append("      \"options\": [\n")
                .append("        {\n")
                .append("          \"text\": \"Option A\",\n")
                .append("          \"isCorrect\": false\n")
                .append("        },\n")
                .append("        {\n")
                .append("          \"text\": \"Option B\",\n")
                .append("          \"isCorrect\": true\n")
                .append("        },\n")
                .append("        {\n")
                .append("          \"text\": \"Option C\",\n")
                .append("          \"isCorrect\": false\n")
                .append("        },\n")
                .append("        {\n")
                .append("          \"text\": \"Option D\",\n")
                .append("          \"isCorrect\": false\n")
                .append("        }\n")
                .append("      ],\n")
                .append("      \"explanation\": \"Explanation of why Option B is correct\"\n")
                .append("    }\n")
                .append("  ]\n")
                .append("}\n\n")
                .append("Material to create quiz from:\n\n")
                .append(materialText);
        
        return promptBuilder.toString();
    }
    
    /**
     * Processes the AI-generated quiz content and creates the question and answer entities.
     *
     * @param quiz The quiz entity to add questions to.
     * @param quizContent The JSON string containing generated questions and answers.
     */
    private void processQuizContent(QuizEntity quiz, String quizContent) {
        try {
            // Extract JSON content from the response
            String jsonContent = extractJsonFromResponse(quizContent);
            
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonContent);
            JsonNode questionsNode = rootNode.path("questions");
            
            if (questionsNode.isArray()) {
                int questionOrder = 1;
                
                for (JsonNode questionNode : questionsNode) {
                    String questionText = questionNode.path("question").asText();
                    String explanation = questionNode.path("explanation").asText();
                    
                    QuizQuestionEntity questionEntity = QuizQuestionEntity.builder()
                            .questionText(questionText)
                            .quiz(quiz)
                            .questionOrder(questionOrder++)
                            .explanation(explanation)
                            .build();
                    
                    questionRepository.save(questionEntity);
                    
                    JsonNode optionsNode = questionNode.path("options");
                    if (optionsNode.isArray()) {
                        int answerOrder = 1;
                        
                        for (JsonNode optionNode : optionsNode) {
                            String answerText = optionNode.path("text").asText();
                            boolean isCorrect = optionNode.path("isCorrect").asBoolean();
                            
                            QuizAnswerEntity answerEntity = QuizAnswerEntity.builder()
                                    .answerText(answerText)
                                    .question(questionEntity)
                                    .isCorrect(isCorrect)
                                    .answerOrder(answerOrder++)
                                    .build();
                            
                            answerRepository.save(answerEntity);
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                    "Error processing quiz content: " + e.getMessage());
        }
    }
    
    /**
     * Extracts valid JSON from the AI response which might include markdown formatting.
     *
     * @param response The raw response from DeepSeek.
     * @return The cleaned JSON string.
     */
    private String extractJsonFromResponse(String response) {
        // Handle case where the response is wrapped in code blocks
        if (response.contains("```json")) {
            response = response.substring(response.indexOf("```json") + 7);
            response = response.substring(0, response.lastIndexOf("```"));
        } else if (response.contains("```")) {
            response = response.substring(response.indexOf("```") + 3);
            response = response.substring(0, response.lastIndexOf("```"));
        }
        
        // Trim whitespace
        return response.trim();
    }
    
    /**
     * Starts a new quiz attempt for the authenticated user.
     *
     * @param quizId The ID of the quiz to take.
     * @return The created quiz attempt resource.
     */
    @Transactional
    public QuizAttemptResource startQuizAttempt(String quizId) {
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        QuizEntity quiz = quizRepository.findByIdAndNotDeleted(quizId);
        
        if (quiz == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz not found with id: " + quizId);
        }
        
        // Create a new quiz attempt
        QuizAttemptEntity attempt = QuizAttemptEntity.builder()
                .quiz(quiz)
                .user(user)
                .startedAt(new Date())
                .score(0) // Initially score is 0
                .totalQuestions(quiz.getQuestions().size())
                .userAnswers(new ArrayList<>()) // Initialize empty userAnswers list
                .build();
        
        QuizAttemptEntity savedAttempt = attemptRepository.save(attempt);
        return attemptMapper.mapToResource(savedAttempt);
    }
    
    /**
     * Submits a user answer for a question in a quiz attempt.
     *
     * @param attemptId The ID of the quiz attempt.
     * @param userAnswerResource The user answer resource containing the selected answer.
     * @return The saved user answer resource with feedback on correctness.
     */
    @Transactional
    public QuizUserAnswerResource saveQuizUserAnswer(String attemptId, QuizUserAnswerResource userAnswerResource) {
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        QuizAttemptEntity attempt = attemptRepository.findById(attemptId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz attempt not found with id: " + attemptId));
        
        // Verify the attempt belongs to the current user
        if (!attempt.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission to access this quiz attempt");
        }
        
        // Check if the attempt has already been completed
        if (attempt.getCompletedAt() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This quiz attempt has already been completed");
        }
        
        // Get the question entity
        QuizQuestionEntity question = questionRepository.findById(userAnswerResource.getQuestionId()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found with id: " + userAnswerResource.getQuestionId()));
        
        // Get the selected answer entity
        QuizAnswerEntity selectedAnswer = answerRepository.findById(userAnswerResource.getSelectedAnswerId()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Answer not found with id: " + userAnswerResource.getSelectedAnswerId()));
        
        // Verify the selected answer belongs to the question
        if (!selectedAnswer.getQuestion().getId().equals(question.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The selected answer does not belong to the specified question");
        }
        
        // Check if the user has already answered this question in this attempt
        userAnswerRepository.findByAttemptIdAndQuestionId(attemptId, question.getId()).ifPresent(existingAnswer -> {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This question has already been answered in this attempt");
        });
        
        // Create and save the user answer
        QuizUserAnswerEntity userAnswer = QuizUserAnswerEntity.builder()
                .attempt(attempt)
                .question(question)
                .selectedAnswer(selectedAnswer)
                .isCorrect(selectedAnswer.isCorrect())
                .build();
        
        QuizUserAnswerEntity savedAnswer = userAnswerRepository.save(userAnswer);
        
        // Find the correct answer for this question (whether user was correct or not)
        QuizAnswerEntity correctAnswer = question.getAnswers().stream()
                .filter(QuizAnswerEntity::isCorrect)
                .findFirst()
                .orElse(null);
        
        // Return the user answer with additional information about the correct answer and explanation
        QuizUserAnswerResource result = userAnswerMapper.mapToResource(savedAnswer);
        result.setExplanation(question.getExplanation());
        
        // Always include the correct answer ID and text (helpful especially when the user's answer is incorrect)
        if (correctAnswer != null) {
            result.setCorrectAnswerId(correctAnswer.getId());
            result.setCorrectAnswerText(correctAnswer.getAnswerText());
        }
        
        return result;
    }
    
    /**
     * Completes a quiz attempt and calculates the final score.
     *
     * @param attemptId The ID of the quiz attempt to complete.
     * @return The completed quiz attempt resource with final score.
     */
    @Transactional
    public QuizAttemptResource completeQuizAttempt(String attemptId) {
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        QuizAttemptEntity attempt = attemptRepository.findById(attemptId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz attempt not found with id: " + attemptId));
        
        // Verify the attempt belongs to the current user
        if (!attempt.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission to access this quiz attempt");
        }
        
        // Check if the attempt has already been completed
        if (attempt.getCompletedAt() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This quiz attempt has already been completed");
        }
        
        // Get all user answers for this attempt
        List<QuizUserAnswerEntity> userAnswers = userAnswerRepository.findByAttemptId(attemptId);
        
        // Calculate score
        long correctAnswers = userAnswers.stream().filter(QuizUserAnswerEntity::isCorrect).count();
        int totalQuestions = attempt.getTotalQuestions();
        int score = totalQuestions > 0 ? (int) ((correctAnswers * 100) / totalQuestions) : 0;
        
        // Update attempt with completion information
        attempt.setCompletedAt(new Date());
        attempt.setScore(score);
        QuizAttemptEntity savedAttempt = attemptRepository.save(attempt);
        
        return attemptMapper.mapToResource(savedAttempt);
    }
    
    /**
     * Gets all attempts for a specific quiz by the authenticated user.
     *
     * @param quizId The ID of the quiz to get attempts for.
     * @return A list of quiz attempt resources.
     */
    public List<QuizAttemptResource> getQuizAttempts(String quizId) {
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        QuizEntity quiz = quizRepository.findByIdAndNotDeleted(quizId);
        
        if (quiz == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz not found with id: " + quizId);
        }
        
        List<QuizAttemptEntity> attempts = attemptRepository.findByQuizIdAndUserId(quizId, user.getId());
        
        return attempts.stream()
                .map(attemptMapper::mapToResource)
                .collect(Collectors.toList());
    }
    
    /**
     * Gets a specific quiz attempt by ID.
     *
     * @param attemptId The ID of the quiz attempt to retrieve.
     * @return The quiz attempt resource.
     */
    public QuizAttemptResource getQuizAttempt(String attemptId) {
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        QuizAttemptEntity attempt = attemptRepository.findById(attemptId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz attempt not found with id: " + attemptId));
        
        // Verify the attempt belongs to the current user
        if (!attempt.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission to access this quiz attempt");
        }
        
        // Get the basic mapped resource
        QuizAttemptResource attemptResource = attemptMapper.mapToResource(attempt);
        
        // Enhance user answers with correct answer information if not already present
        if (attemptResource.getUserAnswers() != null) {
            for (QuizUserAnswerResource userAnswer : attemptResource.getUserAnswers()) {
                // Skip if correct answer info is already present
                if (userAnswer.getCorrectAnswerId() != null && userAnswer.getCorrectAnswerText() != null) {
                    continue;
                }
                
                // Find the question entity to get its correct answer
                QuizQuestionEntity question = questionRepository.findById(userAnswer.getQuestionId()).orElse(null);
                if (question != null) {
                    // Find the correct answer
                    QuizAnswerEntity correctAnswer = question.getAnswers().stream()
                            .filter(QuizAnswerEntity::isCorrect)
                            .findFirst()
                            .orElse(null);
                    
                    if (correctAnswer != null) {
                        userAnswer.setCorrectAnswerId(correctAnswer.getId());
                        userAnswer.setCorrectAnswerText(correctAnswer.getAnswerText());
                        // Make sure explanation is set
                        if (userAnswer.getExplanation() == null || userAnswer.getExplanation().isEmpty()) {
                            userAnswer.setExplanation(question.getExplanation());
                        }
                    }
                }
            }
        }
        
        return attemptResource;
    }
}
