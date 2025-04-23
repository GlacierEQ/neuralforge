package com.cenfotec.p3.neuralforge_api.service;

import com.cenfotec.p3.neuralforge_api.model.entity.DynamicContentEntity;
import com.cenfotec.p3.neuralforge_api.model.enums.DynamicContentTypeEnum;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import com.cenfotec.p3.neuralforge_api.repository.DynamicContentRepository;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SummaryContentService {

    private static final String DEEPSEEK_API_URL = "https://api.deepseek.com/chat/completions";
    private static final String MODEL_NAME = "deepseek-chat";

    @Value("${deepseek.api.bearer-token}")
    private String bearerToken;

    private final DynamicContentRepository dynamicContentRepository;

    public SummaryContentService(DynamicContentRepository dynamicContentRepository) {
        this.dynamicContentRepository = dynamicContentRepository;
    }

    public String getSummaryFromDeepSeek(String text, String language) {
        String instructions = """
            Summarize the text using the following rules:
            
            - Use "# " for main titles.
            - Use "## " for subtitles.
            - Use "### " for sub-subtitles.
            - Only use these 3 types of headings—no more.
            - Headings must be processed correctly and not be fragmented.
            - Headings must start with a capital letter, even if preceded by numbering, and should not be cut off.
            - Use "**text**" for bold.
            - Use "*text*" for italic.
            - Use "- " for bullet lists when appropriate.
            - Do not include extra comments or explanations outside the main content.
            - Understand the overall content before summarizing.
            - Use vocabulary appropriate for high school and college students.
            - Maintain a logical and coherent structure. Summarize by sections.
            - Do not skip essential ideas.
            - The summary should contain only essential information but remain well connected.
            - Omit sections such as references, bibliography, and footnotes.
            - Add necessary transitions for clarity without introducing new content.
            - Do not use "---" to separate sections or ANYWHERE IMPORTANT.
            - The output must be in this language: """ + language + """
            
            Example of the expected format (output only, do not include original text):
            
            # Photosynthesis
            
            ## Definition And Importance
            
            **Photosynthesis** is a process where **green plants** and some **organisms** use *sunlight* to convert *carbon dioxide* and *water* into **glucose**. This process is vital for life on Earth because:
            
            - It produces **oxygen** for respiration.
            - It provides **glucose**, the foundation of most food chains.
            
            ## Where It Happens
            
            Photosynthesis occurs in **chloroplasts**, specialized cell structures in plants. These contain **chlorophyll**, a pigment that absorbs sunlight.
            
            ## General Equation
            
            The general photosynthesis equation is:
            
            **6CO₂ + 6H₂O + light energy → C₆H₁₂O₆ + 6O₂**
            
            ### Key Concepts
            
            - **Carbon dioxide** and **water** are the inputs.
            - **Light energy** is essential for the reaction.
            - **Oxygen** is produced as a *byproduct*.
            """;

        List<String> fragments = splitTextIntoChunks(text, 20000);
        StringBuilder fullSummary = new StringBuilder();

        System.out.println("Starting the summarization process...");

        for (int i = 0; i < fragments.size(); i++) {
            List<Map<String, String>> messages = new ArrayList<>();

            if (i == 0) {
                messages.add(Map.of("role", "user", "content", instructions + "\n\n" + fragments.get(0)));
            } else {
                messages.add(Map.of("role", "assistant", "content", fullSummary.toString().trim()));
                messages.add(Map.of("role", "user", "content",
                        "Just continue the following fragment of the text, maintaining the previous style, do not use (---) to separate sections or ANYWHERE, do not add any extra explanation. These are the instructions of the whole summary:\n\n"
                                + instructions + "\n\n" + fragments.get(i)));
            }

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", MODEL_NAME);
            requestBody.put("messages", messages);
            requestBody.put("max_tokens", 2048);

            RestTemplate restTemplate = new RestTemplate(new SimpleClientHttpRequestFactory());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(bearerToken);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            try {
                System.out.println("Sending request for fragment " + (i + 1) + "...");
                ResponseEntity<Map> response = restTemplate.exchange(DEEPSEEK_API_URL, HttpMethod.POST, entity, Map.class);
                System.out.println("Response received for fragment " + (i + 1));

                if (response.getBody() != null && response.getBody().containsKey("choices")) {
                    List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
                    if (choices != null && !choices.isEmpty()) {
                        Map<String, Object> messageResponse = (Map<String, Object>) choices.get(0).get("message");
                        if (messageResponse != null && messageResponse.containsKey("content")) {
                            String summary = (String) messageResponse.get("content");
                            fullSummary.append(summary).append("\n\n");
                            System.out.println("Fragment " + (i + 1) + " processed successfully.");
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "Error connecting to DeepSeek: " + e.getMessage();
            }
        }

        System.out.println("Final summary generated.");
        return fullSummary.toString().trim();
    }

    private List<String> splitTextIntoChunks(String text, int maxLength) {
        List<String> chunks = new ArrayList<>();
        String[] paragraphs = text.split("\n");

        StringBuilder currentChunk = new StringBuilder();
        for (String para : paragraphs) {
            if (currentChunk.length() + para.length() > maxLength) {
                chunks.add(currentChunk.toString());
                currentChunk = new StringBuilder();
            }
            currentChunk.append(para).append("\n");
        }
        if (!currentChunk.isEmpty()) {
            chunks.add(currentChunk.toString());
        }
        return chunks;
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

    public void savePdf(String content, String title, String email, String type, String projectId) throws IOException {
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

            PDFont fontRegular = PDType0Font.load(pdfDocument, new File("src/main/resources/dejavu-fonts-ttf-2.37/ttf/DejaVuSerif.ttf"));
            PDFont fontBold = PDType0Font.load(pdfDocument, new File("src/main/resources/dejavu-fonts-ttf-2.37/ttf/DejaVuSerif-Bold.ttf"));
            PDFont fontItalic = PDType0Font.load(pdfDocument, new File("src/main/resources/dejavu-fonts-ttf-2.37/ttf/DejaVuSerif-Italic.ttf"));
            PDFont fontBoldItalic = PDType0Font.load(pdfDocument, new File("src/main/resources/dejavu-fonts-ttf-2.37/ttf/DejaVuSerif-BoldItalic.ttf"));

            PDPage coverPage = new PDPage(new PDRectangle(595.276f, 841.890f)); // A4
            pdfDocument.addPage(coverPage);

            try (PDPageContentStream coverStream = new PDPageContentStream(pdfDocument, coverPage)) {
                float titleSize = 24;
                float pageHeight = coverPage.getMediaBox().getHeight();
                float titleY = pageHeight - 200;

                coverStream.beginText();
                coverStream.setFont(fontRegular, titleSize);
                float titleWidth = fontRegular.getStringWidth(title) / 1000 * titleSize;
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
                float dateSize = 14;
                float dateWidth = fontRegular.getStringWidth("Generation Date: " + date) / 1000 * dateSize;
                float dateX = (coverPage.getMediaBox().getWidth() - dateWidth) / 2;
                float dateY = logoY - 30;

                coverStream.beginText();
                coverStream.setFont(fontRegular, dateSize);
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
            float marginBottom = 72;
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

                float fontSize = 12;
                if (line.startsWith("# ")) {
                    fontSize = 18;
                    line = line.substring(2);
                } else if (line.startsWith("## ")) {
                    fontSize = 14;
                    line = line.substring(3);
                } else if (line.startsWith("### ")) {
                    fontSize = 12;
                    line = line.substring(4);
                } else if (line.startsWith("#### ")) {
                    fontSize = 12;
                    line = line.substring(5);
                } else if (line.startsWith("##### ")) {
                    fontSize = 12;
                    line = line.substring(6);
                } else if (line.startsWith("- ")) {
                    line = "• " + line.substring(2);
                }

                String wrappedText = wrapText(line, fontRegular, fontSize, maxWidth);
                String[] lines = wrappedText.split("\n");

                for (String textLine : lines) {
                    Matcher matcher = Pattern.compile("(\\*\\*[^*]+\\*\\*|\\*[^*]+\\*|[^*]+)").matcher(textLine);
                    while (matcher.find()) {
                        String segment = matcher.group();
                        PDFont fontToUse = fontRegular;
                        float textWidth;

                        if (segment.startsWith("**") && segment.endsWith("**")) {
                            fontToUse = fontBold;
                            segment = segment.substring(2, segment.length() - 2);
                        } else if (segment.startsWith("*") && segment.endsWith("*")) {
                            fontToUse = fontItalic;
                            segment = segment.substring(1, segment.length() - 1);
                        }

                        contentStream.setFont(fontToUse, fontSize);
                        textWidth = fontToUse.getStringWidth(segment) / 1000 * fontSize;
                        contentStream.showText(segment);
                    }

                    currentHeight -= lineHeight;
                    contentStream.newLineAtOffset(0, -lineHeight);

                    if (currentHeight < marginBottom) {
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

    public String wrapText(String text, PDFont font, float fontSize, float maxWidth) throws IOException {
        StringBuilder result = new StringBuilder();
        String cleanText = text.replace("\n", " ").replace("\r", " ");
        String[] words = cleanText.split(" ");
        StringBuilder line = new StringBuilder();

        for (String word : words) {
            String testLine = line.length() > 0 ? line + " " + word : word;
            float width = font.getStringWidth(testLine) * fontSize / 1000f;

            if (width <= maxWidth) {

                line = new StringBuilder(testLine);
            } else {

                if (font.getStringWidth(word) * fontSize / 1000f > maxWidth) {

                    while (font.getStringWidth(word) * fontSize / 1000f > maxWidth) {
                        int splitIndex = word.length() / 2;
                        result.append(word.substring(0, splitIndex) + "\n");
                        word = word.substring(splitIndex);
                    }
                    line = new StringBuilder(word);
                } else {

                    if (line.length() > 0) {
                        result.append(line).append("\n");
                    }
                    line = new StringBuilder(word);
                }
            }
        }


        if (line.length() > 0) {
            result.append(line);
        }

        return result.toString();
    }
}