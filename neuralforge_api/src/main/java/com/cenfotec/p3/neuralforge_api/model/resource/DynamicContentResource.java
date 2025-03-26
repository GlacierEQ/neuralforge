
package com.cenfotec.p3.neuralforge_api.model.resource;

import java.time.LocalDateTime;

/**
 * Resource representing dynamic content in the system.
 * Exposes only the necessary information about dynamic content.
 *
 * @author Fabian Vargas
 * @version 1.0
 */
public class DynamicContentResource {

    private String id;
    private String title;
    private LocalDateTime creationDate;
    private String path;
    private String email;
    private String type; // Nuevo campo type

    // Getters and Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getEmail() {
        return email; // Obtener el ID del usuario
    }

    public void setEmail(String email) {
        this.email = email; // Establecer solo el ID del usuario
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}