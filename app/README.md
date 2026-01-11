# TravelPath

TravelPath est une application Android native permettant de générer automatiquement des itinéraires touristiques personnalisés en fonction du budget, du temps et des envies de l'utilisateur.

## Fonctionnalités Clés

* **Génération intelligente :** Algorithme prenant en compte le budget, la durée et les centres d'intérêt (Culture, Food, Loisirs).
* **Données réelles :** Utilisation de l'API OpenStreetMap (Overpass) pour trouver les lieux.
* **Intégration Sociale :** Enrichissement visuel grâce à l'API **TravelShare** (Photos des utilisateurs à proximité).
* **Mode Hors-ligne :** Mise en cache des lieux visités via Room Database.
* **Navigation Active :** Suivi étape par étape avec horaires d'ouverture et export PDF.

## Prérequis

* Android Studio Ladybug ou supérieur.
* JDK 17 ou supérieur.
* Appareil Android ou Émulateur (API 24 min).
* Connexion Internet active pour le premier chargement.

## Installation

1.  **Cloner le projet :**
    ```bash
    git clone https://github.com/FeyzaKdnz/app-travelshare.git
    ```
2.  **Ouvrir dans Android Studio :**
    * File > Open > Sélectionner le dossier `app-travelpath`.
3.  **Sync Gradle :**
    * Laissez Android Studio télécharger les dépendances (Retrofit, Glide, Room, Gson...).
4.  **Configuration des API Keys (Optionnel) :**
    * L'application utilise une clé OpenWeatherMap incluse par défaut à des fins de démonstration.

## Manuel d'Utilisation

1.  **Écran d'Accueil :** Entrez une ville (ex: "Montpellier") et vos préférences.
2.  **Génération :** Cliquez sur "Create Journey". L'application télécharge les données et calcule le meilleur chemin.
3.  **Résultats :** Visualisez la liste. Vous pouvez voir les photos, la météo, ou voir la carte globale.
4.  **Start Journey :** Lance le mode "Étape par étape".
    * Cliquez sur "View on Map" pour lancer le GPS.
    * Vérifiez les horaires d'ouverture en bas de la carte.
5.  **Export :** Utilisez le bouton "Export PDF" pour partager l'itinéraire.

## Architecture

L'application suit une architecture **MVVM / Repository Pattern** :
* **UI :** Activités gérant l'affichage.
* **Repository :** Gère la logique de récupération des données (Local vs Réseau).
* **Engine :** Contient l'algorithme de filtrage et de création de trajet.

---
*Projet universitaire réalisé dans le cadre du module Programmation Mobile.*