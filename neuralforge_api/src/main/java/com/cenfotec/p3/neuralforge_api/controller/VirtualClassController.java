package com.cenfotec.p3.neuralforge_api.controller;

import com.cenfotec.p3.neuralforge_api.model.resource.VirtualClassResource;
import com.cenfotec.p3.neuralforge_api.service.VirtualClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/virtual-classes")
public class VirtualClassController {

    @Autowired
    private VirtualClassService virtualClassService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMINISTRATOR', 'ROLE_TEACHER')")
    public ResponseEntity<List<VirtualClassResource>> getAll() {
        return ResponseEntity.status(HttpStatus.OK).body(virtualClassService.getAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMINISTRATOR', 'ROLE_TEACHER', 'ROLE_STUDENT')")
    public ResponseEntity<VirtualClassResource> getById(@PathVariable String id) {
        return ResponseEntity.status(HttpStatus.OK).body(virtualClassService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMINISTRATOR', 'ROLE_TEACHER')")
    public ResponseEntity<VirtualClassResource> create(@RequestBody VirtualClassResource resource) {
        return ResponseEntity.status(HttpStatus.CREATED).body(virtualClassService.create(resource));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMINISTRATOR', 'ROLE_TEACHER')")
    public ResponseEntity<VirtualClassResource> update(@PathVariable String id, @RequestBody VirtualClassResource resource) {
        return ResponseEntity.status(HttpStatus.OK).body(virtualClassService.update(id, resource));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMINISTRATOR', 'ROLE_TEACHER')")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        virtualClassService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/{classId}/{studentId}")
    @PreAuthorize("hasAnyRole('ROLE_TEACHER', 'ROLE_ADMINISTRATOR')")
    public ResponseEntity<Void> addStudentToClass(@PathVariable String classId, @PathVariable String studentId) {
        virtualClassService.addStudentToClass(classId, studentId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/my")
    public ResponseEntity<List<VirtualClassResource>> getMyClasses() {
        List<VirtualClassResource> myClasses = virtualClassService.getMyClasses();
        return ResponseEntity.ok(myClasses);
    }


}

