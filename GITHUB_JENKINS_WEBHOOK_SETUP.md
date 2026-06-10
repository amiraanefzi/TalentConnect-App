# 🔗 Configuration GitHub + Jenkins Webhook

## PARTIE 1 : Configuration Jenkins (Préalable)

### Étape 1 : Installer le Plugin GitHub dans Jenkins

1. Accède à Jenkins : **http://localhost:8080**
2. Clique : **Manage Jenkins** (en bas à gauche)
3. Clique : **Manage Plugins**
4. Onglet : **Available plugins**
5. Recherche : **GitHub**
6. Sélectionne : **GitHub plugin** (par CloudBees)
7. Clique : **Install without restart**
8. Attends l'installation
9. Redémarre Jenkins (optionnel)

### Étape 2 : Configurer le Pipeline pour GitHub

1. Va au job : **TalentConnect-App**
2. Clique : **Configure**
3. Aller à la section : **Build Triggers**
4. ✓ Cocher : **GitHub hook trigger for GITScm polling**
   ```
   ☑ GitHub hook trigger for GITScm polling
   ```
5. Clique : **Save**

---

## PARTIE 2 : Configuration GitHub Webhook

### Étape 1 : Obtenir l'URL Jenkins

Tu as besoin de l'URL publique de Jenkins. Puisque tu es en local :

```
http://localhost:8080
```

⚠️ **IMPORTANT** : GitHub doit accéder à Jenkins !
- Si Jenkins est en local : Besoin d'un tunnel (ngrok)
- Si Jenkins est en ligne : L'URL public suffit

### Étape 2A : Tunnel avec ngrok (Local Jenkins)

Si ton Jenkins est local, tu dois créer un tunnel vers l'internet :

```bash
# 1. Télécharge ngrok : https://ngrok.com/download

# 2. Lance ngrok pour exposer le port 8080
ngrok http 8080

# 3. Tu vas voir une URL comme :
# https://xxxx-yyyy-zzzz.ngrok.io

# C'est l'URL qu'on utilise pour GitHub!
```

**Garde ngrok en cours d'exécution !**

### Étape 2B : Jenkins Public (Production)

Si Jenkins est sur un serveur public :
```
https://jenkins.monentreprise.com
```

---

## PARTIE 3 : Créer le Webhook GitHub

### Étape 1 : Aller sur GitHub

1. Ouvre : **https://github.com/amiraanefzi/TalentConnect-App**
2. Clique : **Settings**
3. Clique : **Webhooks** (à gauche)

### Étape 2 : Ajouter un Webhook

```
Clique : Add webhook
```

### Étape 3 : Remplir le formulaire

```
Payload URL:
  http://localhost:8080/github-webhook/
  
  OU (avec ngrok):
  https://xxxx-yyyy-zzzz.ngrok.io/github-webhook/

Content type:
  application/json

Events:
  ✓ Let me select individual events
    ✓ Push events          ← Important!
    ✓ Pull requests
    (optionnel: Pull request reviews, Issues)

Active:
  ☑ Active

Clique: Add webhook
```

### Étape 4 : Vérifier

Tu devrais voir :
```
✓ Webhook created successfully
```

Tu peux aussi voir le dernier test :
```
Recent Deliveries → Montre les tentatives de webhook
```

---

## PARTIE 4 : Tester la Configuration

### Test 1 : Webhook Delivery (GitHub)

1. Sur le webhook nouvellement créé
2. Clique : **Recent Deliveries**
3. Clique sur la dernière tentative
4. Vois les détails :
   - Request
   - Response (200 = OK, 404 = Error)

### Test 2 : Lancer un Build (Manuel d'abord)

1. Va sur Jenkins : **TalentConnect-App**
2. Clique : **Build Now**
3. Vois le build #X se lancer

### Test 3 : Faire un Commit et Pusher

```bash
# 1. Fais une modification
echo "Test webhook" >> README.md

# 2. Commit et push
git add README.md
git commit -m "Test webhook trigger"
git push origin main
```

**Attends 5-10 secondes...**

Puis va sur Jenkins :
```
TalentConnect-App → Tu devrais voir un nouveau build!
```

---

## 🔍 Dépannage

### Erreur : "404 Not Found" sur le webhook

**Cause** : Jenkins n'est pas accessible

**Solutions** :
1. Jenkins est-il en cours d'exécution?
   ```bash
   podman ps | grep jenkins
   ```

2. Ports corrects?
   ```bash
   curl http://localhost:8080  # Doit répondre
   ```

3. URL webhook correcte?
   - Locale : `http://localhost:8080/github-webhook/`
   - Ngrok : `https://xxxx-yyyy.ngrok.io/github-webhook/`

### Erreur : "Connection refused"

**Cause** : Jenkins est down ou port mauvais

**Solution** :
```bash
# Redémarre Jenkins
podman restart jenkins-server

# Ou relance
podman stop jenkins-server
podman rm jenkins-server
podman run -d --name jenkins-server -p 8080:8080 -p 50000:50000 -v jenkins_home:/var/jenkins_home jenkins/jenkins:lts
```

### Webhook ne déclenche pas le build

**Vérifier** :
1. ✓ Plugin GitHub installé dans Jenkins?
2. ✓ Build Trigger activé dans le job?
3. ✓ Webhook créé sur GitHub?
4. ✓ L'URL webhook est correcte?
5. ✓ Choisir "Push events" sur GitHub?

**Debug** :
- Va sur GitHub Webhook → Recent Deliveries
- Vois le Response body (error message)
- Copie le message d'erreur dans les logs Jenkins

---

## ✅ Workflow Complet Automatisé

Une fois configuré, voici le flux :

```
┌──────────────────────────────────┐
│ Developer                        │
│                                  │
│ $ git push origin main           │
└──────────────────────────────────┘
         ↓ (2 sec)
┌──────────────────────────────────┐
│ GitHub                           │
│                                  │
│ Reçoit le push                   │
│ Envoie un webhook POST           │
└──────────────────────────────────┘
         ↓ (1 sec)
┌──────────────────────────────────┐
│ Jenkins                          │
│                                  │
│ Reçoit le webhook                │
│ Crée un nouveau build            │
└──────────────────────────────────┘
         ↓ (3 min)
┌──────────────────────────────────┐
│ Pipeline Executes                │
│                                  │
│ 1. Checkout                      │
│ 2. Build Root                    │
│ 3. Build Services (parallèle)    │
│ 4. Tests                         │
│ 5. Archive Artifacts             │
└──────────────────────────────────┘
         ↓ (OK ou FAIL)
┌──────────────────────────────────┐
│ Notification (Slack/Email)       │
│                                  │
│ "Build #X SUCCESS/FAILED"        │
└──────────────────────────────────┘
```

**Total : ~3 min du commit au résultat! 🚀**

---

## 📝 Résumé Configuration

| Étape | Fait |
|-------|------|
| 1. Plugin GitHub installé | ✓ |
| 2. Build Trigger activé | ✓ |
| 3. Webhook GitHub créé | ✓ |
| 4. URL correcte | ✓ |
| 5. Push events sélectionnés | ✓ |
| 6. Webhook testé | ✓ |

Quand tout est ✓ → Jenkins se déclenche automatiquement!

