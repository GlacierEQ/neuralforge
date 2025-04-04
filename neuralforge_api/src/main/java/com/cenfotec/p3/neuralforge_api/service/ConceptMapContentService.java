package com.cenfotec.p3.neuralforge_api.service;

import com.cenfotec.p3.neuralforge_api.model.entity.DynamicContentEntity;
import com.cenfotec.p3.neuralforge_api.model.enums.DynamicContentTypeEnum;
import com.cenfotec.p3.neuralforge_api.repository.DynamicContentRepository;
import net.sourceforge.plantuml.SourceStringReader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service responsible for handling dynamic content operations.
 * Includes text extraction from PDF, concept map generation, and saving content information to the database.
 */
@Service
public class ConceptMapContentService {

    private static final String DEEPSEEK_API_URL = "https://api.deepseek.com/chat/completions";
    private static final String MODEL_NAME = "deepseek-chat";

    @Value("${deepseek.api.bearer-token}")
    private String bearerToken;

    @Autowired
    private DynamicContentRepository dynamicContentRepository;

    /**
     * Extracts text from a PDF file, generates a concept map, and saves the concept map as an image.
     *
     * @param file The PDF file to be processed.
     * @param title The title of the concept map.
     * @param email The email of the user submitting the concept map.
     * @param type The type of content for the concept map.
     * @return A success message indicating the concept map was generated and saved.
     * @throws IOException If an error occurs while processing the file.
     */
    public String extractTextAndGenerateConceptMap(MultipartFile file, String title, String email, String type) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty.");
        }

        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            String extractedText = pdfStripper.getText(document);

            String conceptMap = getConceptMapFromDeepSeek(extractedText);
            savePlantUmlDiagram(conceptMap, title, email, type);
            return "Concept map generated and saved successfully.";
        }
    }

    /**
     * Calls the DeepSeek API to generate a concept map from the extracted text.
     *
     * @param text The extracted text to be summarized.
     * @return The concept map in PlantUML format.
     */
    private String getConceptMapFromDeepSeek(String text) {
        String instructions = """
            Genera un mapa conceptual en formato PlantUML siguiendo estas reglas:
            - NO uses bloques de código Markdown como ```plantuml``` ni ningún otro formato de código.
            - NO agregues comentarios adicionales, solo devuelve el mapa en formato de texto.
            - Usa "* " para el concepto principal.
            - Usa "** " para subtemas directos del concepto principal.
            - Usa "*** " para subtemas de segundo nivel.
            - Usa "**** " para subtemas de tercer nivel.
            - NO agregues "@startmindmap" ni "@endmindmap".
            - Toma en cuenta solo info importante, que sea un mapa util didacticamente, descarta informacion de formato o referencias"
            """;

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
     * Saves the concept map as an image and stores the content information in the database.
     *
     * @param content The concept map content to be saved.
     * @param title The title of the concept map.
     * @param email The email of the user submitting the concept map.
     * @param type The type of content for the concept map.
     * @throws IOException If an error occurs while creating the image.
     */
    private void savePlantUmlDiagram(String content, String title, String email, String type) {
        try {
            File directory = new File("src/main/resources/dynamicContent/");
            if (!directory.exists() && !directory.mkdirs()) {
                System.err.println("Error: Could not create directory " + directory.getAbsolutePath());
                return;
            }

            File imageFile = new File(directory, title + ".png");

            try (OutputStream pngOutput = new FileOutputStream(imageFile)) {
                String umlContent = "@startmindmap\n" + content + "\n@endmindmap";
                SourceStringReader reader = new SourceStringReader(umlContent);
                reader.generateImage(pngOutput);
            }

            if (!imageFile.exists() || imageFile.length() == 0) {
                System.err.println("Error: Image was not generated correctly.");
                return;
            }

            System.out.println("Image generated successfully at: " + imageFile.getAbsolutePath());

            DynamicContentEntity dynamicContent = new DynamicContentEntity();
            dynamicContent.setTitle(title);
            dynamicContent.setPath(imageFile.getAbsolutePath());
            dynamicContent.setEmail(email);
            dynamicContent.setType(DynamicContentTypeEnum.valueOf(type));
            dynamicContent.setCreationDate(LocalDateTime.now());

            dynamicContentRepository.save(dynamicContent);

            System.out.println("PlantUML diagram saved successfully.");
        } catch (IOException e) {
            System.err.println("I/O error while generating the diagram: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}