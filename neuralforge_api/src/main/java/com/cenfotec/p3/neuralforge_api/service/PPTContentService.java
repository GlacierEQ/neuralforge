package com.cenfotec.p3.neuralforge_api.service;

import com.cenfotec.p3.neuralforge_api.model.entity.DynamicContentEntity;
import com.cenfotec.p3.neuralforge_api.model.enums.DynamicContentTypeEnum;
import com.cenfotec.p3.neuralforge_api.repository.DynamicContentRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.sl.usermodel.PictureData;
import org.apache.poi.xslf.usermodel.*;

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
    public String extractTextAndGeneratePPT(MultipartFile file, String title, String email, String type) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty.");
        }

        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            String extractedText = pdfStripper.getText(document);

            String slideContent = getSlideDeckFromDeepSeek(extractedText);
            saveTextAsPPT(slideContent, title, email, type);
            return "Slide deck generated and saved successfully.";
        }
    }

    /**
     * Calls the DeepSeek API to generate a structured slide deck from the extracted text.
     *
     * @param text The extracted text to be converted into slides.
     * @return The structured slide content.
     */
    private String getSlideDeckFromDeepSeek(String text) {
        String instructions = """
            Genera una presentación en texto siguiendo este formato:
            - Cada diapositiva comienza con "Slide X:"
            - El título de la diapositiva empieza con "Title: "
            - Los puntos clave de la diapositiva empiezan con "- "
            - NO uses bloques de código ni etiquetas Markdown.
            - NO agregues texto extra ni explicaciones, solo devuelve el contenido en el formato indicado.
            - Resumir información de manera efectiva para una presentación didáctica.
            - Que lo que coloques tenga sentido con el tema, esta bien si quitas texto que no sea relevante.
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
     * Saves the structured slide content as a PowerPoint presentation.
     *
     * @param content The structured slide content.
     * @param title   The title of the presentation.
     * @param email   The email of the user.
     * @param type    The type of content.
     */
    private void saveTextAsPPT(String content, String title, String email, String type) {
        try {
            File directory = new File("src/main/resources/dynamicContent/");
            if (!directory.exists() && !directory.mkdirs()) {
                System.err.println("Error: Could not create directory " + directory.getAbsolutePath());
                return;
            }

            File pptFile = new File(directory, title + ".pptx");

            try (XMLSlideShow ppt = new XMLSlideShow(); FileOutputStream out = new FileOutputStream(pptFile)) {
                // Establecer las dimensiones de la diapositiva en 16:9 (1920x1080 píxeles)
                ppt.setPageSize(new java.awt.Dimension(1920, 1080));

                // Ruta de las imágenes de fondo
                File coverImageFile = new File("src/main/resources/images/mainBackground.png"); // Fondo para la primera diapositiva
                File contentImageFile = new File("src/main/resources/images/finalBackground.png"); // Fondo para las demás

                // Crear PictureData a partir de los archivos de imagen
                PictureData coverPictureData = ppt.addPicture(coverImageFile, XSLFPictureData.PictureType.PNG);
                PictureData contentPictureData = ppt.addPicture(contentImageFile, XSLFPictureData.PictureType.PNG);

                // Crear la primera diapositiva (portada)
                XSLFSlide coverSlide = ppt.createSlide();
                XSLFPictureShape coverPictureShape = coverSlide.createPicture(coverPictureData);
                coverPictureShape.setAnchor(new java.awt.Rectangle(0, 0, 1920, 1080));

                XSLFTextBox coverTitleBox = coverSlide.createTextBox();
                coverTitleBox.setAnchor(new java.awt.Rectangle(400, 400, 1120, 200));
                XSLFTextParagraph coverTitleParagraph = coverTitleBox.addNewTextParagraph();
                XSLFTextRun coverTitleRun = coverTitleParagraph.addNewTextRun();
                coverTitleRun.setText(title);
                coverTitleRun.setBold(true);
                coverTitleRun.setFontSize(100.0);
                coverTitleRun.setFontFamily("Arial");
                coverTitleRun.setFontColor(new java.awt.Color(255, 255, 255));

                String[] slides = content.split("Slide \\d+:");

                for (String slideContent : slides) {
                    if (slideContent.trim().isEmpty()) continue;

                    XSLFSlide slide = ppt.createSlide();
                    XSLFPictureShape pictureShape = slide.createPicture(contentPictureData);
                    pictureShape.setAnchor(new java.awt.Rectangle(0, 0, 1920, 1080));

                    String[] lines = slideContent.trim().split("\\n");

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

                    XSLFTextBox contentBox = slide.createTextBox();
                    contentBox.setAnchor(new java.awt.Rectangle(500, 250, 1820, 800));

                    for (int i = 1; i < lines.length; i++) {
                        if (lines[i].startsWith("-")) {
                            XSLFTextParagraph bulletParagraph = contentBox.addNewTextParagraph();
                            bulletParagraph.setBullet(true);
                            XSLFTextRun bulletRun = bulletParagraph.addNewTextRun();
                            bulletRun.setText(lines[i].replace("-", "").trim());
                            bulletRun.setFontSize(44.0);
                            bulletRun.setFontFamily("Arial");
                            bulletRun.setFontColor(new java.awt.Color(255, 255, 255));
                            bulletParagraph.setSpaceAfter(25.0);
                        } else {
                            XSLFTextParagraph textParagraph = contentBox.addNewTextParagraph();
                            textParagraph.setLineSpacing(18.0);
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

            if (!pptFile.exists() || pptFile.length() == 0) {
                System.err.println("Error: Presentation was not generated correctly.");
                return;
            }

            System.out.println("Presentation generated successfully at: " + pptFile.getAbsolutePath());

            DynamicContentEntity dynamicContent = new DynamicContentEntity();
            dynamicContent.setTitle(title);
            dynamicContent.setPath(pptFile.getAbsolutePath());
            dynamicContent.setEmail(email);
            dynamicContent.setType(DynamicContentTypeEnum.valueOf(type));
            dynamicContent.setCreationDate(LocalDateTime.now());

            dynamicContentRepository.save(dynamicContent);

            System.out.println("Presentation saved successfully.");
        } catch (IOException e) {
            System.err.println("I/O error while generating the presentation: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }

}