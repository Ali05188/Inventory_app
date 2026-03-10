package com.inventory.asset.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "locations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String code;
    private String building;
    private String floor;
    private String zone;

    @Column(name = "is_active")
    private boolean isActive = true;
}

