# TalentConnect Backend API — Guide d'intégration Angular

## 🚀 Lancement du backend

```bash
cd talentconnect-backend
./mvnw spring-boot:run
# ou avec IntelliJ : Run > TalentConnectApplication
```

Le backend démarre sur **http://localhost:8080**

### Prérequis
- Java 21
- MySQL 8 sur le port 3306
- La base `talentconnect` est créée automatiquement (`createDatabaseIfNotExist=true`)

### Comptes de test (créés au démarrage si DB vide)

| Email                            | Mot de passe  | Rôle     |
|----------------------------------|---------------|----------|
| employee@talentconnect.local     | password123   | EMPLOYEE |
| rh@talentconnect.local           | password123   | HR       |
| admin@talentconnect.local        | password123   | ADMIN    |

---

## 📡 Référence des endpoints

### Format de réponse universel
```json
{ "data": { ... }, "timestamp": "2026-06-18T10:00:00Z", "status": 200 }
```
### Format erreur
```json
{ "error": "message", "status": 404, "timestamp": "..." }
```

---

### 🔐 Auth

| Méthode | URL              | Auth | Description          |
|---------|------------------|------|----------------------|
| POST    | /api/auth/login  | ❌   | Login → JWT + UserDto |

**Body login :**
```json
{ "email": "employee@talentconnect.local", "password": "password123" }
```
**Réponse :**
```json
{
  "data": {
    "token": "eyJ...",
    "user": { "id": 1, "email": "...", "role": "EMPLOYEE", ... }
  }
}
```

---

### 👤 Users — `/api/users`

| Méthode | URL                 | Rôle         | Description              |
|---------|---------------------|--------------|--------------------------|
| GET     | /api/users          | ADMIN        | Liste paginée            |
| GET     | /api/users/profile  | Tous         | Mon profil               |
| PUT     | /api/users/profile  | Tous         | Modifier mon profil      |
| GET     | /api/users/:id      | Tous         | Profil par ID            |
| POST    | /api/users          | ADMIN        | Créer un utilisateur     |
| DELETE  | /api/users/:id      | ADMIN        | Supprimer                |

---

### 💼 Offres — `/api/jobs`

| Méthode | URL                      | Rôle       | Description                          |
|---------|--------------------------|------------|--------------------------------------|
| GET     | /api/jobs                | Tous       | Recherche `?q=&location=&department=&status=&page=&size=` |
| GET     | /api/jobs/:id            | Tous       | Détail offre                         |
| POST    | /api/jobs                | HR/ADMIN   | Créer                                |
| PUT     | /api/jobs/:id            | HR/ADMIN   | Modifier                             |
| PATCH   | /api/jobs/:id/status     | HR/ADMIN   | `{ "status": "OPEN" }`               |
| DELETE  | /api/jobs/:id            | ADMIN      | Supprimer                            |

---

### 📋 Candidatures — `/api/applications`

| Méthode | URL                              | Rôle       | Description                    |
|---------|----------------------------------|------------|--------------------------------|
| GET     | /api/applications                | HR/ADMIN   | Toutes (paginé)                |
| GET     | /api/applications/mine           | EMPLOYEE   | Mes candidatures               |
| GET     | /api/applications/:id            | Tous       | Détail + timeline + documents  |
| POST    | /api/applications                | EMPLOYEE   | Postuler `{ "jobId": 1 }`      |
| PATCH   | /api/applications/:id/status     | HR/ADMIN   | `{ "status": "REVIEW" }`       |
| DELETE  | /api/applications/:id            | EMPLOYEE   | Retirer ma candidature         |

**Statuts disponibles :** `SUBMITTED → REVIEW → INTERVIEW → OFFER → HIRED / REJECTED`

---

### 🤝 Cooptations — `/api/referrals`

| Méthode | URL                 | Rôle | Description         |
|---------|---------------------|------|---------------------|
| GET     | /api/referrals/mine | Tous | Mes cooptations     |
| POST    | /api/referrals      | Tous | Créer               |
| DELETE  | /api/referrals/:id  | Tous | Supprimer la mienne |

---

### 🔔 Notifications — `/api/notifications`

