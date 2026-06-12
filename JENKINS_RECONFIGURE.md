# 🔧 Reconfiguration Jenkins - Pipeline Correctement

## Problème Détecté

Le job Jenkins créé n'était pas configuré comme un **Pipeline** qui lit le Jenkinsfile depuis Git.

**Raison** : Le job était un **FreeStyle** vide, pas un **Pipeline**.

---

## Solution : Recréer le Job Pipeline

### Étape 1 : Supprimer l'ancien job (optionnel)

1. Va à : **http://localhost:8080/job/TalentConnect-App/**
2. Clique : **Configure** (en haut)
3. Clique : **Delete Job**
4. Confirme

### Étape 2 : Créer un nouveau Pipeline Job

1. Va à : **http://localhost:8080/**
2. Clique : **New Item** (en haut à gauche)
3. Remplis :
   ```
   Item name: TalentConnect-App
   Type: Pipeline ✓
   ```
4. Clique : **OK**

### Étape 3 : Configurer la Pipeline

Tu es maintenant dans la page de configuration du job.

**Section 1 : General**
```
Description: TalentConnect Microservices CI/CD Pipeline
Discard old builds: ✓
  Keep builds: 30
  Keep artifacts: 7 days
```

**Section 2 : Build Triggers**
```
✓ GitHub hook trigger for GITScm polling
```

**Section 3 : Pipeline**
```
Definition: Pipeline script from SCM ✓

SCM: Git ✓
  Repository URL: 
    https://github.com/amiraanefzi/TalentConnect-App.git
  
  Credentials: None (public repo)
  
  Branches to build:
    */main
  
  Script Path: Jenkinsfile ✓

Advanced Options:
  Lightweight checkout: ✓
```

5. Clique : **Save**

---

## Étape 4 : Test du Pipeline

### Test 1 : Build Manual

1. Clique : **Build Now**
2. Attends ~5-10 sec le téléchargement de maven
3. Attends ~3 min la compilation
4. Vois le résultat

### Test 2 : Voir les Logs

1. Clique sur le build (#1)
2. Clique : **Console Output**
3. Vois les stages s'exécuter en direct :
   ```
   ✓ Checkout
   ✓ Build Root App
   ✓ Build Services (parallèle)
   ✓ Tests
   ✓ Archive
   ```

### Test 3 : Vérifier les Artifacts

1. Clique sur le build (#1)
2. Voir : **Artifacts**
3. Tu devrais voir :
   ```
   talent-connect-0.0.1-SNAPSHOT.jar
   auth-service-0.0.1-SNAPSHOT.jar
   job-service-0.0.1-SNAPSHOT.jar
   chatbot-service-0.0.1-SNAPSHOT.jar
   ```

---

## Résultat Attendu

```
✓ Build #1 : SUCCESS (3-4 min)
  Stages:
    ✓ Checkout Git
    ✓ Build Root App
    ✓ Build Auth Service
    ✓ Build Chatbot Service
    ✓ Build Job Service
    ✓ Test
    ✓ Archive Artifacts
  
  Artifacts:
    ✓ 4 JARs archivés
  
  Test Reports:
    ✓ XX tests passed
```

---

## Webhook Automatique

Une fois que c'est configuré :

```
git push origin main
     ↓
GitHub webhook déclenche Jenkins
     ↓
Build s'exécute automatiquement
     ↓
Résultats en 3-4 minutes!
```

