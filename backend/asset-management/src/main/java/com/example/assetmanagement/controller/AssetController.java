package com.example.assetmanagement.controller;

import com.example.assetmanagement.entity.Asset;
import com.example.assetmanagement.service.AssetService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assets")
@CrossOrigin(origins = "http://localhost:5175")
public class AssetController {

    private final AssetService service;

    public AssetController(AssetService service) {
        this.service = service;
    }

    @GetMapping
    public List<Asset> getAll() {
        return service.getAllAssets();
    }

    @GetMapping("/{id}")
    public Asset getById(@PathVariable Long id) {
        return service.getAssetById(id);
    }

    @PostMapping
    public Asset create(@RequestBody Asset asset) {
        return service.createAsset(asset);
    }

    @PutMapping("/{id}")
    public Asset update(
            @PathVariable Long id,
            @RequestBody Asset asset) {

        return service.updateAsset(id, asset);
    }
}
