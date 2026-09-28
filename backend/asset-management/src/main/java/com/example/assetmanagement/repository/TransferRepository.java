package com.example.assetmanagement.repository;

import com.example.assetmanagement.entity.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransferRepository extends JpaRepository<Transfer, Long> {
}
