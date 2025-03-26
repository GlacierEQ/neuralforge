package com.cenfotec.p3.neuralforge_api.controller;

import com.cenfotec.p3.neuralforge_api.service.SummaryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/auth/summary")
public class SummaryController {

    private final SummaryService summaryService;

    public SummaryController(SummaryService summaryService) {
        this.summaryService = summaryService;
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<String> extractTextAndGeneratePdf(@RequestParam("file") MultipartFile file,
                                                            @RequestParam("title") String title,
                                                            @RequestParam("email") String email,
                                                            @RequestParam("type") String type) {
        try {
            String responseMessage = summaryService.extractTextAndGeneratePdf(file, title, email, type);
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