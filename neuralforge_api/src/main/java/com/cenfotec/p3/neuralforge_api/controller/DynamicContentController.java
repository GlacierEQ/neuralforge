package com.cenfotec.p3.neuralforge_api.controller;

import com.cenfotec.p3.neuralforge_api.service.DynamicContentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Controller for handling operations related to generating and processing summaries.
 * This controller exposes an endpoint for uploading a file, extracting text,
 * and generating a PDF summary based on the provided details.
 *
 * @author Fabian Vargas
 * @version 1.0
 */
@RestController
@RequestMapping("/DynamicContent")
public class DynamicContentController {

    private final DynamicContentService summaryService;

    /**
     * Constructor for the SummaryController.
     *
     * @param summaryService Service used for processing summary generation logic.
     */
    public DynamicContentController(DynamicContentService summaryService) {
        this.summaryService = summaryService;
    }

    /**
     * Endpoint for extracting text from a file and generating a PDF summary.
     * This endpoint handles multipart form data and processes the provided file,
     * title, email, and type to generate a summary.
     *
     * @param file The file to be processed (in multipart form).
     * @param title The title of the summary.
     * @param email The email of the user submitting the summary.
     * @param type The type of content for the summary.
     * @return A response containing a success message or error details.
     */
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<String> extractTextAndGeneratePdf(@RequestParam("file") MultipartFile file,
                                                            @RequestParam("title") String title,
                                                            @RequestParam("email") String email,
                                                            @RequestParam("type") String type) {
        try {
            // Calls the service method to extract text from the file and generate the PDF.
            String responseMessage = summaryService.extractTextAndGeneratePdf(file, title, email, type);
            return ResponseEntity.ok(responseMessage);  // Return success response.
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());  // Return bad request response if argument is invalid.
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al procesar el archivo PDF.");  // Return internal server error if file processing fails.
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error inesperado: " + e.getMessage());  // Return internal server error for unexpected issues.
        }
    }
}