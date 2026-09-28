package com.example.assetmanagement.service;

import com.example.assetmanagement.entity.Asset;
import com.example.assetmanagement.repository.AssetRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssetService {

    private final AssetRepository assetRepository;

    public AssetService(AssetRepository assetRepository) {
        this.assetRepository = assetRepository;
    }

    public List<Asset> getAllAssets() {
        return assetRepository.findAll();
    }

    public Asset getAssetById(Long id) {
        return assetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asset not found"));
    }

    public Asset createAsset(Asset asset) {
        return assetRepository.save(asset);
    }

    public Asset updateAsset(Long id, Asset updatedAsset) {

        Asset existing = getAssetById(id);

        existing.setAssetCode(updatedAsset.getAssetCode());
        existing.setAssetName(updatedAsset.getAssetName());
        existing.setEquipmentType(updatedAsset.getEquipmentType());
        existing.setBase(updatedAsset.getBase());
        existing.setQuantity(updatedAsset.getQuantity());
        existing.setStatus(updatedAsset.getStatus());

        return assetRepository.save(existing);
    }
}
