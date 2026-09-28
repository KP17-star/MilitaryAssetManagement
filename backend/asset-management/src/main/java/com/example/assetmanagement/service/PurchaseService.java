package com.example.assetmanagement.service;

import com.example.assetmanagement.entity.Asset;
import com.example.assetmanagement.entity.Purchase;
import com.example.assetmanagement.repository.AssetRepository;
import com.example.assetmanagement.repository.PurchaseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final AssetRepository assetRepository;
    private final AuditLogService auditLogService;

    public PurchaseService(
            PurchaseRepository purchaseRepository,
            AssetRepository assetRepository,
            AuditLogService auditLogService) {

        this.purchaseRepository = purchaseRepository;
        this.assetRepository = assetRepository;
        this.auditLogService = auditLogService;
    }

    public List<Purchase> getAllPurchases() {
        return purchaseRepository.findAll();
    }

    @Transactional
    public Purchase createPurchase(Purchase purchase) {

        if (purchase.getQuantity() == null || purchase.getQuantity() <= 0) {
            throw new RuntimeException("Purchase quantity must be greater than zero");
        }

        if (purchase.getBase() == null || purchase.getBase().getId() == null) {
            throw new RuntimeException("Base is required");
        }

        if (purchase.getEquipmentType() == null ||
                purchase.getEquipmentType().getId() == null) {
            throw new RuntimeException("Equipment type is required");
        }

        purchase.setCreatedAt(LocalDateTime.now());

        if (purchase.getPurchaseDate() == null) {
            purchase.setPurchaseDate(LocalDate.now());
        }

        List<Asset> assets = assetRepository.findAll();

        Asset matchingAsset = assets.stream()
                .filter(asset ->
                        asset.getBase() != null &&
                        asset.getBase().getId().equals(purchase.getBase().getId()))
                .filter(asset ->
                        asset.getEquipmentType() != null &&
                        asset.getEquipmentType().getId()
                                .equals(purchase.getEquipmentType().getId()))
                .findFirst()
                .orElse(null);

        if (matchingAsset == null) {
            throw new RuntimeException(
                    "No asset found for this base and equipment type");
        }

        int currentQuantity =
                matchingAsset.getQuantity() == null
                        ? 0
                        : matchingAsset.getQuantity();

        matchingAsset.setQuantity(
                currentQuantity + purchase.getQuantity()
        );

        assetRepository.save(matchingAsset);

        Purchase savedPurchase = purchaseRepository.save(purchase);

        auditLogService.log(
                purchase.getCreatedBy() == null
                        ? "admin"
                        : purchase.getCreatedBy(),
                "CREATE",
                "PURCHASE",
                savedPurchase.getId(),
                "Purchase of " + purchase.getQuantity()
                        + " units for base ID "
                        + purchase.getBase().getId()
        );

        return savedPurchase;
    }
}
