# Réseau Social de Développeurs - MDD

Application full stack permettant aux développeurs de partager des articles sur différents thèmes de programmation et de technologie.

## 🚀 Aperçu du projet

Cette application permet aux développeurs de :
- ✅ S'inscrire et se connecter
- 📝 Créer et partager des articles
- 💬 Commenter les articles
- 🏷️ S'abonner à des thèmes d'intérêt
- 👤 Gérer leur profil et abonnements

## 🏗️ Architecture

Le projet est divisé en deux parties principales :

- **[Frontend](./front/README.md)** - Application Angular avec Angular Material
- **[Backend](./back/README.md)** - API REST Spring Boot avec MySQL

## 🛠️ Technologies principales

### Frontend
- Angular 14
- Angular Material
- TypeScript
- RxJS

### Backend
- Spring Boot
- Spring Security
- Spring Data JPA
- JWT
- MySQL

## 🚀 Démarrage rapide

### Prérequis
- Java 11+
- Node.js 14+
- MySQL 8+
- Maven 3.6+

### 1. Base de données
```sql
CREATE DATABASE mddapi;
```

### 2. Backend
```bash
cd back
./mvnw spring-boot:run
```
🔗 API disponible sur http://localhost:8080

### 3. Frontend
```bash
cd front
npm install
ng serve
```
🌐 Application disponible sur http://localhost:4200

## 📖 Documentation détaillée

Pour des instructions complètes d'installation, configuration et développement :

- **[📱 Frontend README](./front/README.md)** - Guide complet Angular
- **[⚙️ Backend README](./back/README.md)** - Guide complet Spring Boot

## 🏛️ Architecture technique

```
📦 Projet MDD
├── 📁 front/           # Application Angular
│   ├── src/app/pages/  # Pages principales
│   ├── src/app/services/ # Services Angular
│   └── README.md       # 📱 Guide Frontend
├── 📁 back/            # API Spring Boot
│   ├── src/main/java/  # Code Java
│   ├── src/main/resources/ # Configuration
│   └── README.md       # ⚙️ Guide Backend
└── README.md           # 📄 Ce fichier
```

## 🔐 Authentification

L'application utilise JWT (JSON Web Tokens) pour l'authentification :
1. L'utilisateur se connecte avec email/mot de passe
2. Le backend génère un token JWT
3. Le frontend stocke et utilise ce token pour les requêtes

## 🌟 Fonctionnalités clés

| Fonctionnalité | Description |
|---|---|
| 🔐 **Auth** | Inscription, connexion sécurisée |
| 📝 **Articles** | CRUD complet des articles |
| 💬 **Commentaires** | Système de commentaires |
| 🏷️ **Thèmes** | Abonnement aux thèmes d'intérêt |
| 👤 **Profil** | Gestion du profil utilisateur |
| 📱 **Responsive** | Compatible mobile/tablet/desktop |

## 🤝 Contribution

1. Forkez le projet
2. Créez une branche pour votre fonctionnalité
3. Committez vos changements
4. Poussez vers la branche
5. Ouvrez une Pull Request

## 📞 Support

Pour toute question ou problème :
- Consultez les README détaillés ([Frontend](./front/README.md) | [Backend](./back/README.md))
- Vérifiez les sections "Troubleshooting" dans chaque README

---

**Développé avec ❤️ pour la communauté des développeurs** 