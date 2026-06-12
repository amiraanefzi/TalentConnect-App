# 🔐 Instructions de Connexion Podman vers Docker Hub

## Étape 1 : Se connecter à Docker Hub avec Podman

```bash
podman login docker.io
```

Tu seras demandé de saisir:
```
Username: amiraanefzi
Password: (ton password Docker Hub)
```

## Étape 2 : Vérifier la connexion

```bash
# Voir les credentials stockés
podman info | grep "Registries"

# Ou tester un push
podman push amiraanefzi/talent-connect:latest
```

## Étape 3 : Troubleshooting

### Erreur: "access denied"

**Cause**: Pas connecté ou credentials mauvais

**Solutions**:
1. Redémarre Podman machine
   ```bash
   podman machine restart
   ```

2. Reconnecte-toi
   ```bash
   podman logout docker.io
   podman login docker.io
   ```

3. Crée un Personal Access Token (PAT)
   - Va sur : https://hub.docker.com/settings/security
   - Clique : New Access Token
   - Copie le token
   - Utilise le token comme password à la place du vrai password

## Étape 4 : Repush l'image

```bash
podman push amiraanefzi/talent-connect:latest
```

Attends ~2-5 minutes (upload 292 MB)

## Étape 5 : Vérifier sur Docker Hub

Va sur: https://hub.docker.com/r/amiraanefzi/talent-connect

Tu devrais voir l'image "latest" listée!

