package com.cenfotec.p3.neuralforge_api.service;

import com.cenfotec.p3.neuralforge_api.model.entity.DynamicContentEntity;
import com.cenfotec.p3.neuralforge_api.model.mapper.DynamicContentMapper;
import com.cenfotec.p3.neuralforge_api.model.resource.DynamicContentResource;
import com.cenfotec.p3.neuralforge_api.repository.DynamicContentRepository;
import com.cenfotec.p3.neuralforge_api.repository.ProjectMaterialRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.server.ResponseStatusException;
import lombok.RequiredArgsConstructor;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import com.cenfotec.p3.neuralforge_api.model.entity.ProjectMaterialEntity;

/**
 * Service responsible for handling dynamic content operations.
 * Includes text extraction from PDF, summary generation, and PDF creation.
 * Also handles saving dynamic content information to the database.
 *
 * @author Fabian Vargas
 * @version 1.0
 */

@Service
@RequiredArgsConstructor
public class DynamicContentService {

    private static final String DEEPSEEK_API_URL = "https://api.deepseek.com/chat/completions";
    private static final String MODEL_NAME = "deepseek-chat";

    @Value("${deepseek.api.bearer-token}")
    private String bearerToken;

    private final DynamicContentRepository dynamicContentRepository;
    private final ProjectMaterialRepository materialRepository;
    private final DynamicContentMapper dynamicContentMapper;
    private final SummaryContentService summaryContentService;
    private final PPTContentService pptContentService;
    private final ConceptMapContentService conceptMapContentService;
    private final CuestionaryContentService cuestionaryContentService;

    private final String baseUploadDir = "uploads";
    private final String materialsDir = "materials";

    public List<DynamicContentResource> getByProjectId(String projectId) {
        return dynamicContentRepository.findByProjectId(projectId).stream()
            .map(dynamicContentMapper::mapToResource)
            .collect(Collectors.toList());
    }

    public void generateContent(String projectId, String materialId, String title, String type, String language) {
        ProjectMaterialEntity material = materialRepository.findById(materialId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Material not found"));

        String fileUrl;
        String fileName;
        String extractedText = "";

        try {
            if ("file".equals(material.getType())) {
                fileUrl = material.getFileUrl();
            } else if ("hyperlink".equals(material.getType())) {
                fileUrl = material.getHyperlink();
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported material type");
            }



            if (fileUrl == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File URL is null");
            }

            fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
            boolean isRemoteFile = fileUrl.startsWith("http");

            if (isRemoteFile) {
                // Leer desde URL remota
                URL url = new URL(fileUrl);
                try (InputStream inputStream = url.openStream()) {
                    if (fileName.toLowerCase().endsWith(".pdf")) {
                        try (PDDocument document = PDDocument.load(inputStream)) {
                            PDFTextStripper pdfStripper = new PDFTextStripper();
                            extractedText = pdfStripper.getText(document);
                        }
                    } else if (fileName.toLowerCase().endsWith(".txt")) {
                        extractedText = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                    } else {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported file type");
                    }
                }
            } else {
                // Leer desde archivo local
                Path filePath = Paths.get(baseUploadDir, materialsDir, fileName);
                if (!Files.exists(filePath)) {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Material file not found");
                }

                if (fileName.toLowerCase().endsWith(".pdf")) {
                    try (PDDocument document = PDDocument.load(filePath.toFile())) {
                        PDFTextStripper pdfStripper = new PDFTextStripper();
                        extractedText = pdfStripper.getText(document);
                    }
                } else if (fileName.toLowerCase().endsWith(".txt")) {
                    extractedText = new String(Files.readAllBytes(filePath), StandardCharsets.UTF_8);
                } else {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported file type");
                }
            }

            // Procesar el texto extraído
            String username = SecurityContextHolder.getContext().getAuthentication().getName();

            switch (type) {
                case "SUMMARY":
                    String summary = summaryContentService.getSummaryFromDeepSeek(extractedText, language);
                    summaryContentService.savePdf(summary, title, username, type, projectId);
                    break;
                case "PPT":
                    String pptContent = pptContentService.getPPTFromDeepSeek(extractedText, language);
                    pptContentService.saveTextAsPPT(pptContent, title, username, type, projectId);
                    break;
                case "CONCEPTMAP":
                    String conceptMapContent = conceptMapContentService.getConceptMapFromDeepSeek(extractedText, language);
                    conceptMapContentService.savePlantUmlDiagram(conceptMapContent, title, username, type, projectId);
                    break;
                case "CUESTIONARY":
                    String cuestionary = cuestionaryContentService.getCuestionaryFromDeepSeek(extractedText, language);
                    cuestionaryContentService.savePdf(cuestionary, title, username, type, projectId);
                    break;
                default:
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported content type");
            }

        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error processing material file");
        }
    }

    public Resource downloadContent(String contentId) {
        DynamicContentEntity content = dynamicContentRepository.findById(contentId)
            .orElseThrow(() -> new IllegalArgumentException("Content not found"));

        File file = new File(content.getPath());
        if (!file.exists()) {
            throw new IllegalArgumentException("Content file not found");
        }

        return new FileSystemResource(file);
    }



    /**
     * Calls the DeepSeek API with the given prompt and returns the raw response.
     * This method is specifically designed for getting structured data back from DeepSeek.
     *
     * @param prompt The prompt to send to DeepSeek API.
     * @return The raw response string from DeepSeek API.
     */
    public String sendToDeepSeekAndGetRawResponse(String prompt) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", MODEL_NAME);

        Map<String, String> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", prompt);

        requestBody.put("messages", List.of(message));
        requestBody.put("max_tokens", 4096);
        requestBody.put("response_format", Map.of("type", "json_object"));

        RestTemplate restTemplate = new RestTemplate(new SimpleClientHttpRequestFactory());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(bearerToken);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<Map> response = restTemplate.exchange(DEEPSEEK_API_URL, HttpMethod.POST, entity, Map.class);

        if (response.getBody() != null && response.getBody().containsKey("choices")) {
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
            if (choices != null && !choices.isEmpty()) {
                Map<String, Object> messageResponse = (Map<String, Object>) choices.get(0).get("message");
                if (messageResponse != null && messageResponse.containsKey("content")) {
                    return (String) messageResponse.get("content");
                }
            }
        }
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to get valid response from DeepSeek API");
    }

}