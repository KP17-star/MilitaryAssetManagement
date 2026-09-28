package com.example.assetmanagement.service;

import com.example.assetmanagement.entity.Assignment;
import com.example.assetmanagement.entity.Asset;
import com.example.assetmanagement.repository.AssignmentRepository;
import com.example.assetmanagement.repository.AssetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final AssetRepository assetRepository;

    public AssignmentService(
            AssignmentRepository assignmentRepository,
            AssetRepository assetRepository) {
        this.assignmentRepository = assignmentRepository;
        this.assetRepository = assetRepository;
    }

    public List<Assignment> getAllAssignments() {
        return assignmentRepository.findAll();
    }

    @Transactional
    public Assignment createAssignment(Assignment assignment) {

        if (assignment.getAsset() == null || assignment.getAsset().getId() == null) {
            throw new RuntimeException("Asset is required");
        }

        if (assignment.getQuantity() == null || assignment.getQuantity() <= 0) {
            throw new RuntimeException("Assignment quantity must be greater than zero");
        }

        Asset asset = assetRepository.findById(assignment.getAsset().getId())
                .orElseThrow(() -> new RuntimeException("Asset not found"));

        int availableQuantity = asset.getQuantity() == null ? 0 : asset.getQuantity();

        if (assignment.getQuantity() > availableQuantity) {
            throw new RuntimeException("Insufficient asset quantity");
        }

        assignment.setAsset(asset);
        assignment.setAssignedDate(LocalDateTime.now());
        assignment.setStatus("ASSIGNED");

        return assignmentRepository.save(assignment);
    }

    @Transactional
    public Assignment expendAssignment(Long id) {

        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));

        if ("EXPENDED".equalsIgnoreCase(assignment.getStatus())) {
            throw new RuntimeException("Assignment is already expended");
        }

        Asset asset = assignment.getAsset();

        int availableQuantity = asset.getQuantity() == null ? 0 : asset.getQuantity();

        if (assignment.getQuantity() > availableQuantity) {
            throw new RuntimeException("Insufficient asset quantity for expenditure");
        }

        asset.setQuantity(availableQuantity - assignment.getQuantity());
        assetRepository.save(asset);

        assignment.setStatus("EXPENDED");

        return assignmentRepository.save(assignment);
    }
}
