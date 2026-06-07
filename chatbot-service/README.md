# TalentConnect Chatbot Service

Un service de chatbot conversationnel pour TalentConnect, conçu pour assister les utilisateurs en matière de recherche d'emploi, développement de carrière, et support RH.

## 🚀 Fonctionnalités

- **Communication en temps réel** via WebSocket
- **Moteur de conversation** basé sur des patterns/règles
- **Historique de conversations** persistant en base de données
- **API REST** pour récupérer l'historique et gérer les conversations
- **Support multi-utilisateurs** avec identification par userId

## 📋 Prérequis

- Java 17+
- Spring Boot 4.0.5
- MySQL 8.0+
- Maven 3.6+

## 🔧 Installation

### 1. Configuration de la base de données

Créez la base de données MySQL :

```sql
CREATE DATABASE talent_connect_chatbot;
```

### 2. Compilation et démarrage

```bash
cd chatbot-service
mvn clean install
mvn spring-boot:run
```

Le service démarrera sur le port **8083**.

## 📡 WebSocket Endpoint

### Connexion

**URL** : `ws://localhost:8083/ws/chat`

### Format du message

```json
{
  "sender": "user",
  "message": "Bonjour, je cherche un emploi",
  "userId": "user123",
  "timestamp": 1686752400000
}
```

### Réponse du bot

```json
{
  "sender": "bot",
  "message": "Vous cherchez un emploi? 💼 Nous avons de nombreuses offres disponibles...",
  "timestamp": 1686752401000
}
```

## 🔌 API REST Endpoints

### 1. Récupérer l'historique complet

```http
GET /api/chatbot/history/{userId}
```

**Exemple** :
```bash
curl http://localhost:8083/api/chatbot/history/user123
```

### 2. Récupérer les conversations récentes

```http
GET /api/chatbot/recent/{userId}?limit=10
```

**Exemple** :
```bash
curl http://localhost:8083/api/chatbot/recent/user123?limit=10
```

### 3. Effacer l'historique

```http
DELETE /api/chatbot/history/{userId}
```

**Exemple** :
```bash
curl -X DELETE http://localhost:8083/api/chatbot/history/user123
```

### 4. Vérification de santé

```http
GET /api/chatbot/health
```

**Exemple** :
```bash
curl http://localhost:8083/api/chatbot/health
```

## 🤖 Moteur de Conversation

Le chatbot supporte les thèmes suivants :

1. **Salutations** : "Bonjour", "Salut", "Coucou"
2. **Recherche d'emploi** : "Emploi", "Job", "Offre", "Poste"
3. **Développement de carrière** : "Carrière", "Compétence", "Formation"
4. **Avantages** : "Salaire", "Prime", "Assurance", "Congés"
5. **Culture d'entreprise** : "Culture", "Équipe", "Valeurs", "Mission"
6. **Statut des candidatures** : "Candidature", "Statut", "Dossier"
7. **Support** : "Contact", "Aide", "Problème"
8. **Au revoir** : "Au revoir", "Bye", "À bientôt"

## 🔄 Flux d'interaction

```
User                          WebSocket                    Chatbot Service
  |                              |                              |
  |------ Connect(userId) ------->|                              |
  |                              |---- Welcome Message -------->|
  |<------ Welcome Msg -----------|                              |
  |                              |                              |
  |------ Chat Message ---------->|                              |
  |                              |---- Process & Store -------->|
  |                              |<---- Bot Response ----------|
  |<------ Bot Response ----------|                              |
  |                              |                              |
  |------ Disconnect ------------>|                              |
```

## 📊 Structure de la Base de Données

### Table : chat_conversations

```sql
CREATE TABLE chat_conversations (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id VARCHAR(255) NOT NULL,
  message LONGTEXT NOT NULL,
  sender VARCHAR(50) NOT NULL,
  created_at DATETIME NOT NULL,
  INDEX idx_user_id (user_id),
  INDEX idx_created_at (created_at)
);
```

## 🛠️ Architecture

```
chatbot-service/
├── src/main/java/tn/iteam/chatbotservice/
│   ├── ChatbotServiceApplication.java
│   ├── config/
│   │   └── WebSocketConfig.java
│   ├── controller/
│   │   └── ChatbotController.java
│   ├── dto/
│   │   └── ChatMessage.java
│   ├── engine/
│   │   ├── ConversationEngine.java
│   │   └── ConversationPattern.java
│   ├── model/
│   │   └── ChatConversation.java
│   ├── repository/
│   │   └── ChatConversationRepository.java
│   ├── service/
│   │   └── ChatbotService.java
│   └── websocket/
│       ├── ChatWebSocketHandler.java
│       └── ConnectionManager.java
├── src/main/resources/
│   ├── application.properties
│   └── logback-spring.xml
└── pom.xml
```

## 🚀 Exemple d'utilisation (JavaScript)

```html
<!DOCTYPE html>
<html>
<head>
    <title>TalentConnect Chatbot</title>
</head>
<body>
    <div id="chat-box"></div>
    <input type="text" id="message-input" placeholder="Votre message...">
    <button onclick="sendMessage()">Envoyer</button>

    <script>
        const ws = new WebSocket('ws://localhost:8083/ws/chat');
        const userId = 'user_' + Date.now();

        ws.onopen = () => console.log('Connected');
        
        ws.onmessage = (event) => {
            const msg = JSON.parse(event.data);
            document.getElementById('chat-box').innerHTML += 
                `<p><strong>${msg.sender}:</strong> ${msg.message}</p>`;
        };

        function sendMessage() {
            const input = document.getElementById('message-input');
            const message = {
                sender: 'user',
                message: input.value,
                userId: userId,
                timestamp: Date.now()
            };
            ws.send(JSON.stringify(message));
            input.value = '';
        }
    </script>
</body>
</html>
```

## 📝 Logs

Les logs sont stockés dans :
- **Console** : Pour le développement
- **Fichier** : `spring.log` (rotation automatique)

## 🔐 Sécurité

- Actuellement, le service accepte toutes les origines (`*`). À adapter en production.
- Ajouter une authentification/autorisation si nécessaire.

## 🚀 Déploiement

```bash
# Build JAR
mvn clean package

# Exécuter
java -jar chatbot-service-0.0.1-SNAPSHOT.jar

# Ou utiliser Docker
docker build -t chatbot-service:1.0 .
docker run -p 8083:8083 chatbot-service:1.0
```

## 📞 Support

Pour des questions ou problèmes, contactez support@talentconnect.com

## 📄 Licences

TalentConnect - 2024

