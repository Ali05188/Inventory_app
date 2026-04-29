# Diagramme de Classes du Système d'Inventaire

```mermaid
classDiagram
    class User {
        +Long id
        +String name
        +String email
        +String password
        +boolean isActive
    }

    class Role {
        +Long id
        +String name
    }

    class Permission {
        +Long id
        +String name
    }

    class Asset {
        +Long id
        +String cabNumber
        +String projectCode
        +String assetNumber
        +String serialNumber
        +Integer quantity
        +BigDecimal unitPrice
        +LocalDate deliveryDate
        +AssetStatus status
    }

    class AssetAssignment {
        +Long id
        +Long assignedToUserId
        +LocalDate assignmentDate
        +LocalDate returnDate
        +String status
    }

    class MaintenanceTicket {
        +Long id
        +String issueDescription
        +String resolutionNotes
        +String priority
        +String status
        +LocalDate reportedDate
        +LocalDate resolvedDate
    }

    class Project {
        +Long id
        +String name
        +String code
    }

    class Supplier {
        +Long id
        +String name
        +String contact
    }

    class PredictiveMaintenanceResponse {
        +Long assetId
        +Integer ticketCount
        +Double failureProbability
        +String riskLevel
        +Integer estimatedDaysToFailure
    }

    User "1..*" -- "0..*" Role : "possède des Rôles"
    Role "1..*" -- "0..*" Permission : "possède des Permissions"
    
    Asset "1" -- "0..*" AssetAssignment : "est affecté"
    Asset "1" -- "0..*" MaintenanceTicket : "a des tickets"
    Asset "0..*" -- "1" Project : "appartient au projet"
    Asset "0..*" -- "1" Supplier : "fourni par"
    
    PredictiveMaintenanceResponse ..> Asset : "Analyse"
    PredictiveMaintenanceResponse ..> MaintenanceTicket : "Analyse l'historique"
```

Ce diagramme illustre les entités persistées sous MySQL dans le service d'authentification (`inventory_auth`) et d'inventaire (`inventory_assets`), ainsi que l'entité virtuelle d'analyse qui agglomère les informations via communication inter-microservices.

