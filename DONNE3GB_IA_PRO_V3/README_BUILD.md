# DONNE 3GB IA V3 — construction APK

## 1. Android Studio
Ouvrir le dossier `android/` dans Android Studio.
Attendre la synchronisation Gradle.

## 2. APK de test
Menu **Build → Build APK(s)**.
Le fichier est normalement généré dans :
`android/app/build/outputs/apk/debug/app-debug.apk`

## 3. Serveur IA
Dans `server/` :
- installer Node.js 20+
- `npm install`
- copier `.env.example` vers `.env`
- renseigner `OPENAI_API_KEY`
- `npm start`

Pour l'émulateur Android, l'application utilise `http://10.0.2.2:3000`.
Pour un téléphone réel, remplacer `API_URL` dans `MainActivity.kt` par l'URL HTTPS publique du serveur.

## 4. Important
Ne mets jamais `OPENAI_API_KEY` dans l'APK.
