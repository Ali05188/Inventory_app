# Cas d'Utilisation du Système de Gestion d'Inventaire

```mermaid
flowchart LR
    %% Acteurs Humains (Garde l'alignement à gauche)
    Admin((Super Admin))
    Manager((Asset Manager))
    Viewer((Viewer / Employé))
    
    %% Acteur Système
    System((Système Analytique))

    %% Forcer la disposition des acteurs de haut en bas
    Admin ~~~ Manager
    Manager ~~~ Viewer

    subgraph System_Boundary ["Gestion de l'Inventaire & Sécurité (Microservices)"]
        %% Ordre optimisé pour limiter les croisements de lignes
        UC2(["Gérer les Utilisateurs et Rôles"])
        UC4(["Importer un Fichier CSV (Assets)"])
        UC5(["Affecter un Matériel (Assignment)"])
        UC7(["Résoudre un Ticket"])
        UC3(["Gérer les Équipements (CRUD)"])
        UC1(["S'authentifier"])
        UC6(["Déclarer un Ticket de Maintenance"])
        UC8(["Consulter l'Inventaire"])
        UC9(["Analyser les Risques de Pannes"])
    end

    %% Connexions Admin (Vers le haut et le milieu)
    Admin --> UC2
    Admin --> UC4
    Admin --> UC5
    Admin --> UC7
    Admin --> UC3
    Admin --> UC1

    %% Connexions Manager (Vers le milieu)
    Manager --> UC4
    Manager --> UC5
    Manager --> UC7
    Manager --> UC3
    Manager --> UC1
    Manager --> UC6

    %% Connexions Viewer (Vers le bas)
    Viewer --> UC1
    Viewer --> UC6
    Viewer --> UC8

    %% Connexions du Système (Placé vers le bas)
    System --> UC9
    UC9 -.->|"Lit les données"| UC3
    UC9 -.->|"Analyse l'historique"| UC6
```

## Description
*   **Super Admin** : Contrôle total sur la plateforme et les utilisateurs.
*   **Asset Manager** : Gère activement les stocks, les maintenances, les affectations du matériel de l'entreprise.
*   **Viewer** : Un utilisateur standard avec des droits de lecture ou de signalement de panne (création de tickets).

