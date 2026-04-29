package com.inventory.asset.repository;

import com.inventory.asset.entity.Asset;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long>, JpaSpecificationExecutor<Asset> {

    Optional<Asset> findBySerialNumber(String serialNumber);

    boolean existsBySerialNumber(String serialNumber);

    boolean existsBySerialNumberAndIdNot(String serialNumber, Long id);

    @Query("SELECT a FROM Asset a WHERE a.deletedAt IS NULL")
    Page<Asset> findAllActive(Pageable pageable);

    @Query("SELECT a FROM Asset a WHERE a.deletedAt IS NULL " +
           "AND (:search IS NULL OR a.cabNumber LIKE %:search% " +
           "OR a.assetNumber LIKE %:search% " +
           "OR a.designation LIKE %:search% " +
           "OR a.serialNumber LIKE %:search%)")
    Page<Asset> searchAssets(@Param("search") String search, Pageable pageable);

    List<Asset> findByProjectId(Long projectId);

    List<Asset> findBySupplierId(Long supplierId);

    List<Asset> findByStatus(String status);

    @Query("SELECT a FROM Asset a WHERE a.deliveryDate BETWEEN :fromDate AND :toDate AND a.deletedAt IS NULL")
    List<Asset> findByDeliveryDateBetween(@Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate);

    @Query("SELECT COUNT(a) FROM Asset a WHERE a.status = :status AND a.deletedAt IS NULL")
    long countByStatus(@Param("status") String status);
}

