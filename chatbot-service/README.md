# TalentConnect Chatbot Service

Optimized Spring Boot service for TalentConnect chatbot conversations.

## What Changed

- REST-first API with validated request bodies and query parameters.
- Deterministic intent engine, so tests are stable and responses are predictable.
- Constructor injection and no Lombok dependency.
- Database paging for history instead of loading all messages into memory.
- MySQL configuration through environment variables.
- Clean service folder without generated `target` files.
- Local Maven wrapper, aligned with the other services.

## Run Locally

Create or start MySQL, then run:

```bash
cd chatbot-service
mvn spring-boot:run
```

Default configuration:

```text
port: 8083
database: jdbc:mysql://localhost:3306/talent_connect_chatbot
username: root
password: empty
```

Environment overrides:

```text
CHATBOT_PORT
CHATBOT_DATASOURCE_URL
CHATBOT_DATASOURCE_USERNAME
CHATBOT_DATASOURCE_PASSWORD
CHATBOT_DDL_AUTO
CHATBOT_ALLOWED_ORIGINS
```

## API

Open the browser test page:

```text
http://localhost:8083/chat
```

Send a message:

```http
POST /api/chatbot/messages
Content-Type: application/json

{
  "userId": "user-123",
  "message": "Bonjour, je cherche un emploi"
}
```

Response:

```json
{
  "userId": "user-123",
  "message": "Bonjour, je cherche un emploi",
  "response": "Je peux vous orienter vers les offres pertinentes. Precisez le type de poste que vous cherchez.",
  "intent": "job_search",
  "createdAt": "2026-06-07T18:30:00"
}
```

Read history:

```http
GET /api/chatbot/history/{userId}?page=0&size=20
```

Read recent messages:

```http
GET /api/chatbot/recent/{userId}?limit=10
```

Clear history:

```http
DELETE /api/chatbot/history/{userId}
```

Health:

```http
GET /api/chatbot/health
GET /actuator/health
```

## Test

```bash
cd chatbot-service
mvnw.cmd test
```
