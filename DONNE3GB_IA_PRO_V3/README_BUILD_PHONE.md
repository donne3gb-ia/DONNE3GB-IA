# Construire l'APK depuis un téléphone

1. Crée un dépôt GitHub vide depuis le navigateur du téléphone.
2. Envoie tout le contenu de ce dossier dans le dépôt.
3. Ouvre l'onglet Actions.
4. Lance « Build DONNE 3GB IA APK » avec « Run workflow ».
5. Quand le workflow est terminé, ouvre le run puis télécharge l'artifact « DONNE3GB-IA-debug-apk ».

Remarque : le serveur `server/` est séparé. Ne mets jamais `OPENAI_API_KEY` dans l'application Android.
