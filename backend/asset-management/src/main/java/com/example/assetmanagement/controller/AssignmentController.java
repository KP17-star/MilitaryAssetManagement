package com.example.assetmanagement.controller;

import com.example.assetmanagement.entity.Assignment;
import com.example.assetmanagement.service.AssignmentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assignments")
@CrossOrigin(origins = "http://localhost:5175")
public class AssignmentController {

    private final AssignmentService service;

    public AssignmentController(AssignmentService service) {
        this.service = service;
    }

    @GetMapping
    public List<Assignment> getAll() {
        return service.getAllAssignments();
    }

    @PostMapping
    public Assignment create(@RequestBody Assignment assignment) {
        return service.createAssignment(assignment);
    }

    @PutMapping("/{id}/expend")
    public Assignment expend(@PathVariable Long id) {
        return service.expendAssignment(id);
    }
}
