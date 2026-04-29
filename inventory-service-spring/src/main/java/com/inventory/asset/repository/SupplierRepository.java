package com.inventory.asset.repository;

import com.inventory.asset.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    Optional<Supplier> findByCode(String code);

    List<Supplier> findByIsActiveTrue();

    boolean existsByCode(String code);
}

