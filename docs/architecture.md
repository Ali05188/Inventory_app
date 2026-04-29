# Architecture du Système d'Inventaire (Microservices)

## Diagramme de Déploiement & Architecture globale

```mermaid
graph TD
    Client((Client Web/Mobile)) -->|HTTP/REST| Gateway[API Gateway :8080]
    
    subgraph Spring Cloud Infrastructure
        Gateway -.-> |Routage| Discovery[Eureka Discovery Server :8761]
        Config[Config Server :8888] -.-> |Fournit la configuration| AuthSvc
        Config -.-> |Fournit la configuration| InvSvc
        Config -.-> |Fournit la configuration| AnlSvc
    end

    subgraph Microservices Métiers
        Gateway -->|/api/auth/*| AuthSvc[Auth Service :8081]
        Gateway -->|/api/assets/*| InvSvc[Inventory Service :8082]
        Gateway -->|/api/analytics/*| AnlSvc[Analytics Service :8083]
        
        AnlSvc -->|FeignClient| InvSvc
    end
    
    subgraph Bases de Données
        AuthSvc --> MySQL_DB[(MySQL: Database 'inventory')]
        InvSvc --> MySQL_DB
    end
```

## Composants
1. **API Gateway** : Point d'entrée unique. Expose les routes `/api/auth`, `/api/assets`, `/api/maintenances`, `/api/analytics`.
2. **Eureka Discovery Server** : Gère le registre des microservices pour la résolution des noms (LB).
3. **Auth Service** : Gère les rôles (RBAC), les utilisateurs, et génère le JWT.
4. **Inventory Service** : Cœur métier (Équipements, Affectations, Tickets de maintenance, Historique).
5. **Analytics Service** : Module décisionnel (Maintenance prédictive heuristique) communicant via FeignClient.

