package com.example.assetmanagement.service;

import com.example.assetmanagement.entity.EquipmentType;
import com.example.assetmanagement.repository.EquipmentTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EquipmentTypeService {

    private final EquipmentTypeRepository repository;

    public EquipmentTypeService(EquipmentTypeRepository repository) {
        this.repository = repository;
    }

    public List<EquipmentType> getAll() {
        return repository.findAll();
    }

    public EquipmentType getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Equipment type not found"));
    }

    public EquipmentType create(EquipmentType equipmentType) {
        return repository.save(equipmentType);
    }
}
