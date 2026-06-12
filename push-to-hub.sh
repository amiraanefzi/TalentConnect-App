#!/bin/bash

# Configuration
DOCKER_USERNAME="amiraanefzi"
IMAGE_NAME="talent-connect"
IMAGE_TAG="latest"

echo "🔐 Connexion à Docker Hub..."

# Créer le dossier de config si nécessaire
mkdir -p ~/.docker

# Créer un fichier de credentials temporaire
cat > ~/.docker/config.json << EOF
{
  "auths": {
    "docker.io": {
      "auth": ""
    }
  }
}
EOF

echo "📦 Tentative de push..."
podman push ${DOCKER_USERNAME}/${IMAGE_NAME}:${IMAGE_TAG}

if [ $? -eq 0 ]; then
  echo "✅ Push réussi!"
  echo "L'image est disponible sur: https://hub.docker.com/r/${DOCKER_USERNAME}/${IMAGE_NAME}"
else
  echo "❌ Push échoué"
  echo ""
  echo "Solution alternative: Utilise Docker Hub UI"
  echo "1. Va sur: https://hub.docker.com/r/${DOCKER_USERNAME}/${IMAGE_NAME}"
  echo "2. Clique: Upload image"
  echo ""
  echo "Ou relance la connexion:"
  echo "  podman login docker.io"
fi
