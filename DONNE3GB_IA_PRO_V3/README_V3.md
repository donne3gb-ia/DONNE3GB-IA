# DONNE 3GB IA V3

Version professionnelle :
- IA connectée au serveur OpenAI
- génération de devis dynamique
- ajout/modification d'articles
- export PDF avec logo DONNE 3GB
- sélection de photos
- commande vocale Android
- backend Node/Express sécurisé

## Sécurité
Ne mets jamais OPENAI_API_KEY dans l'application Android. La clé reste dans `server/.env`.

## Lancer le serveur
```bash
cd server
npm install
cp .env.example .env
# renseigner OPENAI_API_KEY dans .env
npm start
```

## Android
Ouvre le dossier `android/` dans Android Studio puis synchronise Gradle et lance l'application.

Pour un téléphone réel, remplace `API_URL` dans `MainActivity.kt` par l'adresse HTTPS de ton serveur. Pour l'émulateur Android, `10.0.2.2:3000` pointe vers le PC hôte.

## APK
Build > Build APK(s) pour un APK de test. Pour une version distribuable, utilise une signature release.
