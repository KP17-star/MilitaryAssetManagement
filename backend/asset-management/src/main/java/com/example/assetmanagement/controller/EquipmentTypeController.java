package com.example.assetmanagement.controller;

import com.example.assetmanagement.entity.EquipmentType;
import com.example.assetmanagement.service.EquipmentTypeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/equipment-types")
@CrossOrigin(origins = "http://localhost:5175")
public class EquipmentTypeController {

    private final EquipmentTypeService service;

    public EquipmentTypeController(EquipmentTypeService service) {
        this.service = service;
    }

    @GetMapping
    public List<EquipmentType> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public EquipmentType getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public EquipmentType create(@RequestBody EquipmentType equipmentType) {
        return service.create(equipmentType);
    }
}
