# DONNE 3GB IA — application Android + serveur OpenAI

## Fonctions V1
- Assistant IA
- Générateur de devis
- Calcul automatique des totaux
- Préparation à l'export PDF
- Backend sécurisé pour OpenAI

## Architecture
`android/` = application Android.
`server/` = serveur Node.js qui conserve `OPENAI_API_KEY`.

Ne mets JAMAIS la clé OpenAI dans l'APK.
L'API Responses d'OpenAI est appelée uniquement par le serveur.

## Lancement du serveur
1. Installer Node.js 20+
2. `cd server`
3. `npm install`
4. Copier `.env.example` vers `.env`
5. Mettre ta clé dans `OPENAI_API_KEY`
6. `npm start`

## Lancement Android
Ouvrir `android/` avec Android Studio, synchroniser Gradle puis lancer l'application.
Pour une version distribuable, Android demande une version release signée. Voir la documentation Android.
