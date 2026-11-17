# TP24 — Conteneurisation Spring Boot et Angular

Ce projet réalise le [TP24 du cours Architecture Microservices](https://mliaedu.toubkalit.com/student/courses/7/labs/250) à partir de l'application [Smart Home de lachgar](https://github.com/lachgar/smarthouse), commit `b9e62ff` (10 décembre 2023). Les sources Spring Boot et Angular proviennent de ce dépôt ; les Dockerfiles, le proxy Nginx, le fichier Compose et les adaptations de configuration ont été ajoutés pour ce TP.

## Services

| Service | Port hôte par défaut | Rôle |
| --- | ---: | --- |
| Angular + Nginx | 80 | Interface web et proxy `/api/` |
| Spring Boot | 8085 | API des catégories et appareils |
| MySQL | 3306 | Base `smart-house` avec volume persistant |
| phpMyAdmin | 8081 | Administration de la base |

Le navigateur appelle `/api/controller/` sur le même hôte que l'interface. Nginx transmet ces requêtes au service `backend` par le réseau Docker. Le backend se connecte à `mysql:3306`. Compose attend le contrôle de santé de MySQL avant de démarrer le backend, puis celui du backend avant de démarrer le frontend.

## Exécution

Docker Desktop et `docker compose` sont nécessaires. À la racine du projet :

```powershell
Copy-Item .env.example .env
# Modifier les deux mots de passe dans .env avant de lancer les services.
docker compose config --quiet
docker compose build
docker compose up -d
docker compose ps
```

Ouvrir ensuite <http://localhost/> pour l'application, <http://localhost:8085/api/controller/categorie/> pour vérifier l'API, et <http://localhost:8081/> pour phpMyAdmin. Dans phpMyAdmin, utiliser l'utilisateur et le mot de passe définis par `MYSQL_USER` et `MYSQL_PASSWORD` dans `.env`.

Pour consulter les journaux ou arrêter les conteneurs :

```powershell
docker compose logs -f backend
docker compose down
```

La commande `docker compose down` conserve le volume MySQL. Si les ports hôtes sont occupés, adapter `MYSQL_PORT`, `BACKEND_PORT`, `FRONTEND_PORT` ou `PHPMYADMIN_PORT` dans `.env` avant de démarrer. Le proxy du frontend fonctionne aussi si le port hôte change.

## Fichiers importants

- `Smart_Home_back/Dockerfile` : compilation Maven dans une image de construction, exécution du JAR dans une image Java 17 plus légère.
- `smartHome-front/Dockerfile` : construction Angular avec Node 16 puis service des fichiers statiques avec Nginx.
- `smartHome-front/nginx.conf` : routes Angular et proxy `/api/`.
- `compose.yaml` : les quatre services, les contrôles de santé, les ports et le volume MySQL.
- `.env.example` : modèle de configuration locale ; le vrai fichier `.env` est ignoré par Git.

L'application source utilise Angular 14. Node 16 est retenu pour la compilation selon la [table officielle de compatibilité Angular](https://angular.dev/reference/versions). Le dépôt d'origine contenait un fichier `.env` versionné ; il a été retiré du suivi dans cette version.

Le backend a été adapté à Spring Boot 3.5 et à une configuration MySQL pilotée par les variables d'environnement. Un test d'intégration utilise H2 pour exercer la création et la lecture d'une catégorie sans conteneur.

## Vérification

Depuis `Smart_Home_back`, lancer `mvn verify` pour exécuter le test d'intégration. La configuration Compose se valide à la racine du projet avec `docker compose config --quiet`. Sur la machine de préparation, ces deux commandes ont réussi (1 test, 0 échec). La construction des images et le démarrage des quatre services nécessitent le téléchargement initial des images Docker et restent à valider sur une machine disposant d'une connexion suffisante.
