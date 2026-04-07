# Cas d'Utilisation du Système de Gestion d'Inventaire

```mermaid
flowchart LR
    Admin((Super Admin))
    Manager((Asset Manager))
    Viewer((Viewer / Employé))
    System((Système Analytique))

    subgraph System_Boundary ["Gestion de l'Inventaire & Sécurité (Microservices)"]
        direction TB
        UC1(["S'authentifier"])
        UC2(["Gérer les Utilisateurs et Rôles"])
        UC3(["Gérer les Équipements (CRUD)"])
        UC4(["Importer un Fichier CSV (Assets)"])
        UC5(["Affecter un Matériel (Assignment)"])
        UC6(["Déclarer un Ticket de Maintenance"])
        UC7(["Résoudre un Ticket"])
        UC8(["Consulter l'Inventaire"])
        UC9(["Analyser les Risques de Pannes"])
    end

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

    UC9 -.->|"Lit les données"| UC3
    UC9 -.->|"Analyse l'historique des tickets"| UC6
```

## Description
*   **Super Admin** : Contrôle total sur la plateforme et les utilisateurs.
*   **Asset Manager** : Gère activement les stocks, les maintenances, les affectations du matériel de l'entreprise.
*   **Viewer** : Un utilisateur standard avec des droits de lecture ou de signalement de panne (création de tickets).

