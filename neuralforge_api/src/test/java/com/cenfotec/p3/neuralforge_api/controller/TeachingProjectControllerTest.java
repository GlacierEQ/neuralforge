package com.cenfotec.p3.neuralforge_api.controller;

import com.cenfotec.p3.neuralforge_api.model.resource.TeachingProjectResource;
import com.cenfotec.p3.neuralforge_api.service.TeachingProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test class for TeachingProjectController.
 * 
 * @author Enrique Alpízar
 * @version 1.0
 */
class TeachingProjectControllerTest {

    @Mock
    private TeachingProjectService teachingProjectService;

    @InjectMocks
    private TeachingProjectController teachingProjectController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createTeachingProject_Success() {
        // Arrange
        TeachingProjectResource resource = new TeachingProjectResource();
        when(teachingProjectService.createTeachingProject(any())).thenReturn(resource);

        // Act
        ResponseEntity<TeachingProjectResource> response = teachingProjectController.createTeachingProject(resource);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(teachingProjectService).createTeachingProject(resource);
    }

    @Test
    void updateTeachingProject_Success() {
        // Arrange
        Long id = 1L;
        TeachingProjectResource resource = new TeachingProjectResource();
        when(teachingProjectService.updateTeachingProject(eq(id), any())).thenReturn(resource);

        // Act
        ResponseEntity<TeachingProjectResource> response = teachingProjectController.updateTeachingProject(id, resource);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(teachingProjectService).updateTeachingProject(id, resource);
    }

    @Test
    void getTeachingProject_Success() {
        // Arrange
        Long id = 1L;
        TeachingProjectResource resource = new TeachingProjectResource();
        when(teachingProjectService.getTeachingProject(id)).thenReturn(resource);

        // Act
        ResponseEntity<TeachingProjectResource> response = teachingProjectController.getTeachingProject(id);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(teachingProjectService).getTeachingProject(id);
    }

    @Test
    void getAllTeachingProjects_Success() {
        // Arrange
        List<TeachingProjectResource> resources = Arrays.asList(new TeachingProjectResource());
        when(teachingProjectService.getAllTeachingProjects()).thenReturn(resources);

        // Act
        ResponseEntity<List<TeachingProjectResource>> response = teachingProjectController.getAllTeachingProjects();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(teachingProjectService).getAllTeachingProjects();
    }

    @Test
    void deleteTeachingProject_Success() {
        // Arrange
        Long id = 1L;
        doNothing().when(teachingProjectService).deleteTeachingProject(id);

        // Act
        ResponseEntity<Void> response = teachingProjectController.deleteTeachingProject(id);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(teachingProjectService).deleteTeachingProject(id);
    }

    @Test
    void uploadFile_Success() throws IOException {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "test.txt",
            "text/plain",
            "test content".getBytes()
        );
        String fileName = "test_file.txt";
        when(teachingProjectService.uploadFile(any())).thenReturn(fileName);

        // Act
        ResponseEntity<String> response = teachingProjectController.uploadFile(file);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(fileName, response.getBody());
        verify(teachingProjectService).uploadFile(file);
    }

    @Test
    void uploadFile_Failure() throws IOException {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "test.txt",
            "text/plain",
            "test content".getBytes()
        );
        when(teachingProjectService.uploadFile(any())).thenThrow(new IOException("Upload failed"));

        // Act
        ResponseEntity<String> response = teachingProjectController.uploadFile(file);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().contains("Failed to upload file"));
        verify(teachingProjectService).uploadFile(file);
    }

    @Test
    void deleteFile_Success() throws IOException {
        // Arrange
        String fileName = "test.txt";
        doNothing().when(teachingProjectService).deleteFile(fileName);

        // Act
        ResponseEntity<Void> response = teachingProjectController.deleteFile(fileName);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(teachingProjectService).deleteFile(fileName);
    }

    @Test
    void deleteFile_Failure() throws IOException {
        // Arrange
        String fileName = "test.txt";
        doThrow(new IOException("Delete failed")).when(teachingProjectService).deleteFile(fileName);

        // Act
        ResponseEntity<Void> response = teachingProjectController.deleteFile(fileName);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(teachingProjectService).deleteFile(fileName);
    }
} 