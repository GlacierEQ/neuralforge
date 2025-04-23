package com.cenfotec.p3.neuralforge_api.service;

import com.cenfotec.p3.neuralforge_api.model.entity.ProjectEntity;
import com.cenfotec.p3.neuralforge_api.model.entity.ProjectMaterialEntity;
import com.cenfotec.p3.neuralforge_api.model.entity.UserEntity;
import com.cenfotec.p3.neuralforge_api.model.enums.UserRoleEnum;
import com.cenfotec.p3.neuralforge_api.model.mapper.ProjectMaterialMapper;
import com.cenfotec.p3.neuralforge_api.model.resource.ProjectMaterialResource;
import com.cenfotec.p3.neuralforge_api.repository.ProjectMaterialRepository;
import com.cenfotec.p3.neuralforge_api.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service class for managing project materials.
 *
 * @author Enrique Alpízar
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class ProjectMaterialService {

    @Autowired
    private final ProjectMaterialRepository materialRepository;

    @Autowired
    private final ProjectRepository projectRepository;

    @Autowired
    private final ProjectMaterialMapper materialMapper;


    private final String baseUploadDir = "uploads";
    private final String materialsDir = "materials";

    /**
     * Get the full path to the uploads directory.
     * @return The Path to the uploads directory.
     */
    private Path getUploadPath() {
        return Paths.get(baseUploadDir, materialsDir);
    }

    /**
     * Retrieves all project materials for a specific project.
     *
     * @param projectId The ID of the project.
     * @return A list of project material resources.
     */
    public List<ProjectMaterialResource> getProjectMaterials(String projectId) {
        return materialRepository.findByProjectId(projectId)
                .stream()
                .map(materialMapper::mapToResource)
                .collect(Collectors.toList());
    }

    /**
     * Uploads a new project material.
     *
     * @param file The uploaded file.
     * @param type The type of the material.
     * @param description The description of the material.
     * @param hyperlink The hyperlink of the material.
     * @param projectId The ID of the project.
     * @return The created project material resource.
     */
    public ProjectMaterialResource uploadMaterial(MultipartFile file, String type, String description, String hyperlink, String projectId) {
        if (type == null || (!type.equals("file") && !type.equals("hyperlink"))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid material type. Must be 'file' or 'hyperlink'");
        }

        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found with id: " + projectId));

        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!user.getId().equals(project.getCreatorUserId()) && user.getRole().getName() != UserRoleEnum.ROLE_ADMINISTRATOR){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not allowed to modify this project");
        }

        ProjectMaterialEntity material = ProjectMaterialEntity.builder()
                .type(type)
                .description(description)
                .project(project)
                .createdAt(LocalDateTime.now())
                .build();

        if ("file".equals(type)) {
            if (file == null || file.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File is required for type 'file'");
            }

            String originalFileName = file.getOriginalFilename();
            String uniqueFileName = UUID.randomUUID() + "." + getFileExtension(originalFileName);

            try {
                Path uploadPath = getUploadPath();
                Files.createDirectories(uploadPath);
                Path filePath = uploadPath.resolve(uniqueFileName);
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                material.setFileName(originalFileName);
                material.setFileUrl("/api/neuralforge/v1/project-materials/files/" + uniqueFileName);
            } catch (IOException e) {

                System.err.println("Error storing file: " + e.getMessage());
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to store file");
            }
        } else if ("hyperlink".equals(type)) {
            if (hyperlink == null || hyperlink.isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Hyperlink is required for type 'hyperlink'");
            }
            material.setHyperlink(hyperlink);
        }

        ProjectMaterialEntity savedMaterial = materialRepository.save(material);
        return materialMapper.mapToResource(savedMaterial);
    }

    /**
     * Deletes a project material by its ID.
     *
     * @param materialId The ID of the project material to delete.
     */
    public void deleteMaterial(String materialId) {
        ProjectMaterialEntity material = materialRepository.findById(materialId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Material not found with id: " + materialId));

        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!user.getId().equals(material.getProject().getCreatorUserId()) && user.getRole().getName() != UserRoleEnum.ROLE_ADMINISTRATOR){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not allowed to modify this project");
        }

        if ("file".equals(material.getType()) && material.getFileUrl() != null) {
            try {
                String fileName = material.getFileUrl().substring(material.getFileUrl().lastIndexOf("/") + 1);
                Path filePath = getUploadPath().resolve(fileName);
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                System.err.println("Error deleting file: " + e.getMessage());
            }
        }

        materialRepository.delete(material);
    }

    public ProjectMaterialResource createProjectMaterial(ProjectMaterialResource material) {
        ProjectEntity project = projectRepository.findById(material.getProjectId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));

        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!user.getId().equals(project.getCreatorUserId()) && user.getRole().getName() != UserRoleEnum.ROLE_ADMINISTRATOR){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not allowed to modify this project");
        }

        ProjectMaterialEntity entity = materialMapper.mapToEntity(material);
        ProjectMaterialEntity savedEntity = materialRepository.save(entity);
        return materialMapper.mapToResource(savedEntity);
    }

    public ProjectMaterialResource updateProjectMaterial(String id, ProjectMaterialResource material) {
        ProjectMaterialEntity existingMaterial = materialRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Material not found"));

        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!user.getId().equals(existingMaterial.getProject().getCreatorUserId()) && user.getRole().getName() != UserRoleEnum.ROLE_ADMINISTRATOR){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not allowed to modify this project");
        }

        existingMaterial.setDescription(material.getDescription());

        // Only update hyperlink for hyperlink type materials
        if ("hyperlink".equals(existingMaterial.getType()) && material.getHyperlink() != null) {
            existingMaterial.setHyperlink(material.getHyperlink());
        }

        ProjectMaterialEntity updatedMaterial = materialRepository.save(existingMaterial);
        return materialMapper.mapToResource(updatedMaterial);
    }

    public ProjectMaterialResource getProjectMaterial(String id) {
        ProjectMaterialEntity material = materialRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Material not found"));

        return materialMapper.mapToResource(material);
    }

    public List<ProjectMaterialResource> getAllProjectMaterials() {
        return materialRepository.findAll()
                .stream()
                .map(materialMapper::mapToResource)
                .collect(Collectors.toList());
    }

    /**
     * Securely downloads a file by material ID, verifying the user has access rights.
     * Checks if the user is either an admin or the owner of the project.
     *
     * @param materialId The ID of the material to download.
     * @return A ResponseEntity containing the file as a resource.
     */
    public ResponseEntity<Resource> downloadMaterialFile(String materialId) {
        // Get the material
        ProjectMaterialEntity material = materialRepository.findById(materialId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Material not found with id: " + materialId));

        // Verify material type is file
        if (!"file".equals(material.getType()) || material.getFileUrl() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Material is not a file");
        }

        // Get current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserEntity currentUser = (UserEntity) authentication.getPrincipal();

        // Check if user is admin
        boolean isAdmin = currentUser.getRole().getName() == UserRoleEnum.ROLE_ADMINISTRATOR;

        // If not admin, check if user is the project owner
        if (!isAdmin) {
            // Get the project
            ProjectEntity project = material.getProject();

            // Check if current user is the owner of the project
            if (!project.getCreatorUserId().equals(currentUser.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission to download this file");
            }
        }

        // User is authorized to download the file
        try {
            // Extract the filename from the fileUrl
            String fileName = material.getFileUrl().substring(material.getFileUrl().lastIndexOf("/") + 1);

            // First try default path
            Path filePath = getUploadPath().resolve(fileName);
            File fileObject = filePath.toFile();

            if (!fileObject.exists()) {
                 throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found. It may have been deleted or moved.");
            }

            // Create resource from the file object
            Resource resource = new UrlResource(fileObject.toURI());

            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "attachment; filename=\"" + material.getFileName() + "\"")
                        .body(resource);
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "File found but cannot be read. Please contact system administrator.");
            }
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error accessing file. Please try again later.");
        }
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf(".") == -1) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }
}