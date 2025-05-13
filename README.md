# Réseau Social de Développeurs

Ce projet est une application full stack permettant aux développeurs de partager des articles sur différents thèmes de programmation et de technologie.

## Fonctionnalités

- Authentification des utilisateurs (inscription, connexion)
- Création, lecture, mise à jour et suppression d'articles
- Système de commentaires sur les articles
- Système d'abonnement à des thèmes
- Affichage personnalisé des articles selon les abonnements

## Structure du projet

Le projet est divisé en deux parties principales :

- **Frontend** : Application Angular avec Angular Material
- **Backend** : API REST Spring Boot avec base de données

## Prérequis

- Java 11+
- Node.js 14+
- npm 6+
- Maven 3.6+
- MySQL 8+

## Installation et démarrage

### Base de données

1. Créez une base de données MySQL nommée `devblog`
2. Configurez les informations de connexion dans `back/src/main/resources/application.properties`

### Backend (Spring Boot)

1. Accédez au répertoire backend :
   ```
   cd back
   ```

2. Installez les dépendances et lancez le serveur :
   ```
   ./mvnw spring-boot:run
   ```
   
   Le serveur démarrera sur http://localhost:8080

### Frontend (Angular)

1. Accédez au répertoire frontend :
   ```
   cd front
   ```

2. Installez les dépendances :
   ```
   npm install
   ```

3. Lancez le serveur de développement :
   ```
   ng serve
   ```
   
   L'application sera accessible sur http://localhost:4200

## Technologies utilisées

### Frontend
- Angular 14
- Angular Material
- RxJS
- TypeScript

### Backend
- Spring Boot
- Spring Security
- Spring Data JPA
- Hibernate
- JWT pour l'authentification
- MySQL

## Architecture

### Backend

Le backend suit une architecture en couches :
- **Contrôleurs** : Gestion des requêtes HTTP
- **Services** : Logique métier
- **Repositories** : Accès aux données
- **Modèles** : Entités JPA

### Frontend

Le frontend suit l'architecture composant d'Angular :
- **Composants** : Interface utilisateur
- **Services** : Communication avec l'API et logique métier
- **Modèles** : Interfaces TypeScript
- **Guards** : Protection des routes

## Contributeurs

- Équipe OpenClassrooms

