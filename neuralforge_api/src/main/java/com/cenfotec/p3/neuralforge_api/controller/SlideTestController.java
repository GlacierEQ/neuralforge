package com.cenfotec.p3.neuralforge_api.controller;

import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;

@RestController
@RequestMapping("/auth/concept-map")
public class SlideTestController {

    private static final String DEEPSEEK_API_URL = "https://api.deepseek.com/chat/completions";
    private static final String MODEL_NAME = "deepseek-chat";
    private static final String BEARER_TOKEN = "sk-44fe8f6278c545f982817d175934b260";

    @PostMapping(consumes = "multipart/form-data", produces = "application/pdf")
    public ResponseEntity<byte[]> generateConceptMap(@RequestParam("file") MultipartFile file,
                                                     @RequestParam("title") String title) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            String extractedText = pdfStripper.getText(document);

            String instructions = """
                    Analiza el siguiente texto y genera un mapa conceptual en formato PlantUML.\s
                    Sigue estas reglas:
                    
                    - Usa "node" para representar los conceptos principales.
                    - Usa "-->" para indicar relaciones entre conceptos.
                    - Agrupa conceptos similares en subgrupos si es necesario usando "node".
                    - Cada relación debe conectar dos conceptos, no más ni menos.
                    - No agregues comentarios adicionales, solo genera el formato correcto en PlantUML.
                    - Incluye las directivas `@startuml` al principio y `@enduml` al final del código generado.
                    - Si se hace referencia a un concepto, debe ser representado como un "node".
                    - Las relaciones deben ser claras y concisas, y reflejar la estructura lógica del texto.
                    - No utilices ningún otro tipo de formato o sintaxis fuera de "node" y "-->".
                    
                    Ejemplo de formato esperado:
                    
                    @startuml
                    "Proyecto X" {
                        node "Fase 1" {
                            node "Análisis"
                            node "Diseño"
                        }
                        node "Fase 2" {
                            node "Desarrollo"
                            node "Pruebas"
                        }
                    }
                    
                    "Proyecto X" --> "Fase 1"
                    "Proyecto X" --> "Fase 2"
                    "Fase 1" --> "Análisis"
                    "Fase 1" --> "Diseño"
                    "Fase 2" --> "Desarrollo"
                    "Fase 2" --> "Pruebas"
                    @enduml
                    """;

            String conceptMapData = fetchConceptMapFromAPI(extractedText, instructions);
            if (conceptMapData.isEmpty()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }

            System.out.println("PlantUML Generated Content:");
            System.out.println(conceptMapData);

            Path umlFilePath = Files.createTempFile("concept_map", ".puml");
            Files.write(umlFilePath, conceptMapData.getBytes(), StandardOpenOption.CREATE);

            Path imagePath = Files.createTempFile("concept_map", ".png");
            generateImageFromPlantUML(umlFilePath, imagePath);

            byte[] pdfBytes = generatePdfWithImage(imagePath, title);

            HttpHeaders pdfHeaders = new HttpHeaders();
            pdfHeaders.setContentType(MediaType.APPLICATION_PDF);
            pdfHeaders.setContentDisposition(ContentDisposition.builder("attachment").filename(title + ".pdf").build());

            return ResponseEntity.ok().headers(pdfHeaders).body(pdfBytes);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    private String fetchConceptMapFromAPI(String text, String instructions) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", MODEL_NAME);

        Map<String, String> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", instructions + "\n\n" + text);

        requestBody.put("messages", List.of(message));
        requestBody.put("max_tokens", 2048);
        requestBody.put("response_format", Map.of("type", "text"));

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(10000);
        factory.setReadTimeout(30000);
        RestTemplate restTemplate = new RestTemplate(factory);

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

    private void generateImageFromPlantUML(Path umlFile, Path outputImage) throws IOException {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("plantuml", "-tpng", umlFile.toString());
            processBuilder.redirectOutput(outputImage.toFile());
            Process process = processBuilder.start();
            process.waitFor();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Error al generar la imagen con PlantUML", e);
        }
    }

    private byte[] generatePdfWithImage(Path imagePath, String title) throws IOException {
        try (PDDocument pdfDocument = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            pdfDocument.addPage(page);
            PDPageContentStream contentStream = new PDPageContentStream(pdfDocument, page);
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 20);
            contentStream.beginText();
            contentStream.newLineAtOffset(100, 750);
            contentStream.showText(title);
            contentStream.endText();

            PDImageXObject image = PDImageXObject.createFromFile(imagePath.toString(), pdfDocument);
            contentStream.drawImage(image, 50, 100, 500, 600);  // Ajusta la posición y tamaño de la imagen
            contentStream.close();

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            pdfDocument.save(outputStream);
            return outputStream.toByteArray();
        }
    }
}
