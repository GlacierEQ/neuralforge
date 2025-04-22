package com.cenfotec.p3.neuralforge_api.controller;

import com.cenfotec.p3.neuralforge_api.model.request.GenerateContentRequest;
import com.cenfotec.p3.neuralforge_api.model.resource.DynamicContentResource;
import com.cenfotec.p3.neuralforge_api.service.DynamicContentService;
import com.cenfotec.p3.neuralforge_api.service.ConceptMapContentService;
import com.cenfotec.p3.neuralforge_api.service.PPTContentService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * Controller for handling operations related to dynamic content generation and management.
 * This controller provides endpoints for generating, retrieving, deleting, and downloading dynamic content.
 * Supported content types include summaries, concept maps, and slide decks.
 *
 * @author Fabian Vargas
 * @version 1.1
 */
@RestController
@RequestMapping("DynamicContent")
public class DynamicContentController {

    private final DynamicContentService dynamicContentService;

    /**
     * Constructor for the DynamicContentController.
     *
     * @param dynamicContentService Service used for processing summary generation logic.
     * @param conceptMapService Service used for generating concept maps (not used in this class).
     * @param pptContentService Service used for generating PowerPoint presentations (not used in this class).
     */
    public DynamicContentController(DynamicContentService dynamicContentService, ConceptMapContentService conceptMapService, PPTContentService pptContentService) {
        this.dynamicContentService = dynamicContentService;
    }

    /**
     * Retrieves a list of dynamic content resources associated with a specific project.
     *
     * @param projectId The ID of the project for which to retrieve content.
     * @return A ResponseEntity containing a list of DynamicContentResource objects.
     */
    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<DynamicContentResource>> getByProjectId(@PathVariable String projectId) {
        List<DynamicContentResource> contents = dynamicContentService.getByProjectId(projectId);
        return ResponseEntity.ok(contents);
    }

    /**
     * Generates dynamic content based on the provided request.
     *
     * @param request The request object containing details for content generation, such as project ID, material ID, title, type, and language.
     * @return A ResponseEntity with an HTTP 200 status if the content generation is successful.
     */
    @PostMapping("/generate")
    public ResponseEntity<Void> generateContent(@RequestBody GenerateContentRequest request) {
        dynamicContentService.generateContent(
                request.getProjectId(),
                request.getMaterialId(),
                request.getTitle(),
                request.getType(),
                request.getLanguage()
        );
        return ResponseEntity.ok().build();
    }

    /**
     * Deletes a specific dynamic content resource by its ID.
     *
     * @param contentId The ID of the content to delete.
     * @return A ResponseEntity with an HTTP 204 status if the deletion is successful.
     */
    @DeleteMapping("/{contentId}")
    public ResponseEntity<Void> deleteContent(@PathVariable String contentId) {
        dynamicContentService.deleteDynamicContent(contentId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Downloads a specific dynamic content resource by its ID.
     *
     * @param contentId The ID of the content to download.
     * @return A ResponseEntity containing the content as a downloadable resource, or an HTTP 500 status if an error occurs.
     */
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