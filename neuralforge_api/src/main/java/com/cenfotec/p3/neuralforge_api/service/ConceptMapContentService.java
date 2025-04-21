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
     * Calls the DeepSeek API to generate a concept map from the extracted text.
     *
     * @param text The extracted text to be summarized.
     * @return The concept map in PlantUML format.
     */
    public String getConceptMapFromDeepSeek(String text, String language) {
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
           - El contenido debe estar redactado en el siguiente idioma: """ + language + ".";

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
    public void savePlantUmlDiagram(String content, String title, String email, String type, String projectId) {
        try {
            File directory = new File("src/main/resources/dynamicContent/");
            if (!directory.exists() && !directory.mkdirs()) {
                System.err.println("Error: Could not create directory " + directory.getAbsolutePath());
                return;
            }

            File imageFile = new File(directory, title + ".png");

            try (OutputStream pngOutput = new FileOutputStream(imageFile)) {

                String umlContent = "@startmindmap\n"
                        + "skinparam backgroundColor #18285a\n"
                        + "skinparam nodeFontSize 16\n"
                        + "skinparam nodePadding 20\n"
                        + "skinparam nodeBorderColor #5064BC\n"
                        + "skinparam nodeBackgroundColor #5064BC\n"
                        + "skinparam ArrowColor #5064BC\n"
                        + "skinparam ArrowThickness 2\n"
                        + "skinparam titleFontSize 24\n"
                        + "skinparam titleFontColor #FFFFFF\n"
                        + "skinparam nodeFontColor #FFFFFF\n"
                        + "skinparam lineStyle solid\n"
                        + "skinparam mindMapEdgeThickness 1\n"
                        + "title " + title + "\n"
                        + content + "\n@endmindmap";

                SourceStringReader reader = new SourceStringReader(umlContent);
                reader.generateImage(pngOutput);
            }

            if (!imageFile.exists() || imageFile.length() == 0) {
                System.err.println("Error: Image was not generated correctly.");
                return;
            }

            System.out.println("Image generated successfully at: " + imageFile.getAbsolutePath());

            // Guardar los detalles del diagrama en la base de datos
            DynamicContentEntity dynamicContent = new DynamicContentEntity();
            dynamicContent.setTitle(title);
            dynamicContent.setPath(imageFile.getAbsolutePath());
            dynamicContent.setEmail(email);
            dynamicContent.setType(DynamicContentTypeEnum.valueOf(type));
            dynamicContent.setCreationDate(LocalDateTime.now());
            dynamicContent.setProjectId(projectId);

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