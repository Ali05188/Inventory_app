package com.inventory.asset.repository;

import com.inventory.asset.entity.AssetType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AssetTypeRepository extends JpaRepository<AssetType, Long> {

    Optional<AssetType> findByName(String name);

    boolean existsByName(String name);
}

