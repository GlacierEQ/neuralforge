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
            Generate a text-based presentation following this format:
            - Each slide starts with "Slide X:"
            - The slide title starts with "Title: "
            - Key points start with "- "
            - DO NOT use code blocks or Markdown tags.
            - Slide titles must be no longer than 30 characters.
            - DO NOT add extra text or explanations, only return the content in the format specified.
            - Summarize information effectively for an educational presentation.
            - Make sure the content makes sense for the topic; it's okay to omit irrelevant text.
            - The content must be written in the following language: """ + language + """
            - DO NOT include sub-points or bullet points within bullet points.
            - Each slide should contain a maximum of 5 key points.
            - Each key point must be no longer than 60 characters.
        
            Example of expected format:
        
            Slide 1:
            Title: Introduction to Java
            - Overview of Java programming language
            - Key features: object-oriented, platform-independent
            - Widely used for web and mobile development
            - Strong community support
            - High performance and scalability
        
            Slide 2:
            Title: Java Syntax Basics
            - Classes and objects are fundamental
            - Java uses curly braces for code blocks
            - Statements end with semicolons
            - Case-sensitive language
            - Commonly used for backend development
        
            Slide 3:
            Title: Control Structures
            - If-else: Used for conditional branching
            - Switch: Simplifies multiple conditions
            - Loops: for, while, and do-while for repetition
            - Break and continue control loop flow
            - Nested loops for complex operations
        
            Slide 4:
            Title: Object-Oriented Concepts
            - Encapsulation: Hiding data in classes
            - Inheritance: Reuse code with subclasses
            - Polymorphism: Method variety in classes
            - Abstraction: Hide complexity with simplicity
            - Interfaces: Define contracts for classes
        
            Slide 5:
            Title: Conclusions
            - Java is versatile and widely used
            - Strong support for object-oriented design
            - Great for web, mobile, enterprise apps
            - Large library and community ecosystem
            - Suitable for scalable applications
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
    public void saveTextAsPPT(String content, String title, String email, String type, String projectId) {
        try {
            File directory = new File("src/main/resources/dynamicContent/");
            if (!directory.exists() && !directory.mkdirs()) {
                System.err.println("Error: Could not create the directory. " + directory.getAbsolutePath());
                return;
            }

            File pptFile = new File(directory, title + ".pptx");

            if (pptFile.exists() && pptFile.length() > 0) {
                System.out.println("The file already exists and contains content. It will be overwritten.");
            }

            try (XMLSlideShow ppt = new XMLSlideShow(); FileOutputStream out = new FileOutputStream(pptFile)) {

                ppt.setPageSize(new java.awt.Dimension(1920, 1080));

                File coverImageFile = new File("src/main/resources/images/mainBackground.png");
                File contentImageFile = new File("src/main/resources/images/finalBackground.png");

                PictureData coverPictureData = ppt.addPicture(coverImageFile, XSLFPictureData.PictureType.PNG);
                PictureData contentPictureData = ppt.addPicture(contentImageFile, XSLFPictureData.PictureType.PNG);

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
                    contentBox.setAnchor(new java.awt.Rectangle(500, 350, 1220, 700));

                    for (int i = 1; i < lines.length; i++) {
                        if (lines[i].startsWith("-")) {
                            XSLFTextParagraph bulletParagraph = contentBox.addNewTextParagraph();
                            bulletParagraph.setBullet(true);
                            bulletParagraph.setSpaceAfter(35.0);
                            XSLFTextRun bulletRun = bulletParagraph.addNewTextRun();
                            bulletRun.setText(" " + lines[i].replace("-", "").trim());
                            bulletRun.setFontSize(44.0);
                            bulletRun.setFontFamily("Arial");
                            bulletRun.setFontColor(new java.awt.Color(255, 255, 255));
                        } else {
                            XSLFTextParagraph textParagraph = contentBox.addNewTextParagraph();
                            textParagraph.setLineSpacing(28.0);
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

            if (!pptFile.exists() || pptFile.length() == 0) {
                System.err.println("Error: The presentation was not generated correctly.");
                return;
            }

            System.out.println("Presentation successfully generated at: " + pptFile.getAbsolutePath());

            DynamicContentEntity dynamicContent = new DynamicContentEntity();
            dynamicContent.setTitle(title);
            dynamicContent.setPath(pptFile.getAbsolutePath());
            dynamicContent.setEmail(email);
            dynamicContent.setType(DynamicContentTypeEnum.valueOf(type));
            dynamicContent.setCreationDate(LocalDateTime.now());
            dynamicContent.setProjectId(projectId);

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