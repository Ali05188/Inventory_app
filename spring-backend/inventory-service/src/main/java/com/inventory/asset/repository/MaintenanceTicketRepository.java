package com.inventory.asset.repository;

import com.inventory.asset.entity.MaintenanceTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaintenanceTicketRepository extends JpaRepository<MaintenanceTicket, Long> {
    List<MaintenanceTicket> findByAssetId(Long assetId);
    List<MaintenanceTicket> findByStatus(String status);
}