| Méthode | URL                              | Description            |
|---------|----------------------------------|------------------------|
| GET     | /api/notifications               | Mes notifications      |
| PATCH   | /api/notifications/:id/read      | Marquer lue            |
| PATCH   | /api/notifications/read-all      | Tout marquer lu        |
| DELETE  | /api/notifications/:id           | Supprimer              |

---

### 📊 HR & Audit — `/api/hr`, `/api/audit`

| Méthode | URL              | Rôle     | Description                     |
|---------|------------------|----------|---------------------------------|
| GET     | /api/hr/metrics  | HR/ADMIN | `totalApplications, referrals, conversionRate...` |
| GET     | /api/audit       | HR/ADMIN | Journal d'audit paginé          |

---

## 🏗️ Intégration Angular

### 1. Copier les fichiers

```
angular-integration/
├── models/talentconnect.models.ts     → src/app/core/models/
├── interceptors/auth.interceptor.ts   → src/app/core/interceptors/
├── guards/auth.guard.ts               → src/app/core/guards/
└── services/
    ├── auth.service.ts                → src/app/core/services/
    ├── user.service.ts                → src/app/core/services/
    ├── job.service.ts                 → src/app/core/services/
    ├── application.service.ts         → src/app/core/services/
    ├── referral.service.ts            → src/app/core/services/
    ├── notification.service.ts        → src/app/core/services/
    └── hr.service.ts                  → src/app/core/services/
```

### 2. Configurer `app.config.ts`

```typescript
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { authInterceptor } from './core/interceptors/auth.interceptor';

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    provideHttpClient(withInterceptors([authInterceptor])),
  ]
};
```

### 3. Exemple d'utilisation — Login

```typescript
// login.component.ts
import { AuthService } from '@core/services/auth.service';

@Component({ ... })
export class LoginComponent {
  private auth = inject(AuthService);
  private router = inject(Router);

  login(email: string, password: string) {
    this.auth.login({ email, password }).subscribe({
      next: () => this.router.navigate(['/dashboard']),
      error: (err) => console.error('Erreur login', err)
    });
  }
}
```

### 4. Exemple — Liste des offres

```typescript
import { JobService } from '@core/services/job.service';

@Component({ ... })
export class JobsComponent implements OnInit {
  private jobService = inject(JobService);
  jobs: PageDto<JobOfferDto> | null = null;

  ngOnInit() {
    this.jobService.search({ status: 'OPEN', size: 10 }).subscribe(
      page => this.jobs = page
    );
  }
}
```

### 5. Exemple — Notifications réactives

```typescript
import { NotificationService } from '@core/services/notification.service';

@Component({ ... })
export class NavbarComponent {
  private notifService = inject(NotificationService);

  unreadCount = this.notifService.unreadCount; // Signal<number>
  notifications = this.notifService.notifications; // Signal<NotificationDto[]>

  ngOnInit() {
    this.notifService.load().subscribe();
  }
}
```

---

## 🛡️ Sécurité

- Toutes les routes sauf `POST /api/auth/login` nécessitent un token JWT
- Token à envoyer dans l'en-tête : `Authorization: Bearer <token>`
- Durée de validité : **24h** (configurable via `app.jwt.expiration`)
- CORS configuré pour : `http://localhost:4200`

---

## 🗄️ Base de données (MySQL)

| Table                  | Service         | Description                  |
|------------------------|-----------------|------------------------------|
| `users`                | auth            | Comptes utilisateurs         |
| `user_skills`          | auth            | Compétences (collection)     |
| `user_languages`       | auth            | Langues (collection)         |
| `job_offers`           | jobs            | Offres d'emploi              |
| `job_requirements`     | jobs            | Exigences (collection)       |
| `job_tags`             | jobs            | Tags (collection)            |
| `applications`         | candidatures    | Candidatures                 |
| `timeline_entries`     | candidatures    | Historique d'une candidature |
| `documents`            | candidatures    | Fichiers attachés            |
| `referrals`            | cooptation      | Cooptations                  |
| `referral_skills`      | cooptation      | Compétences cooptation       |
| `notifications`        | notifications   | Notifications utilisateur    |
| `audit_events`         | audit           | Journal d'audit RH           |

