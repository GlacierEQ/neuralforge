package com.cenfotec.p3.neuralforge_api.controller;

import com.cenfotec.p3.neuralforge_api.service.SummaryContentService;
import com.cenfotec.p3.neuralforge_api.service.ConceptMapContentService;
import com.cenfotec.p3.neuralforge_api.service.PPTContentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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

    private final SummaryContentService summaryService;
    private final ConceptMapContentService conceptMapService;
    private final PPTContentService pptContentService;

    /**
     * Constructor for the DynamicContentController.
     *
     * @param summaryService Service used for processing summary generation logic.
     * @param conceptMapService Service used for generating concept maps.
     * @param pptContentService Service used for generating PowerPoint slide decks.
     */
    public DynamicContentController(SummaryContentService summaryService, ConceptMapContentService conceptMapService, PPTContentService pptContentService) {
        this.summaryService = summaryService;
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
                                                            @RequestParam("type") String type,
                                                            @RequestParam("projectId") String projectId) {
        try {
            String responseMessage = summaryService.extractTextAndGeneratePdf(file, title, email, type, projectId);
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
}