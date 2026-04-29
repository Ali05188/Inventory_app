package com.inventory.asset.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "asset_types")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssetType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String code;
    private String description;
}

