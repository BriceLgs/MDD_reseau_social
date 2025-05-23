# Backend - Réseau Social de Développeurs

API REST Spring Boot pour le réseau social de développeurs MDD.

## Technologies utilisées

- **Spring Boot 2.7+** - Framework principal
- **Spring Security** - Sécurité et authentification
- **Spring Data JPA** - Accès aux données
- **Hibernate** - ORM
- **JWT** - Authentification par token
- **MySQL** - Base de données
- **Maven** - Gestionnaire de dépendances

## Prérequis

- Java 11+
- Maven 3.6+
- MySQL 8+

## Installation

### Base de données

1. Installez MySQL et créez une base de données :
   ```sql
   CREATE DATABASE mddapi;
   ```

2. Configurez les informations de connexion dans `src/main/resources/application.properties` :
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/mddapi
   spring.datasource.username=votre_username
   spring.datasource.password=votre_password
   spring.jpa.hibernate.ddl-auto=update
   ```

### Démarrage

1. Accédez au répertoire backend :
   ```bash
   cd back
   ```

2. Installez les dépendances et lancez le serveur :
   ```bash
   ./mvnw spring-boot:run
   ```
   
   Ou sur Windows :
   ```bash
   mvnw.cmd spring-boot:run
   ```

Le serveur démarrera sur `http://localhost:8080`

## API Endpoints

### Authentification
- `POST /api/auth/register` - Inscription
- `POST /api/auth/login` - Connexion

### Articles
- `GET /api/articles` - Liste des articles
- `GET /api/articles/{id}` - Détail d'un article
- `POST /api/articles` - Créer un article
- `PUT /api/articles/{id}` - Modifier un article
- `DELETE /api/articles/{id}` - Supprimer un article

### Thèmes
- `GET /api/themes` - Liste des thèmes
- `GET /api/themes/{id}` - Détail d'un thème
- `POST /api/themes/{id}/subscribe` - S'abonner à un thème
- `DELETE /api/themes/{id}/unsubscribe` - Se désabonner d'un thème

### Commentaires
- `GET /api/articles/{articleId}/comments` - Commentaires d'un article
- `POST /api/articles/{articleId}/comments` - Ajouter un commentaire

### Utilisateur
- `GET /api/user/me` - Profil utilisateur
- `PUT /api/user/me` - Modifier le profil
- `GET /api/user/subscriptions` - Abonnements utilisateur

## Configuration

### Variables d'environnement

Vous pouvez configurer l'application via les variables d'environnement :

```bash
# Base de données
DB_URL=jdbc:mysql://localhost:3306/mddapi
DB_USERNAME=votre_username
DB_PASSWORD=votre_password

# JWT
JWT_SECRET=votre_secret_jwt
JWT_EXPIRATION=86400000

# CORS
CORS_ALLOWED_ORIGINS=http://localhost:4200
```

### Profils Spring

- **dev** - Profil de développement (par défaut)
- **prod** - Profil de production

Lancez avec un profil spécifique :
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

## Sécurité

### JWT Authentication

L'API utilise JWT pour l'authentification :

1. **Login** : Envoyez email/password à `/api/auth/login`
2. **Token** : Recevez un token JWT en réponse
3. **Authorization** : Incluez le token dans l'header : `Authorization: Bearer <token>`

### CORS

CORS est configuré pour autoriser les requêtes depuis le frontend Angular (localhost:4200).

## Base de données

### Schéma

```sql
-- Utilisateurs
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Thèmes
CREATE TABLE themes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Articles
CREATE TABLE articles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    author_id BIGINT NOT NULL,
    theme_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (author_id) REFERENCES users(id),
    FOREIGN KEY (theme_id) REFERENCES themes(id)
);

-- Commentaires
CREATE TABLE comments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    content TEXT NOT NULL,
    author_id BIGINT NOT NULL,
    article_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (author_id) REFERENCES users(id),
    FOREIGN KEY (article_id) REFERENCES articles(id)
);

-- Abonnements
CREATE TABLE subscriptions (
    user_id BIGINT NOT NULL,
    theme_id BIGINT NOT NULL,
    subscribed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, theme_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (theme_id) REFERENCES themes(id)
);
```

## Tests

### Tests unitaires

Lancez les tests unitaires :
```bash
./mvnw test
```

### Tests d'intégration

Lancez tous les tests :
```bash
./mvnw verify
```

## Build et déploiement

### Build local

Créez un JAR exécutable :
```bash
./mvnw clean package
```

Le JAR sera généré dans `target/`

### Exécution du JAR

```bash
java -jar target/mddapi-0.0.1-SNAPSHOT.jar
```

### Docker (optionnel)

Créez une image Docker :
```dockerfile
FROM openjdk:11-jre-slim
COPY target/mddapi-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

## Monitoring et logs

### Actuator

Spring Boot Actuator est configuré pour le monitoring :
- `/actuator/health` - État de l'application
- `/actuator/info` - Informations sur l'application

### Logs

Les logs sont configurés avec Logback. Niveaux disponibles :
- DEBUG
- INFO
- WARN
- ERROR

## Troubleshooting

### Problèmes courants

**Erreur de connexion à la DB** : Vérifiez que MySQL est démarré et que les credentials sont corrects

**Port déjà utilisé** : Changez le port dans `application.properties` :
```properties
server.port=8081
```

**Erreur JWT** : Vérifiez que la clé secrète JWT est configurée

**Problème CORS** : Vérifiez la configuration CORS dans `SecurityConfig.java` 