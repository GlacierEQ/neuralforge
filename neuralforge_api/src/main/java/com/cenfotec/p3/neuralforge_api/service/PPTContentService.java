package com.cenfotec.p3.neuralforge_api.service;

import com.cenfotec.p3.neuralforge_api.model.entity.DynamicContentEntity;
import com.cenfotec.p3.neuralforge_api.model.enums.DynamicContentTypeEnum;
import com.cenfotec.p3.neuralforge_api.repository.DynamicContentRepository;
import org.apache.poi.sl.usermodel.PictureData;
import org.apache.poi.sl.usermodel.TextParagraph;
import org.apache.poi.xslf.usermodel.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service responsible for handling dynamic content operations.
 * Includes text extraction from PDF, slide deck generation, and saving content information to the database.
 */
@Service
public class PPTContentService {

    private static final String DEEPSEEK_API_URL = "https://api.deepseek.com/chat/completions";
    private static final String MODEL_NAME = "deepseek-chat";

    @Value("${deepseek.api.bearer-token}")
    private String bearerToken;

    @Autowired
    private DynamicContentRepository dynamicContentRepository;


    /**
     * Extracts text from a PDF file, generates a slide deck, and saves the presentation.
     *
     * @param file  The PDF file to be processed.
     * @param title The title of the presentation.
     * @param email The email of the user submitting the content.
     * @param type  The type of content.
     * @return A success message indicating the slide deck was generated and saved.
     * @throws IOException If an error occurs while processing the file.
     */
    /**
     * Calls the DeepSeek API to generate a structured slide deck from the extracted text.
     *
     * @param text          The extracted text to be converted into slides.
     * @return The structured slide content.
     */
    public String getPPTFromDeepSeek(String text, String language) {
        String instructions = """
            Genera una presentación en texto siguiendo este formato:
            - Cada diapositiva comienza con "Slide X:"
            - El título de la diapositiva empieza con "Title: "
            - Los puntos clave de la diapositiva empiezan con "- "
            - NO uses bloques de código ni etiquetas Markdown.
            - Que los titulos de cada diapositica no pasen los 30 caracteres.
            - NO agregues texto extra ni explicaciones, solo devuelve el contenido en el formato indicado.
            - Resumir información de manera efectiva para una presentación didáctica.
            - Que lo que coloques tenga sentido con el tema, esta bien si quitas texto que no sea relevante.
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
     * Saves the structured slide content as a PowerPoint presentation.
     *
     * @param content The structured slide content.
     * @param title   The title of the presentation.
     * @param email   The email of the user.
     * @param type    The type of content.
     */
    public void saveTextAsPPT(String content, String title, String email, String type, String projectId) {
        try {
            // Definir el directorio donde se guardarán las presentaciones
            File directory = new File("src/main/resources/dynamicContent/");
            if (!directory.exists() && !directory.mkdirs()) {
                System.err.println("Error: No se pudo crear el directorio " + directory.getAbsolutePath());
                return;
            }

            // Crear el archivo PPTX
            File pptFile = new File(directory, title + ".pptx");

            // Verificar si el archivo ya existe y si es válido
            if (pptFile.exists() && pptFile.length() > 0) {
                System.out.println("El archivo ya existe y tiene contenido. Se sobrescribirá.");
            }

            try (XMLSlideShow ppt = new XMLSlideShow(); FileOutputStream out = new FileOutputStream(pptFile)) {

                ppt.setPageSize(new java.awt.Dimension(1920, 1080));

                File coverImageFile = new File("src/main/resources/images/mainBackground.png"); // Fondo para la primera diapositiva
                File contentImageFile = new File("src/main/resources/images/finalBackground.png"); // Fondo para las demás

                PictureData coverPictureData = ppt.addPicture(coverImageFile, XSLFPictureData.PictureType.PNG);
                PictureData contentPictureData = ppt.addPicture(contentImageFile, XSLFPictureData.PictureType.PNG);

                // Crear la diapositiva de portada
                XSLFSlide coverSlide = ppt.createSlide();
                XSLFPictureShape coverPictureShape = coverSlide.createPicture(coverPictureData);
                coverPictureShape.setAnchor(new java.awt.Rectangle(0, 0, 1920, 1080));

                XSLFTextBox coverTitleBox = coverSlide.createTextBox();
                coverTitleBox.setAnchor(new java.awt.Rectangle(400, 400, 1120, 200));
                XSLFTextParagraph coverTitleParagraph = coverTitleBox.addNewTextParagraph();
                coverTitleParagraph.setTextAlign(TextParagraph.TextAlign.CENTER);
                XSLFTextRun coverTitleRun = coverTitleParagraph.addNewTextRun();
                coverTitleRun.setText(title);
                coverTitleRun.setBold(true);
                coverTitleRun.setFontSize(100.0);
                coverTitleRun.setFontFamily("Arial");
                coverTitleRun.setFontColor(new java.awt.Color(255, 255, 255));

                // Procesar las diapositivas de contenido
                String[] slides = content.split("Slide \\d+:");

                for (String slideContent : slides) {
                    if (slideContent.trim().isEmpty()) continue;

                    XSLFSlide slide = ppt.createSlide();
                    XSLFPictureShape pictureShape = slide.createPicture(contentPictureData);
                    pictureShape.setAnchor(new java.awt.Rectangle(0, 0, 1920, 1080));

                    String[] lines = slideContent.trim().split("\\n");

                    // Si hay un título, agregarlo
                    if (lines.length > 0 && lines[0].startsWith("Title:")) {
                        String titleText = lines[0].replace("Title:", "").trim();

                        XSLFTextBox titleBox = slide.createTextBox();
                        titleBox.setAnchor(new java.awt.Rectangle(400, 150, 1820, 100));
                        XSLFTextParagraph titleParagraph = titleBox.addNewTextParagraph();
                        XSLFTextRun titleRun = titleParagraph.addNewTextRun();
                        titleRun.setText(titleText);
                        titleRun.setBold(true);
                        titleRun.setFontSize(84.0);
                        titleRun.setFontFamily("Arial");
                        titleRun.setFontColor(new java.awt.Color(255, 255, 255));
                    }

                    // Agregar contenido
                    XSLFTextBox contentBox = slide.createTextBox();
                    contentBox.setAnchor(new java.awt.Rectangle(500, 350, 1220, 700)); // Mayor separación del título

                    for (int i = 1; i < lines.length; i++) {
                        if (lines[i].startsWith("-")) {
                            XSLFTextParagraph bulletParagraph = contentBox.addNewTextParagraph();
                            bulletParagraph.setBullet(true);
                            bulletParagraph.setSpaceAfter(35.0); // Más espacio entre puntos
                            XSLFTextRun bulletRun = bulletParagraph.addNewTextRun();
                            bulletRun.setText(" " + lines[i].replace("-", "").trim()); // Espacio entre el punto y el texto
                            bulletRun.setFontSize(44.0);
                            bulletRun.setFontFamily("Arial");
                            bulletRun.setFontColor(new java.awt.Color(255, 255, 255));
                        } else {
                            XSLFTextParagraph textParagraph = contentBox.addNewTextParagraph();
                            textParagraph.setLineSpacing(28.0); // Más espacio entre líneas de texto plano
                            textParagraph.setSpaceAfter(25.0);
                            XSLFTextRun textRun = textParagraph.addNewTextRun();
                            textRun.setText(lines[i].trim());
                            textRun.setFontSize(35.0);
                            textRun.setFontFamily("Arial");
                            textRun.setFontColor(new java.awt.Color(255, 255, 255));
                        }
                    }
                }

                ppt.write(out);
            }

            // Verificar si el archivo PPTX fue creado correctamente
            if (!pptFile.exists() || pptFile.length() == 0) {
                System.err.println("Error: La presentación no se generó correctamente.");
                return;
            }

            System.out.println("Presentación generada exitosamente en: " + pptFile.getAbsolutePath());

            DynamicContentEntity dynamicContent = new DynamicContentEntity();
            dynamicContent.setTitle(title);
            dynamicContent.setPath(pptFile.getAbsolutePath());
            dynamicContent.setEmail(email);
            dynamicContent.setType(DynamicContentTypeEnum.valueOf(type));
            dynamicContent.setCreationDate(LocalDateTime.now());
            dynamicContent.setProjectId(projectId);

            dynamicContentRepository.save(dynamicContent);

            System.out.println("Presentación guardada correctamente.");
        } catch (IOException e) {
            System.err.println("Error de I/O al generar la presentación: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }
}