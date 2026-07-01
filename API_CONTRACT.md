# 📋 TalentConnect — Contrat API (v2.0 — 2026-06-29)

> **Document de référence front-end ↔ back-end.**  
> Architecture microservices complète — `talentconnect-backend` (monolithe) **supprimé**.

---

## 🗺️ Architecture des services

| Service | Port | Base de données | Rôle |
|---|---|---|---|
| `auth-service` | **8081** | MySQL `talentconnect_auth` | Auth, Users, Profils, Rôles |
| `file-service` | **8082** | MySQL `talentconnect_files` | Upload / Download / Suppression fichiers |
| `chatbot-service` | **8083** | MySQL `talent_connect_chatbot` | Chatbot IA |
| `candidatures-service` | **8084** | MySQL `candidatures_db` | Candidatures + Notifications |
| `job-service` | **8085** | MySQL `talentconnect_jobs` | Offres + Cooptation + Audit + HR Metrics |

> ✅ **Port 8080 est libre.** Plus de conflit. Chaque service a son port dédié.

---

## 🔐 1. Authentification & Sécurité

### Mécanisme

| Paramètre | Valeur |
|---|---|
| Type | **JWT Bearer Token** |
| Header HTTP | `Authorization: Bearer <token>` |
| Algorithme | HS256 |
| Expiration — auth-service | **1 h** (3 600 s) |
| CORS autorisé | `http://localhost:4200` |
| Secret JWT partagé (dev) | `dev-local-only-secret-change-in-prod-32ch` |

> ⚠️ **`candidatures-service` et `file-service` utilisent des headers HTTP** pour l'identité (`X-User-Id: Long`, `X-Role: String`) au lieu de JWT.

### Rôles

| Service | Valeurs |
|---|---|
| `auth-service` / `job-service` (JWT) | `EMPLOYE`, `RH`, `ADMIN` |
| `candidatures-service` / `file-service` (headers) | `EMPLOYEE`, `RH` |

---

## 📦 2. Format de réponse

### auth-service / job-service
Retournent les objets **directement** (pas de wrapper `ApiResponse`).

### candidatures-service — Erreur
```json
{
  "timestamp": "2026-06-29T10:15:30.123456Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Le nouveau statut est obligatoire",
  "path": "/api/candidatures/5/status"
}
```

### Pagination — candidatures-service (`PageResponse<T>`)
```json
{
  "content": [ ... ],
  "page": 0,
  "size": 20,
  "totalElements": 87,
  "totalPages": 5
}
```

### Pagination — auth-service (`GET /api/users`)
```json
{
  "content": [ ... ],
  "page": 0,
  "size": 20,
  "totalElements": 5,
  "totalPages": 1
}
```

### Pagination — job-service (Spring natif)
```json
{
  "content": [ ... ],
  "pageable": { ... },
  "totalElements": 12,
  "totalPages": 1
}
```

---

## 🔑 A. AUTHENTIFICATION — `auth-service` port 8081

### `POST /api/auth/register`
**Auth :** Non — PUBLIC

**Body :**
```json
{ "email": "user@talentconnect.tn", "password": "motdepasse8chars" }
```
**Réponse 200 :**
```json
{
  "accessToken": "eyJ...",
  "tokenType": "Bearer",
  "userId": 42,
  "email": "user@talentconnect.tn",
  "roles": ["EMPLOYE"]
}
```
**Codes erreur :** `400`, `409` (email déjà utilisé)

---

### `POST /api/auth/login`
**Auth :** Non — PUBLIC

**Body :** identique à `/register`

**Réponse 200 :** identique à `/register` (champ `accessToken`)

**Codes erreur :** `400`, `401`

---

## 👤 B. UTILISATEURS — `auth-service` port 8081

> **Auth :** JWT Bearer (`Authorization: Bearer <token>`)

### `GET /api/users/profile`
**Rôles :** Tout utilisateur authentifié

