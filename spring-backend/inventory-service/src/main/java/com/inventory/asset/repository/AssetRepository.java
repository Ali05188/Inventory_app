package com.inventory.asset.repository;

import com.inventory.asset.entity.Asset;
import com.inventory.common.enums.AssetStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {

    Optional<Asset> findBySerialNumber(String serialNumber);

    boolean existsBySerialNumber(String serialNumber);

    Page<Asset> findByDeletedAtIsNull(Pageable pageable);

    List<Asset> findByStatus(AssetStatus status);

    @Query("SELECT a FROM Asset a WHERE a.deletedAt IS NULL " +
           "AND (:search IS NULL OR a.cabNumber LIKE %:search% OR a.assetNumber LIKE %:search% OR a.designation LIKE %:search%) " +
           "AND (:projectId IS NULL OR a.project.id = :projectId) " +
           "AND (:supplierId IS NULL OR a.supplier.id = :supplierId) " +
           "AND (:status IS NULL OR a.status = :status)")
    Page<Asset> findWithFilters(
            @Param("search") String search,
            @Param("projectId") Long projectId,
            @Param("supplierId") Long supplierId,
            @Param("status") AssetStatus status,
            Pageable pageable
    );

    @Query("SELECT COUNT(a) FROM Asset a WHERE a.deletedAt IS NULL")
    long countActive();

    @Query("SELECT COUNT(a) FROM Asset a WHERE a.status = :status AND a.deletedAt IS NULL")
    long countByStatus(@Param("status") AssetStatus status);

    @Query("SELECT COALESCE(SUM(a.unitPrice * a.quantity), 0) FROM Asset a WHERE a.deletedAt IS NULL")
    BigDecimal calculateTotalValue();
}

