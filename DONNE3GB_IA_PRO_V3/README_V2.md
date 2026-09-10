# DONNE 3GB IA — V2
Cette version ajoute le logo DONNE 3GB dans l'application et un vrai export PDF local depuis l'écran Devis.
Le serveur OpenAI reste dans `server/`. Configure `OPENAI_API_KEY` uniquement dans le serveur.
Pour créer un APK: ouvrir `android/` dans Android Studio, synchroniser Gradle, puis Build > Build APK(s).
Pour une release distribuable, l'APK doit être signé avec une clé de signature. 
