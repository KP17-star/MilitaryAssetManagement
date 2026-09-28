package com.example.assetmanagement.repository;

import com.example.assetmanagement.entity.Base;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BaseRepository extends JpaRepository<Base, Long> {
}
