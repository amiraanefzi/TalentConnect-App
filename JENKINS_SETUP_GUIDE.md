# 🔧 Configuration Jenkins - Guide Complet

## ÉTAPE 1 : Accéder à Jenkins
- Ouvre http://localhost:8080 dans ton navigateur
- Tu vas voir le dashboard Jenkins vide

## ÉTAPE 2 : Créer une nouvelle Pipeline Job

### 2.1 Cliquer sur "New Item" (en haut à gauche)

### 2.2 Remplir le formulaire
```
Item name: TalentConnect-App
Type: Pipeline ✓
Cliquer: OK
```

### 2.3 Configuration du Pipeline

Dans la page de configuration, trouve la section "Pipeline" :

```
Definition: Pipeline script from SCM ✓
  SCM: Git ✓
    Repository URL: https://github.com/YOUR_USERNAME/TalentConnect-App
    (ou tu peux utiliser le chemin local du projet)
    
    Credentials: None (sauf si repo privé)
    
    Branches to build: */main
    
    Script Path: Jenkinsfile ✓
    
Cliquer: Save
```

## ÉTAPE 3 : Lancer un Build

### 3.1 Dans le dashboard du job
Cliquer: "Build Now"

### 3.2 Voir la progression
- Clique sur le build "#1"
- Vois les logs en direct
- Regarde chaque stage s'exécuter

## ÉTAPE 4 : Résultats

Le build va exécuter:
1. ✓ Checkout (télécharge le code)
2. ✓ Build Root App (compile l'app root)
3. ✓ Build Services EN PARALLÈLE (Auth, Job, Chatbot)
4. ✓ Tests (lance les tests)
5. ✓ Archive artifacts (sauvegarde les JARs)

## ÉTAPE 5 : Consulter les Résultats

Après le build:
- Voir les logs complets
- Voir les artifacts (JARs)
- Voir les rapports de tests (JUnit)

---

## ⚙️ Configuration Alternative : Git Webhook

Pour que Jenkins se lance **automatiquement** à chaque commit:

### Dans GitHub:
```
Settings → Webhooks → Add webhook

Payload URL: http://YOUR_SERVER:8080/github-webhook/
Content type: application/json
Let me select individual events:
  ✓ Push events
```

### Dans Jenkins:
```
Pipeline configuration:
  Build Triggers:
    ✓ GitHub hook trigger for GITScm polling
```

Maintenant, chaque commit déclenche un build automatique! 🚀

---

## 📊 Résultat Final

Après configuration réussie, tu devrais voir:

```
Dashboard
├─ TalentConnect-App (nom du job)
│  ├─ Build #1
│  │  ├─ Stage: Checkout ✓
│  │  ├─ Stage: Build Root App ✓
│  │  ├─ Stage: Build Services (parallèle) ✓
│  │  ├─ Stage: Test ✓
│  │  └─ Result: SUCCESS ✓
│  │
│  ├─ Build #2
│  │  └─ ...
│  │
│  └─ Build #3
│     └─ ...

Artifacts:
├─ talent-connect-0.0.1-SNAPSHOT.jar
├─ auth-service-0.0.1-SNAPSHOT.jar
├─ job-service-0.0.1-SNAPSHOT.jar
└─ chatbot-service-0.0.1-SNAPSHOT.jar

Test Reports:
└─ XX tests passed ✓
```

---

## 🆘 Troubleshooting

### Erreur: "Jenkinsfile not found"
→ Assure-toi que le Jenkinsfile existe à la racine du repo

### Erreur: "Maven command not found"
→ L'agent Docker doit télécharger maven:3.9-eclipse-temurin-17
→ Attends la première exécution, c'est long

### Erreur: "Git permission denied"
→ Ajoute les credentials GitHub dans Jenkins:
  Manage Jenkins → Credentials → Add Credentials (GitHub username/token)

### Build très lent
→ Normal pour la première fois (télécharge dépendances)
→ Les builds suivants sont plus rapides
