package com.cenfotec.p3.neuralforge_api.service;

import com.cenfotec.p3.neuralforge_api.model.entity.DynamicContentEntity;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import com.cenfotec.p3.neuralforge_api.repository.DynamicContentRepository;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class SummaryService {
    private static final String DEEPSEEK_API_URL = "https://api.deepseek.com/chat/completions";
    private static final String MODEL_NAME = "deepseek-chat";
    private static final String BEARER_TOKEN = "sk-44fe8f6278c545f982817d175934b260";

    @Autowired
    private DynamicContentRepository dynamicContentRepository;

    public String extractTextAndGeneratePdf(MultipartFile file, String title, String email, String type) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("El archivo está vacío.");
        }

        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            String extractedText = pdfStripper.getText(document);

            String summary = getSummaryFromDeepSeek(extractedText);
            savePdf(summary, title, email, type);
            return "PDF generado y guardado correctamente en dinamicContent/";
        }
    }

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
        headers.setBearerAuth(BEARER_TOKEN);

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

    private void savePdf(String content, String title, String email, String type) throws IOException {
        File directory = new File("src/main/resources/dynamicContent/");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File pdfFile = new File(directory, title + ".pdf");

        try (PDDocument pdfDocument = new PDDocument()) {

            // Verificar la existencia del logo
            File logoFile = new File("src/main/resources/images/logo.png");
            if (!logoFile.exists()) {
                System.out.println("Logo no encontrado en la ruta especificada: " + logoFile.getAbsolutePath());
                return;
            }

            // Crear la portada
            PDPage coverPage = new PDPage(new PDRectangle(595.276f, 841.890f)); // A4
            pdfDocument.addPage(coverPage);

            try (PDPageContentStream coverStream = new PDPageContentStream(pdfDocument, coverPage)) {
                // Definir la fuente y márgenes
                PDFont titleFont = PDType1Font.HELVETICA_BOLD;
                float titleSize = 30;
                float pageHeight = coverPage.getMediaBox().getHeight();

                // 🔹 Título más arriba
                float titleY = pageHeight - 200;

                coverStream.beginText();
                coverStream.setFont(titleFont, titleSize);
                float titleWidth = titleFont.getStringWidth(title) / 1000 * titleSize;
                float titleX = (coverPage.getMediaBox().getWidth() - titleWidth) / 2;
                coverStream.newLineAtOffset(titleX, titleY);
                coverStream.showText(title);
                coverStream.endText();

                // 🔹 Agregar logo más centrado
                PDImageXObject logo = PDImageXObject.createFromFile(logoFile.getAbsolutePath(), pdfDocument);
                float logoWidth = 300;
                float logoHeight = 300;
                float logoX = (coverPage.getMediaBox().getWidth() - logoWidth) / 2;
                float logoY = (pageHeight / 2) - (logoHeight / 2) + 60; // 🔹 Centrado en la mitad de la página

                coverStream.drawImage(logo, logoX, logoY, logoWidth, logoHeight);

                // 🔹 Fecha ahora está debajo del logo
                String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
                PDFont dateFont = PDType1Font.HELVETICA;
                float dateSize = 14;

                // Medir el ancho real del texto
                float dateWidth = dateFont.getStringWidth("Fecha de generación: " + date) / 1000 * dateSize;
                float dateX = (coverPage.getMediaBox().getWidth() - dateWidth) / 2;  // 🔹 CENTRADO CORRECTO
                float dateY = logoY - 30; // 🔹 Más espacio entre logo y fecha

                coverStream.beginText();
                coverStream.setFont(dateFont, dateSize);
                coverStream.newLineAtOffset(dateX, dateY);
                coverStream.showText("Fecha de generación: " + date);
                coverStream.endText();
            }

            // Crear la primera página de contenido
            PDPage contentPage = new PDPage(new PDRectangle(595.276f, 841.890f)); // A4
            pdfDocument.addPage(contentPage);

            PDPageContentStream contentStream = new PDPageContentStream(pdfDocument, contentPage);
            contentStream.beginText();  // 🟢 **Asegurar que el texto comienza antes de escribir**
            float marginLeft = 72;  // Margen izquierdo
            float marginRight = 72; // Margen derecho
            float marginTopContent = 769;  // Margen superior (841 - 72)
            float maxWidth = 595 - marginLeft - marginRight;  // Ancho máximo con márgenes
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

                // Detectar y manejar texto en negrita (**) o cursiva (*) dentro de la línea
                StringBuilder formattedLine = new StringBuilder();
                String[] parts = line.split("\\*\\*");  // Divide por doble asterisco `**`
                boolean bold = false;

                // Para detectar negrita y cursiva correctamente
                for (String part : parts) {
                    String[] italicsParts = part.split("\\*");  // Divide por asterisco simple `*`

                    for (String italicPart : italicsParts) {
                        if (bold) {
                            contentStream.setFont(PDType1Font.HELVETICA_BOLD, fontSize);  // Negrita
                            formattedLine.append(italicPart);
                        } else {
                            contentStream.setFont(PDType1Font.HELVETICA, fontSize);  // Normal
                            formattedLine.append(italicPart);
                        }
                        bold = !bold;
                    }
                }

                // Ahora pasamos el texto formateado (con negrita y cursiva procesadas) a la función wrapText
                List<String> wrappedLines = wrapText(formattedLine.toString(), font, fontSize, maxWidth);

                // Aquí es donde procesamos las líneas ya envueltas y las imprimimos en el PDF
                for (String wrappedLine : wrappedLines) {
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
            }

            contentStream.endText();
            contentStream.close();  // 🟢 **Cerrar el stream correctamente**

            // Guardar el PDF en archivo
            pdfDocument.save(pdfFile);
        }

        // Guardar información en la base de datos
        DynamicContentEntity dynamicContent = new DynamicContentEntity();
        dynamicContent.setTitle(title);
        dynamicContent.setPath(pdfFile.getAbsolutePath());
        dynamicContent.setEmail(email);
        dynamicContent.setType(type);
        dynamicContent.setCreationDate(LocalDateTime.now());

        dynamicContentRepository.save(dynamicContent);

        System.out.println("PDF generado y guardado en base de datos.");
        System.out.println("Path: " + pdfFile.getAbsolutePath());
        System.out.println("Title: " + title);
        System.out.println("Email: " + email);
        System.out.println("Type: " + type);
    }



    private List<String> wrapText(String text, PDFont font, float fontSize, float maxWidth) throws IOException {
        List<String> lines = new ArrayList<>();
        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            String testLine = currentLine.length() == 0 ? word : currentLine + " " + word;
            float textWidth = font.getStringWidth(testLine) / 1000 * fontSize;

            if (textWidth > maxWidth) {
                lines.add(currentLine.toString());
                currentLine = new StringBuilder(word);
            } else {
                currentLine.append(" ").append(word);
            }
        }

        if (!currentLine.isEmpty()) {
            lines.add(currentLine.toString());
        }
        return lines;
    }
}