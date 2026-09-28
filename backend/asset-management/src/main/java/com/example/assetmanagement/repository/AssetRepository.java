package com.example.assetmanagement.repository;

import com.example.assetmanagement.entity.Asset;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssetRepository extends JpaRepository<Asset, Long> {
}
