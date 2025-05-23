# Frontend - Réseau Social de Développeurs

Application Angular frontend pour le réseau social de développeurs MDD.

## Technologies utilisées

- **Angular 14** - Framework principal
- **Angular Material** - UI Components
- **RxJS** - Programmation réactive
- **TypeScript** - Langage de programmation
- **SCSS** - Préprocesseur CSS

## Prérequis

- Node.js 14+
- npm 6+
- Angular CLI

## Installation

1. Accédez au répertoire frontend :
   ```bash
   cd front
   ```

2. Installez les dépendances :
   ```bash
   npm install
   ```

3. Installez Angular CLI globalement (si pas déjà fait) :
   ```bash
   npm install -g @angular/cli
   ```

## Démarrage

### Mode développement

Lancez le serveur de développement :
```bash
ng serve
```

L'application sera accessible sur `http://localhost:4200`

### Build de production

Créez un build de production :
```bash
ng build --prod
```

Les fichiers seront générés dans le dossier `dist/`

## Structure du projet

```
src/
├── app/
│   ├── components/       # Composants réutilisables
│   ├── pages/           # Pages principales
│   │   ├── home/        # Page d'accueil
│   │   ├── login/       # Page de connexion
│   │   ├── register/    # Page d'inscription
│   │   ├── articles/    # Liste des articles
│   │   ├── article/     # Détail d'un article
│   │   ├── themes/      # Liste des thèmes
│   │   ├── me/          # Profil utilisateur
│   │   └── create-article/ # Création d'article
│   ├── services/        # Services Angular
│   ├── models/          # Interfaces TypeScript
│   ├── interceptors/    # Intercepteurs HTTP
│   └── guards/          # Guards de route
├── assets/              # Ressources statiques
├── styles/              # Styles globaux
└── environments/        # Configuration d'environnement
```

## Fonctionnalités

### Pages publiques
- **Home** - Page d'accueil avec présentation
- **Login** - Connexion utilisateur
- **Register** - Inscription utilisateur

### Pages authentifiées
- **Articles** - Liste des articles avec tri et filtrage
- **Article Detail** - Affichage d'un article avec commentaires
- **Themes** - Liste des thèmes disponibles
- **Profile** - Gestion du profil et abonnements
- **Create Article** - Création de nouveaux articles

### Fonctionnalités clés
- 🔐 Authentification JWT
- 📱 Design responsive
- 🎨 Interface Material Design
- ⚡ Navigation fluide avec Angular Router
- 🔄 Gestion d'état réactive avec RxJS
- 📄 Pagination et tri des articles
- 💬 Système de commentaires
- 👤 Gestion de profil
- 📚 Abonnement aux thèmes

## Configuration

### Variables d'environnement

Configurez l'URL de l'API dans `src/environments/environment.ts` :

```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api'
};
```

### Proxy pour le développement

Un fichier `proxy.conf.json` peut être configuré pour éviter les problèmes CORS :

```json
{
  "/api/*": {
    "target": "http://localhost:8080",
    "secure": true,
    "changeOrigin": true
  }
}
```

Puis lancez avec :
```bash
ng serve --proxy-config proxy.conf.json
```

## Scripts disponibles

- `npm start` - Lance le serveur de développement
- `npm run build` - Build de production
- `npm run test` - Lance les tests unitaires
- `npm run lint` - Vérification du code avec ESLint
- `npm run e2e` - Tests end-to-end

## Contribution

1. Créez une branche pour votre fonctionnalité
2. Respectez les conventions de nommage Angular
3. Ajoutez des tests si nécessaire
4. Vérifiez que le linting passe
5. Créez une pull request

## Support navigateurs

- Chrome (dernières versions)
- Firefox (dernières versions)
- Safari (dernières versions)
- Edge (dernières versions)

## Troubleshooting

### Problèmes courants

**Erreur de CORS** : Assurez-vous que le backend autorise les requêtes depuis localhost:4200

**Module non trouvé** : Supprimez `node_modules` et relancez `npm install`

**Port déjà utilisé** : Changez le port avec `ng serve --port 4201` 