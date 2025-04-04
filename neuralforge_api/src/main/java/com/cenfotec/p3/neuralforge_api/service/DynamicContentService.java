package com.cenfotec.p3.neuralforge_api.service;

import com.cenfotec.p3.neuralforge_api.model.entity.DynamicContentEntity;
import com.cenfotec.p3.neuralforge_api.model.enums.DynamicContentTypeEnum;
import com.cenfotec.p3.neuralforge_api.model.mapper.DynamicContentMapper;
import com.cenfotec.p3.neuralforge_api.model.mapper.ProjectMaterialMapper;
import com.cenfotec.p3.neuralforge_api.model.resource.DynamicContentResource;
import com.cenfotec.p3.neuralforge_api.repository.DynamicContentRepository;
import com.cenfotec.p3.neuralforge_api.repository.ProjectMaterialRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.server.ResponseStatusException;
import lombok.RequiredArgsConstructor;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    private final ProjectMaterialMapper materialMapper;
    private final DynamicContentMapper dynamicContentMapper;

    private final String baseUploadDir = "uploads";
    private final String materialsDir = "materials";

    public List<DynamicContentResource> getByProjectId(String projectId) {
        return dynamicContentRepository.findByProjectId(projectId).stream()
            .map(dynamicContentMapper::mapToResource)
            .collect(Collectors.toList());
    }

    public void generateContent(String projectId, String materialId, String title, String type) {
        ProjectMaterialEntity material = materialRepository.findById(materialId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Material not found"));

        if (!"file".equals(material.getType()) || material.getFileUrl() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Material is not a file");
        }

        // Extract the filename from the URL
        String fileName = material.getFileUrl().substring(material.getFileUrl().lastIndexOf("/") + 1);
        Path filePath = Paths.get(baseUploadDir, materialsDir, fileName);

        try {
            if (!Files.exists(filePath)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Material file not found");
            }

            try (PDDocument document = PDDocument.load(filePath.toFile())) {
                PDFTextStripper pdfStripper = new PDFTextStripper();
                String extractedText = pdfStripper.getText(document);

                String summary = getSummaryFromDeepSeek(extractedText);
                savePdf(summary, title, SecurityContextHolder.getContext().getAuthentication().getName(), type, projectId);
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
     * Extracts text from a PDF file, generates a summary, and saves the summary as a PDF.
     *
     * @param file The PDF file to be processed.
     * @param title The title of the summary.
     * @param email The email of the user submitting the summary.
     * @param type The type of content for the summary.
     * @return A success message indicating the PDF was generated and saved.
     * @throws IOException If an error occurs while processing the file.
     */

    public String extractTextAndGeneratePdf(MultipartFile file, String title, String email, String type) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty.");
        }

        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            String extractedText = pdfStripper.getText(document);

            String summary = getSummaryFromDeepSeek(extractedText);
            savePdf(summary, title, email, type, null);
            return "PDF generated and saved successfully.";
        }
    }

    /**
     * Calls the DeepSeek API to generate a summary from the extracted text.
     *
     * @param text The extracted text to be summarized.
     * @return The summarized text.
     */

    private String getSummaryFromDeepSeek(String text) {
        String instructions = """
            Resume el texto de manera didáctica siguiendo estas reglas:
            - Usa "# " para títulos principales.
            - Usa "## " para subtítulos.
            - Usa "### " para sub-subtítulos.
            - Usa "**texto**" para negrita.
            - Usa "*texto*" para cursiva.
            - Usa "- " para listas con viñetas.
            No agregues comentarios adicionales, solo formatea y resume correctamente el texto.""";

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", MODEL_NAME);

        Map<String, String> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", instructions + "\n\n" + text);

        requestBody.put("messages", List.of(message));
        requestBody.put("max_tokens", 2048);
        requestBody.put("response_format", Map.of("type", "text"));

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
        return "";
    }

    /**
     * Saves the summarized content as a PDF and stores the content information in the database.
     *
     * @param content The summarized content to be saved.
     * @param title The title of the summary.
     * @param email The email of the user submitting the summary.
     * @param type The type of content for the summary.
     * @param projectId The ID of the project associated with the content.
     * @throws IOException If an error occurs while creating the PDF.
     */

    private void savePdf(String content, String title, String email, String type, String projectId) throws IOException {
        File directory = new File("src/main/resources/dynamicContent/");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File pdfFile = new File(directory, title + ".pdf");

        try (PDDocument pdfDocument = new PDDocument()) {
            File logoFile = new File("src/main/resources/images/logo.png");
            if (!logoFile.exists()) {
                System.out.println("Logo not found in the specified path: " + logoFile.getAbsolutePath());
                return;
            }

            PDPage coverPage = new PDPage(new PDRectangle(595.276f, 841.890f)); // A4
            pdfDocument.addPage(coverPage);

            try (PDPageContentStream coverStream = new PDPageContentStream(pdfDocument, coverPage)) {

                PDFont titleFont = PDType1Font.HELVETICA_BOLD;
                float titleSize = 30;
                float pageHeight = coverPage.getMediaBox().getHeight();

                float titleY = pageHeight - 200;

                coverStream.beginText();
                coverStream.setFont(titleFont, titleSize);
                float titleWidth = titleFont.getStringWidth(title) / 1000 * titleSize;
                float titleX = (coverPage.getMediaBox().getWidth() - titleWidth) / 2;
                coverStream.newLineAtOffset(titleX, titleY);
                coverStream.showText(title);
                coverStream.endText();

                PDImageXObject logo = PDImageXObject.createFromFile(logoFile.getAbsolutePath(), pdfDocument);
                float logoWidth = 300;
                float logoHeight = 300;
                float logoX = (coverPage.getMediaBox().getWidth() - logoWidth) / 2;
                float logoY = (pageHeight / 2) - (logoHeight / 2) + 60;

                coverStream.drawImage(logo, logoX, logoY, logoWidth, logoHeight);

                String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
                PDFont dateFont = PDType1Font.HELVETICA;
                float dateSize = 14;

                float dateWidth = dateFont.getStringWidth("Generation Date: " + date) / 1000 * dateSize;
                float dateX = (coverPage.getMediaBox().getWidth() - dateWidth) / 2;
                float dateY = logoY - 30;

                coverStream.beginText();
                coverStream.setFont(dateFont, dateSize);
                coverStream.newLineAtOffset(dateX, dateY);
                coverStream.showText("Generation Date: " + date);
                coverStream.endText();
            }

            PDPage contentPage = new PDPage(new PDRectangle(595.276f, 841.890f)); // A4
            pdfDocument.addPage(contentPage);

            PDPageContentStream contentStream = new PDPageContentStream(pdfDocument, contentPage);
            contentStream.beginText();
            float marginLeft = 72;
            float marginRight = 72;
            float marginTopContent = 769;
            float maxWidth = 595 - marginLeft - marginRight;
            float lineHeight = 20;
            float currentHeight = marginTopContent;

            contentStream.newLineAtOffset(marginLeft, currentHeight);

            for (String line : content.split("\\n")) {
                line = line.trim();
                if (line.isEmpty()) {
                    currentHeight -= lineHeight;
                    contentStream.newLineAtOffset(0, -lineHeight);
                    continue;
                }

                PDFont font = PDType1Font.HELVETICA;
                float fontSize = 12;

                if (line.startsWith("# ")) {
                    font = PDType1Font.HELVETICA_BOLD;
                    fontSize = 18;
                    line = line.substring(2);
                } else if (line.startsWith("## ")) {
                    font = PDType1Font.HELVETICA_BOLD;
                    fontSize = 14;
                    line = line.substring(3);
                } else if (line.startsWith("### ")) {
                    font = PDType1Font.HELVETICA_BOLD;
                    fontSize = 12;
                    line = line.substring(4);
                } else if (line.startsWith("- ")) {
                    line = "• " + line.substring(2);
                }

                StringBuilder formattedLine = new StringBuilder();
                String[] parts = line.split("\\*\\*");
                boolean bold = false;

                for (String part : parts) {
                    String[] italicsParts = part.split("\\*");

                    for (String italicPart : italicsParts) {
                        if (bold) {
                            contentStream.setFont(PDType1Font.HELVETICA_BOLD, fontSize);
                            formattedLine.append(italicPart);
                        } else {
                            contentStream.setFont(PDType1Font.HELVETICA, fontSize);
                            formattedLine.append(italicPart);
                        }
                        bold = !bold;
                    }
                }

                String wrappedLine = wrapText(formattedLine.toString(), font, fontSize, maxWidth);

                contentStream.setFont(font, fontSize);
                contentStream.showText(wrappedLine);
                currentHeight -= lineHeight;
                contentStream.newLineAtOffset(0, -lineHeight);

                if (currentHeight < 50) {
                    contentStream.endText();
                    contentStream.close();

                    contentPage = new PDPage(new PDRectangle(595.276f, 841.890f));
                    pdfDocument.addPage(contentPage);

                    contentStream = new PDPageContentStream(pdfDocument, contentPage);
                    contentStream.beginText();
                    contentStream.newLineAtOffset(marginLeft, marginTopContent);
                    currentHeight = marginTopContent;
                }
            }

            contentStream.endText();
            contentStream.close();

            pdfDocument.save(pdfFile);
        }

        DynamicContentEntity dynamicContent = new DynamicContentEntity();
        dynamicContent.setTitle(title);
        dynamicContent.setPath(pdfFile.getAbsolutePath());
        dynamicContent.setEmail(email);
        dynamicContent.setType(DynamicContentTypeEnum.valueOf(type));
        dynamicContent.setCreationDate(LocalDateTime.now());
        dynamicContent.setProjectId(projectId);

        dynamicContentRepository.save(dynamicContent);

        System.out.println("PDF generated and saved successfully.");
        System.out.println("Path: " + pdfFile.getAbsolutePath());
        System.out.println("Title: " + title);
        System.out.println("Email: " + email);
        System.out.println("Type: " + type);
    }

    /**
     * Wraps text to fit within a specified width.
     *
     * @param text The text to be wrapped.
     * @param font The font used for the text.
     * @param fontSize The size of the font.
     * @param maxWidth The maximum width for the text.
     * @return A list of wrapped lines.
     * @throws IOException If an error occurs while measuring text width.
     */

    private String wrapText(String text, PDFont font, float fontSize, float maxWidth) {
        StringBuilder result = new StringBuilder();
        // Remove any newline characters that might cause encoding issues
        String cleanText = text.replace("\n", " ").replace("\r", " ");
        String[] words = cleanText.split(" ");
        StringBuilder line = new StringBuilder();
        
        for (String word : words) {
            // Skip words that contain special characters
            if (word.matches(".*[^\\x00-\\x7F].*")) {
                continue;
            }
            
            String testLine = line.length() > 0 ? line + " " + word : word;
            float width = 0;
            try {
                width = font.getStringWidth(testLine) * fontSize / 1000f;
            } catch (Exception e) {
                // Skip this word if it can't be encoded
                continue;
            }
            
            if (width <= maxWidth) {
                line = new StringBuilder(testLine);
            } else {
                if (line.length() > 0) {
                    result.append(line);
                    // Don't add literal newlines here
                    line = new StringBuilder(word);
                } else {
                    line = new StringBuilder(word);
                }
            }
        }
        
        if (line.length() > 0) {
            result.append(line);
        }
        
        return result.toString();
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

    /**
     * Saves raw content to the DynamicContentEntity without creating a PDF.
     * Used for storing structured data responses like JSON.
     *
     * @param title The title of the content.
     * @param content The raw content to save.
     * @param email The email of the user.
     * @param type The type of the content.
     * @param projectId The ID of the associated project.
     * @return The saved DynamicContentEntity.
     */
    public DynamicContentEntity saveRawContent(String title, String content, String email, String type, String projectId) {
        // Create a temp file to store the raw content
        try {
            File directory = new File("uploads/dynamic_content");
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String fileName = title.replaceAll("[^a-zA-Z0-9]", "_") + "_" + System.currentTimeMillis() + ".json";
            File contentFile = new File(directory, fileName);
            
            try (FileWriter writer = new FileWriter(contentFile)) {
                writer.write(content);
            }

            DynamicContentEntity dynamicContent = DynamicContentEntity.builder()
                    .title(title)
                    .path(contentFile.getAbsolutePath())
                    .email(email)
                    .type(DynamicContentTypeEnum.valueOf(type))
                    .projectId(projectId)
                    .build();

            return dynamicContentRepository.save(dynamicContent);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error saving content: " + e.getMessage());
        }
    }
}