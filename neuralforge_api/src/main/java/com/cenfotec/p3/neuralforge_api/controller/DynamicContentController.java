package com.cenfotec.p3.neuralforge_api.controller;

import com.cenfotec.p3.neuralforge_api.model.request.GenerateContentRequest;
import com.cenfotec.p3.neuralforge_api.model.resource.DynamicContentResource;
import com.cenfotec.p3.neuralforge_api.service.DynamicContentService;
import com.cenfotec.p3.neuralforge_api.service.SummaryContentService;
import com.cenfotec.p3.neuralforge_api.service.ConceptMapContentService;
import com.cenfotec.p3.neuralforge_api.service.PPTContentService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * Controller for handling operations related to generating and processing dynamic content.
 * This controller exposes endpoints for generating summaries, concept maps, and slide decks.
 *
 * @author Fabian Vargas
 * @version 1.1
 */
@RestController
@RequestMapping("auth/DynamicContent")
public class DynamicContentController {
    private final DynamicContentService dynamicContentService;
    // private final SummaryContentService summaryContentService;
    private final ConceptMapContentService conceptMapService;
    private final PPTContentService pptContentService;

    /**
     * Constructor for the DynamicContentController.
     *
     * @param dynamicContentService Service used for processing summary generation logic.
     * @param conceptMapService Service used for generating concept maps.
     * @param pptContentService Service used for generating PowerPoint slide decks.
     */
    public DynamicContentController(DynamicContentService dynamicContentService, ConceptMapContentService conceptMapService, PPTContentService pptContentService) {
        this.dynamicContentService = dynamicContentService;
        this.conceptMapService = conceptMapService;
        this.pptContentService = pptContentService;
    }

    /**
     * Endpoint for extracting text from a file and generating a PDF summary.
     */
    @PostMapping("/generateSummary")
    public ResponseEntity<String> extractTextAndGeneratePdf(@RequestParam("file") MultipartFile file,
                                                            @RequestParam("title") String title,
                                                            @RequestParam("email") String email,
                                                            @RequestParam("type") String type) {
        try {
            String responseMessage = dynamicContentService.extractTextAndGeneratePdf(file, title, email, type);
            return ResponseEntity.ok(responseMessage);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al procesar el archivo PDF.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error inesperado: " + e.getMessage());
        }
    }

    /**
     * Endpoint for extracting text from a file and generating a concept map.
     */
    @PostMapping("/generateConceptMap")
    public ResponseEntity<String> extractTextAndGenerateConceptMap(@RequestParam("file") MultipartFile file,
                                                                   @RequestParam("title") String title,
                                                                   @RequestParam("email") String email,
                                                                   @RequestParam("type") String type) {
        try {
            String responseMessage = conceptMapService.extractTextAndGenerateConceptMap(file, title, email, type);
            return ResponseEntity.ok(responseMessage);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al procesar el archivo PDF.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error inesperado: " + e.getMessage());
        }
    }

    /**
     * Endpoint for extracting text from a file and generating a PowerPoint slide deck.
     */
    @PostMapping("/generatePPT")
    public ResponseEntity<String> extractTextAndGeneratePPT(@RequestParam("file") MultipartFile file,
                                                            @RequestParam("title") String title,
                                                            @RequestParam("email") String email,
                                                            @RequestParam("type") String type) {
        try {
            String responseMessage = pptContentService.extractTextAndGeneratePPT(file, title, email, type);
            return ResponseEntity.ok(responseMessage);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al procesar el archivo PDF.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error inesperado: " + e.getMessage());
        }
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<DynamicContentResource>> getByProjectId(@PathVariable String projectId) {
        List<DynamicContentResource> contents = dynamicContentService.getByProjectId(projectId);
        return ResponseEntity.ok(contents);
    }

    @PostMapping("/generate")
    public ResponseEntity<Void> generateContent(@RequestBody GenerateContentRequest request) {
        dynamicContentService.generateContent(
            request.getProjectId(),
            request.getMaterialId(),
            request.getTitle(),
            request.getType()
        );
        return ResponseEntity.ok().build();
    }

    @GetMapping("/download/{contentId}")
    public ResponseEntity<Resource> downloadContent(@PathVariable String contentId) {
        try {
            Resource resource = dynamicContentService.downloadContent(contentId);
            return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}