**Réponse 200 :** `UserDto` (profil de l'utilisateur connecté)
```json
{
  "id": 1,
  "employeeId": "EMP-0001",
  "email": "h.yahyaoui@soprasteria.com",
  "roles": ["EMPLOYE"],
  "enabled": true,
  "createdAt": "2026-06-01T08:00:00Z",
  "firstName": "Hamza",
  "lastName": "Yahyaoui",
  "department": "Engineering",
  "location": "Tunis",
  "title": "Développeur Java",
  "experienceYears": 3,
  "avatarUrl": null,
  "phone": null,
  "address": null,
  "bio": null,
  "linkedinUrl": null,
  "githubUrl": null,
  "languages": ["FR"],
  "skills": ["Java", "Angular"],
  "formations": [],
  "experiences": []
}
```

---

### `PUT /api/users/profile`
**Rôles :** Tout utilisateur authentifié

**Body (champs optionnels) :**
```json
{
  "firstName": "string", "lastName": "string",
  "department": "string", "location": "string", "title": "string",
  "experienceYears": 3, "avatarUrl": "string",
  "phone": "string", "address": "string", "bio": "string",
  "linkedinUrl": "string", "githubUrl": "string",
  "languages": ["FR","EN"], "skills": ["Java","Spring"],
  "formations": [{"institution":"string","degree":"string","fieldOfStudy":"string","startYear":2018,"endYear":2022}],
  "experiences": [{"company":"string","title":"string","location":"string","startDate":"2022-01","endDate":null,"current":true,"description":"string"}]
}
```
**Réponse 200 :** `UserDto` mis à jour

---

### `GET /api/users`
**Rôles :** `ADMIN`  
**Query :** `page` (défaut 0), `size` (défaut 20)

**Réponse 200 :** `{ content: [...], page, size, totalElements, totalPages }`

---

### `GET /api/users/{id}`
**Rôles :** `ADMIN`, `RH`  
**Réponse 200 :** `UserDto` | **404**

---

### `POST /api/users`
**Rôles :** `ADMIN`

**Body :**
```json
{
  "email": "string (requis, unique)",
  "password": "string (requis, 8-72 chars)",
  "roles": ["EMPLOYE","RH","ADMIN"]
}
```
**Réponse 201 :** `UserDto` — `employeeId` auto-généré (ex: `EMP-0006`)  
**Codes erreur :** `400`, `409`

---

### `DELETE /api/users/{id}`
**Rôles :** `ADMIN`  
**Réponse :** `204 No Content` | **404**

---

### `POST /api/admin/users`
**Rôles :** `ADMIN` — identique à `POST /api/users`

### `GET /api/admin/users`
**Rôles :** `ADMIN`  
**Réponse 200 :** `List<UserDto>`

### `PUT /api/admin/users/{id}`
**Rôles :** `ADMIN` — modifier le profil complet d'un utilisateur

### `PATCH /api/admin/users/{id}/roles`
**Body :** `{ "roles": ["ADMIN","EMPLOYE"] }`  
**Réponse 200 :** `UserDto`

### `PATCH /api/admin/users/{id}/enabled/{enabled}`
**Path :** `enabled: true|false`  
**Réponse 200 :** `UserDto`

### `DELETE /api/admin/users/{id}`
**Rôles :** `ADMIN`  
**Réponse :** `204 No Content`

---

## 💼 C. OFFRES D'EMPLOI — `job-service` port 8085

> **Auth :** JWT Bearer (même secret que auth-service)  
> **IDs :** UUID (String de 36 caractères)

### `GET /api/jobs`
**Auth :** Oui — JWT

**Query params :**
| Param | Type | Description |
|---|---|---|
| `q` | string | Recherche textuelle |
| `location` | string | Localisation |
| `employmentType` | enum | `FULL_TIME \| PART_TIME \| CONTRACT \| INTERN \| TEMPORARY` |
| `remote` | boolean | Télétravail |
| `page` | int | Page (Spring Pageable) |
| `size` | int | Taille |
| `sort` | string | ex: `publishedAt,desc` |

> ⚠️ Retourne uniquement les offres **publiées** (`published: true`).

**Réponse 200 :** `Page<JobOfferResponse>`
```json
{
  "content": [{
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "title": "Développeur Java Senior",
    "companyName": "Sopra Steria",
    "location": "Tunis",
    "employmentType": "FULL_TIME",
    "experienceLevel": "SENIOR",
    "remote": false,
    "description": "...",
    "salaryMin": 2000, "salaryMax": 3500, "currency": "TND",
    "published": true,
    "publishedAt": "2026-06-01T09:00:00Z",
    "createdAt": "2026-05-30T10:00:00Z",
    "updatedAt": "2026-06-01T09:00:00Z"
  }],
  "totalElements": 12, "totalPages": 1
}
```

---

### `GET /api/jobs/{id}`
**Path :** `id: UUID`  
**Réponse 200 :** `JobOfferResponse` (uniquement si published=true) | **404**

---

### `POST /api/admin/jobs`
**Rôles :** `ADMIN`

**Body :**
```json
{
  "title": "string (requis, max 140)",
  "companyName": "string (requis, max 140)",
  "location": "string (requis, max 140)",
  "employmentType": "FULL_TIME | PART_TIME | CONTRACT | INTERN | TEMPORARY (requis)",
  "experienceLevel": "JUNIOR | MID | SENIOR | LEAD (requis)",
  "remote": true,
  "description": "string (requis, max 50000)",
  "salaryMin": 2000, "salaryMax": 3500, "currency": "TND",
  "published": false
}
```
**Réponse 201 :** `JobOfferResponse` + Header `Location`  
**Codes erreur :** `400`, `403`

---

### `PUT /api/admin/jobs/{id}`
**Rôles :** `ADMIN`  
**Body :** identique à POST (tous champs requis)  
**Réponse 200 :** `JobOfferResponse` | **400, 403, 404**

---

### `PATCH /api/admin/jobs/{id}/published`
**Rôles :** `RH`, `ADMIN`

**Body :**
```json
{ "published": true }
```
**Règle métier :** Si `published: true` et `publishedAt` est null → `publishedAt` = maintenant.  
**Réponse 200 :** `JobOfferResponse` | **403, 404**

---

### `DELETE /api/admin/jobs/{id}`
**Rôles :** `ADMIN`  
**Réponse :** `204 No Content` | **403, 404**

---

## 🤝 D. COOPTATION (REFERRALS) — `job-service` port 8085

> **Auth :** JWT Bearer

### `GET /api/referrals/mine`
**Rôles :** `EMPLOYE`, `RH`, `ADMIN`  
**Réponse 200 :** `List<ReferralResponse>`
```json
[{
  "id": 1,
  "referrerEmail": "h.yahyaoui@soprasteria.com",
  "candidateFullName": "Yassine Ben Ahmed",
  "candidateEmail": "yassine@email.com",
  "candidatePhone": "+21698765432",
  "linkedIn": "https://linkedin.com/in/yassine",
  "targetJobId": "uuid",
  "skills": ["Java","Angular"],
  "cvFileId": "uuid-fichier",
  "status": "SUBMITTED",
  "createdAt": "2026-06-28T09:00:00Z"
}]
```

---

### `GET /api/referrals`
**Rôles :** `RH`, `ADMIN`  
**Réponse 200 :** `List<ReferralResponse>`

---

### `POST /api/referrals`
**Rôles :** `EMPLOYE`, `RH`, `ADMIN`

**Body :**
```json
{
  "candidateFullName": "string (requis)",
  "candidateEmail": "string",
  "candidatePhone": "string",
  "linkedIn": "string",
  "targetJobId": "uuid",
  "skills": ["Java"],
  "cvFileId": "uuid-fichier",
  "notes": "string"
}
```
**Réponse 201 :** `ReferralResponse` | **400, 404**

---

### `PATCH /api/referrals/{id}/status`
**Rôles :** `RH`, `ADMIN`

**Body :** `{ "status": "REVIEW" }`  
**Valeurs :** `SUBMITTED`, `REVIEW`, `INTERVIEW`, `OFFER`, `HIRED`, `REJECTED`  
**Réponse 200 :** `ReferralResponse`

---

### `DELETE /api/referrals/{id}`
**Rôles :** Propriétaire (`EMPLOYE`)  
**Réponse :** `204 No Content` | **403, 404**

---

## 📊 E. AUDIT & HR METRICS — `job-service` port 8085

> **Auth :** JWT Bearer

### `GET /api/audit`
**Rôles :** `RH`, `ADMIN`

**Query :** `page` (défaut 0), `size` (défaut 20), `sortBy` (défaut `timestamp`), `dir` (`asc|desc`)

**Réponse 200 :** `Page<AuditEvent>` (Spring natif)

---

### `GET /api/hr/metrics` _(job-service)_
**Rôles :** `RH`, `ADMIN`

**Réponse 200 :**
```json
{
  "totalJobOffers": 15,
  "publishedJobs": 8,
  "totalReferrals": 25,
  "hiredFromReferral": 3,
  "auditEventsToday": 12,
  "totalAuditEvents": 150
}
```

---

## 📝 F. CANDIDATURES — `candidatures-service` port 8084

> **Auth simulée via headers :** `X-User-Id: Long`, `X-Role: String`  
> Valeurs acceptées pour `X-Role` : `EMPLOYEE`, `RH`

### `POST /api/candidatures`
**Headers :** `X-User-Id`, `X-Role: EMPLOYEE`

**Body :**
```json
{
  "offerId": 5,
  "type": "INTERNE | RECOMMANDATION (requis)",
  "referralId": null
}
```
**Validations :** `offerId` requis et positif ; `type` requis.  
**Règle :** Un utilisateur ne peut pas postuler deux fois à la même offre → `409 Conflict`.

**Réponse 201 :** `CandidatureResponse`
```json
{
  "id": 1, "offerId": 5, "applicantUserId": 42,
  "type": "INTERNE", "status": "SOUMISE",
  "createdAt": "2026-06-29T10:00:00Z",
  "updatedAt": "2026-06-29T10:00:00Z",
  "cvFileId": null, "referralId": null
}
```

---

### `GET /api/candidatures/me`
**Headers :** `X-User-Id`, `X-Role: EMPLOYEE`

**Query :** `status`, `type`, `offerId`, `page` (défaut 0), `size` (défaut 20, max 100)

**Réponse 200 :** `PageResponse<CandidatureResponse>`

---

### `GET /api/candidatures/{id}`
**Headers :** `X-User-Id`, `X-Role: EMPLOYEE ou RH`

**Réponse 200 :** `CandidatureDetailsResponse`
```json
{
  "candidature": { ... },
  "history": [{
    "id": 1, "fromStatus": "SOUMISE", "toStatus": "EN_COURS",
    "changedAt": "2026-06-20T14:30:00Z", "changedBy": 1, "changedByRole": "RH"
  }]
}
```

---

### `GET /api/candidatures`
**Headers :** `X-User-Id`, `X-Role: RH`

**Query :** `status`, `type`, `offerId`, `applicantUserId`, `page`, `size`

**Réponse 200 :** `PageResponse<CandidatureResponse>`

---

### `PATCH /api/candidatures/{id}/status`
**Headers :** `X-User-Id`, `X-Role: RH`

**Body :** `{ "newStatus": "EN_COURS" }`

**Valeurs :** `SOUMISE` → `EN_COURS` → `ENTRETIEN` → `RECRUTEE` | `REFUSEE`

**Réponse 200 :** `CandidatureResponse`

---

### `PATCH /api/candidatures/{id}/cv`
**Headers :** `X-User-Id`, `X-Role: EMPLOYEE ou RH`

**Body :** `{ "cvFileId": "uuid-fichier-max-80-chars" }`

**Réponse 200 :** `CandidatureResponse` avec `cvFileId` renseigné

---

### `DELETE /api/candidatures/{id}`
**Headers :** `X-User-Id`, `X-Role: EMPLOYEE`

**Règle :** Seul le propriétaire peut retirer sa candidature.

**Réponse :** `204 No Content` | **403 (pas le propriétaire), 404**

---

## 🔔 G. NOTIFICATIONS — `candidatures-service` port 8084

> **Auth via headers :** `X-User-Id`, `X-Role`

### `GET /api/notifications`
**Réponse 200 :** `List<NotificationResponse>`
```json
[{
  "id": 1, "userId": 42,
  "type": "INFO", "title": "Statut mis à jour",
  "message": "Votre candidature #1 est maintenant : EN_COURS",
  "read": false, "deepLink": "/candidatures/1",
  "createdAt": "2026-06-29T09:00:00Z"
}]
```

### `GET /api/notifications/unread-count`
**Réponse 200 :** `{ "count": 3 }`

### `PATCH /api/notifications/{id}/read`
**Réponse 200 :** `NotificationResponse` avec `read: true`

### `PATCH /api/notifications/read-all`
**Réponse :** `204 No Content`

### `DELETE /api/notifications/{id}`
**Réponse :** `204 No Content`

---

## 📊 H. HR METRICS — `candidatures-service` port 8084

### `GET /api/hr/metrics` _(candidatures-service)_
**Headers :** `X-User-Id`, `X-Role: RH`

**Réponse 200 :**
```json
{
  "totalCandidatures": 87,
  "soumises": 20,
  "enCours": 30,
  "entretiens": 15,
  "refusees": 12,
  "recrutees": 10,
  "internalCandidates": 62,
  "referrals": 25,
  "conversionRate": 11.49
}
```

---

## 📁 I. FICHIERS — `file-service` port 8082

> **Auth via headers :** `X-User-Id: Long` (requis pour upload/delete), `X-Role: String`

### `POST /api/files/upload`
**Headers :** `X-User-Id`, `X-Role`  
**Content-Type :** `multipart/form-data`  
**Param form :** `file: MultipartFile`  
**Limite :** 10 MB

**Réponse 200 :**
```json
{ "fileId": "550e8400-e29b-41d4-a716-446655440000", "fileName": "cv.pdf", "size": "245678" }
```

---

### `GET /api/files/{fileId}/metadata`
**Réponse 200 :**
```json
{ "fileId": "...", "fileName": "cv.pdf", "contentType": "application/pdf", "sizeBytes": 245678, "uploadedAt": "2026-06-29T10:00:00Z" }
```

### `GET /api/files/{fileId}/download`
**Réponse 200 :** Fichier binaire (Content-Disposition: attachment)

### `DELETE /api/files/{fileId}`
**Headers :** `X-User-Id`  
**Réponse :** `204 No Content`

### `GET /api/files/health`
**Réponse 200 :** `{ "status": "UP", "service": "file-service" }`

---

## 🤖 J. CHATBOT — `chatbot-service` port 8083

### `POST /api/chatbot/messages`
**Auth :** Non

**Body :**
```json
{ "userId": "user-42 (max 100)", "message": "Comment postuler ? (max 2000)" }
```
**Réponse 200 :**
```json
{
  "userId": "user-42", "message": "Comment puis-je postuler ?",
  "response": "Pour postuler, rendez-vous sur la section Offres.",
  "intent": "APPLY_JOB", "createdAt": "2026-06-29T10:00:00"
}
```

### `GET /api/chatbot/history/{userId}`
**Query :** `page` (défaut 0), `size` (défaut 20, max 100)  
**Réponse 200 :** `Page<ConversationResponse>`

### `GET /api/chatbot/recent/{userId}`
**Query :** `limit` (défaut 10, max 100)  
**Réponse 200 :** `List<ConversationResponse>`

### `DELETE /api/chatbot/history/{userId}`
**Réponse :** `204 No Content`

### `GET /api/chatbot/health`
**Réponse 200 :** `{ "status": "UP", "service": "chatbot-service" }`

---

## 🏷️ 3. Énumérés

### Rôles JWT (auth-service / job-service)
`EMPLOYE` | `RH` | `ADMIN`

### Rôles headers (candidatures-service / file-service)
`EMPLOYEE` | `RH`

### Statuts candidature (candidatures-service)
`SOUMISE` → `EN_COURS` → `ENTRETIEN` → `RECRUTEE` | `REFUSEE`

### Types candidature
`INTERNE` | `RECOMMANDATION`

### Types de contrat (job-service)
`FULL_TIME` | `PART_TIME` | `CONTRACT` | `INTERN` | `TEMPORARY`

### Niveaux d'expérience (job-service)
`JUNIOR` | `MID` | `SENIOR` | `LEAD`

### Statuts cooptation (job-service)
`SUBMITTED` | `REVIEW` | `INTERVIEW` | `OFFER` | `HIRED` | `REJECTED`

### Types de notifications (candidatures-service)
`SUCCESS` | `INFO` | `WARNING` | `ERROR`

---

## 📅 4. Format des dates

| Service | Format |
|---|---|
| `auth-service`, `job-service`, `candidatures-service` | `Instant` — JSON : `"2026-06-29T10:00:00Z"` (UTC ISO 8601) |
| `chatbot-service` | `LocalDateTime` — JSON : `"2026-06-29T10:00:00"` (sans timezone) |

---

## 🌐 5. CORS et headers spéciaux

| Service | Auth | Headers spéciaux |
|---|---|---|
| `auth-service` (8081) | `Authorization: Bearer <token>` | — |
| `job-service` (8085) | `Authorization: Bearer <token>` | — |
| `candidatures-service` (8084) | Aucun JWT | `X-User-Id: Long`, `X-Role: String` |
| `file-service` (8082) | Aucun JWT | `X-User-Id: Long`, `X-Role: String` |
| `chatbot-service` (8083) | Aucun | — |

---

## 📮 6. Exemples curl

```bash
# Login
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@talentconnect.tn","password":"Admin@Dev123"}'

# Lister les offres publiées
curl http://localhost:8085/api/jobs?size=10 \
  -H "Authorization: Bearer <TOKEN>"

# Créer une offre (ADMIN)
curl -X POST http://localhost:8085/api/admin/jobs \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{"title":"Dev Java","companyName":"Sopra","location":"Tunis","employmentType":"FULL_TIME","experienceLevel":"SENIOR","remote":false,"description":"...","published":false}'

# Publier une offre
curl -X PATCH http://localhost:8085/api/admin/jobs/{uuid}/published \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{"published":true}'

# Uploader un CV
curl -X POST http://localhost:8082/api/files/upload \
  -H "X-User-Id: 42" -H "X-Role: EMPLOYEE" \
  -F "file=@/path/to/cv.pdf"

# Postuler (candidatures-service)
curl -X POST http://localhost:8084/api/candidatures \
  -H "X-User-Id: 42" -H "X-Role: EMPLOYEE" \
  -H "Content-Type: application/json" \
  -d '{"offerId":1,"type":"INTERNE"}'

# Retirer une candidature
curl -X DELETE http://localhost:8084/api/candidatures/1 \
  -H "X-User-Id: 42" -H "X-Role: EMPLOYEE"

# Message chatbot
curl -X POST http://localhost:8083/api/chatbot/messages \
  -H "Content-Type: application/json" \
  -d '{"userId":"user-42","message":"Comment postuler ?"}'
```

---

## ⚠️ 7. Points d'attention pour le front-end

| Point | Détail |
|---|---|
| **Deux mécanismes d'auth** | JWT pour ports 8081/8085 ; `X-User-Id`+`X-Role` headers pour 8082/8084 |
| **Rôles EMPLOYE vs EMPLOYEE** | JWT retourne `EMPLOYE` ; headers candidatures/file attendent `EMPLOYEE`. Le front doit mapper `EMPLOYE→EMPLOYEE` pour les headers. |
| **IDs offres** | UUID (String 36 chars) dans job-service |
| **Statuts FR** | candidatures-service : `SOUMISE/EN_COURS/ENTRETIEN/REFUSEE/RECRUTEE` |
| **Dates sans Z** | chatbot-service retourne `LocalDateTime` sans `Z`. Ajouter `Z` ou parser comme local. |
| **Secret JWT dev** | `dev-local-only-secret-change-in-prod-32ch` — changer en prod via `APP_JWT_SECRET` env var |
| **HR Metrics** | Deux endpoints : `GET /api/hr/metrics` sur 8085 (offres/referrals) ET sur 8084 (candidatures) |

