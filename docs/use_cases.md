# Cas d'Utilisation du Système de Gestion d'Inventaire

```mermaid
usecaseDiagram
    actor "Super Admin" as Admin
    actor "Asset Manager" as Manager
    actor "Viewer / Employé" as Viewer
    actor "Système Analytique" as System

    rectangle "Gestion de l'Inventaire & Sécurité (Microservices)" {
        usecase "S'authentifier" as UC1
        usecase "Gérer les Utilisateurs et Rôles" as UC2
        usecase "Gérer les Équipements (CRUD)" as UC3
        usecase "Importer un Fichier CSV (Assets)" as UC4
        usecase "Affecter un Matériel (Assignment)" as UC5
        usecase "Déclarer un Ticket de Maintenance" as UC6
        usecase "Résoudre un Ticket" as UC7
        usecase "Consulter l'Inventaire" as UC8
        usecase "Analyser les Risques de Pannes" as UC9
    }

    Admin --> UC1
    Manager --> UC1
    Viewer --> UC1
    System --> UC9

    Admin --> UC2
    Admin --> UC3
    Admin --> UC4
    Admin --> UC5
    Admin --> UC7

    Manager --> UC3
    Manager --> UC4
    Manager --> UC5
    Manager --> UC6
    Manager --> UC7

    Viewer --> UC6
    Viewer --> UC8

    UC9 --> UC3 : "Lit les données"
    UC9 --> UC6 : "Analyse l'historique des tickets"
```

## Description
*   **Super Admin** : Contrôle total sur la plateforme et les utilisateurs.
*   **Asset Manager** : Gère activement les stocks, les maintenances, les affectations du matériel de l'entreprise.
*   **Viewer** : Un utilisateur standard avec des droits de lecture ou de signalement de panne (création de tickets).

