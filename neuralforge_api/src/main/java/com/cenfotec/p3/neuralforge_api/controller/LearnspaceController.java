
package com.cenfotec.p3.neuralforge_api.controller;

import com.cenfotec.p3.neuralforge_api.model.request.CreateLearnspaceRequest;
import com.cenfotec.p3.neuralforge_api.model.resource.LearnspaceResource;
import com.cenfotec.p3.neuralforge_api.service.LearnspaceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/neuralforge/v1/learnspaces")
public class LearnspaceController {

    @Autowired
    private LearnspaceService learnspaceService;

    @PostMapping
    public ResponseEntity<LearnspaceResource> crearLearnspace(@Valid @RequestBody CreateLearnspaceRequest request) {
        return ResponseEntity.ok(learnspaceService.crearLearnspace(request));
    }

    @GetMapping
    public ResponseEntity<List<LearnspaceResource>> listarLearnspaces() {
        return ResponseEntity.ok(learnspaceService.listarLearnspaces());
    }
}
