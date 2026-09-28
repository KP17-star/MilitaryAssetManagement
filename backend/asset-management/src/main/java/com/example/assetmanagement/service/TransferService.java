package com.example.assetmanagement.service;

import com.example.assetmanagement.entity.Asset;
import com.example.assetmanagement.entity.Transfer;
import com.example.assetmanagement.repository.AssetRepository;
import com.example.assetmanagement.repository.TransferRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransferService {

    private final TransferRepository transferRepository;
    private final AssetRepository assetRepository;
    private final AuditLogService auditLogService;

    public TransferService(
            TransferRepository transferRepository,
            AssetRepository assetRepository,
            AuditLogService auditLogService) {

        this.transferRepository = transferRepository;
        this.assetRepository = assetRepository;
        this.auditLogService = auditLogService;
    }

    public List<Transfer> getAllTransfers() {
        return transferRepository.findAll();
    }

    @Transactional
    public Transfer createTransfer(Transfer transfer) {

        if (transfer.getAsset() == null ||
                transfer.getAsset().getId() == null) {
            throw new RuntimeException("Asset is required");
        }

        if (transfer.getFromBase() == null ||
                transfer.getFromBase().getId() == null) {
            throw new RuntimeException("Source base is required");
        }

        if (transfer.getToBase() == null ||
                transfer.getToBase().getId() == null) {
            throw new RuntimeException("Destination base is required");
        }

        if (transfer.getFromBase().getId()
                .equals(transfer.getToBase().getId())) {
            throw new RuntimeException(
                    "Source and destination bases cannot be the same");
        }

        if (transfer.getQuantity() == null ||
                transfer.getQuantity() <= 0) {
            throw new RuntimeException(
                    "Transfer quantity must be greater than zero");
        }

        Asset sourceAsset = assetRepository
                .findById(transfer.getAsset().getId())
                .orElseThrow(() ->
                        new RuntimeException("Source asset not found"));

        if (!sourceAsset.getBase().getId()
                .equals(transfer.getFromBase().getId())) {
            throw new RuntimeException(
                    "Asset does not belong to the source base");
        }

        if (sourceAsset.getQuantity() < transfer.getQuantity()) {
            throw new RuntimeException("Insufficient asset quantity");
        }

        // Remove from source
        sourceAsset.setQuantity(
                sourceAsset.getQuantity() - transfer.getQuantity()
        );

        assetRepository.save(sourceAsset);

        // Add to destination
        List<Asset> assets = assetRepository.findAll();

        Asset destinationAsset = assets.stream()
                .filter(asset ->
                        asset.getBase() != null &&
                        asset.getBase().getId()
                                .equals(transfer.getToBase().getId()))
                .filter(asset ->
                        asset.getEquipmentType() != null &&
                        sourceAsset.getEquipmentType() != null &&
                        asset.getEquipmentType().getId()
                                .equals(sourceAsset.getEquipmentType().getId()))
                .findFirst()
                .orElse(null);

        if (destinationAsset != null) {

            int currentQuantity =
                    destinationAsset.getQuantity() == null
                            ? 0
                            : destinationAsset.getQuantity();

            destinationAsset.setQuantity(
                    currentQuantity + transfer.getQuantity()
            );

            assetRepository.save(destinationAsset);

        } else {

            Asset newAsset = new Asset();

            newAsset.setAssetCode(
                    sourceAsset.getAssetCode()
                            + "-TRANSFER-"
                            + System.currentTimeMillis()
            );

            newAsset.setAssetName(sourceAsset.getAssetName());
            newAsset.setEquipmentType(sourceAsset.getEquipmentType());
            newAsset.setBase(transfer.getToBase());
            newAsset.setQuantity(transfer.getQuantity());
            newAsset.setStatus("AVAILABLE");
            newAsset.setCreatedAt(LocalDateTime.now());

            assetRepository.save(newAsset);
        }

        transfer.setTransferDate(LocalDateTime.now());

        Transfer savedTransfer =
                transferRepository.save(transfer);

        // Audit log
        auditLogService.log(
                transfer.getCreatedBy() == null
                        ? "admin"
                        : transfer.getCreatedBy(),
                "TRANSFER",
                "TRANSFER",
                savedTransfer.getId(),
                "Transferred "
                        + transfer.getQuantity()
                        + " units from base "
                        + transfer.getFromBase().getId()
                        + " to base "
                        + transfer.getToBase().getId()
        );

        return savedTransfer;
    }
}
