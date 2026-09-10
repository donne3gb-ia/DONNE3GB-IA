# DONNE 3GB IA — compilation depuis téléphone

Ce projet contient un workflow GitHub Actions qui compile automatiquement l'APK Android sans ordinateur local.

## Utilisation
1. Créer un dépôt GitHub.
2. Envoyer tout le contenu de ce dossier dans le dépôt.
3. Ouvrir l'onglet **Actions**.
4. Sélectionner **DONNE 3GB IA - Build APK**.
5. Appuyer sur **Run workflow**.
6. Une fois terminé avec succès, ouvrir l'exécution puis télécharger l'artefact **DONNE3GB-IA-APK**.

Le fichier obtenu est `DONNE3GB-IA.apk`.

## Sécurité
Ne jamais mettre `OPENAI_API_KEY` dans l'application Android ou dans un dépôt public. La clé doit rester côté serveur.
