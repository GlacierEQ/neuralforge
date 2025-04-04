package com.cenfotec.p3.neuralforge_api.model.resource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Resource representing dynamic content in the system.
 * Exposes only the necessary information about dynamic content.
 *
 * @author Fabian Vargas
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DynamicContentResource {

    private String id;
    private String title;
    private LocalDateTime creationDate;
    private String path;
    private String email;
    private String type;
    private String projectId;
}