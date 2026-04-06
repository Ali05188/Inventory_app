package com.inventory.asset.repository;

import com.inventory.asset.entity.AssetAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssetAssignmentRepository extends JpaRepository<AssetAssignment, Long> {
    List<AssetAssignment> findByAssetId(Long assetId);
    List<AssetAssignment> findByAssignedToUserId(Long userId);
}

