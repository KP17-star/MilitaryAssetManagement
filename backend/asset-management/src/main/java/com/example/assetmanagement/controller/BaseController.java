package com.example.assetmanagement.controller;

import com.example.assetmanagement.entity.Base;
import com.example.assetmanagement.service.BaseService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bases")
@CrossOrigin(origins = "http://localhost:5175")
public class BaseController {

    private final BaseService service;

    public BaseController(BaseService service) {
        this.service = service;
    }

    @GetMapping
    public List<Base> getAll() {
        return service.getAllBases();
    }

    @GetMapping("/{id}")
    public Base getById(@PathVariable Long id) {
        return service.getBaseById(id);
    }

    @PostMapping
    public Base create(@RequestBody Base base) {
        return service.createBase(base);
    }
}
