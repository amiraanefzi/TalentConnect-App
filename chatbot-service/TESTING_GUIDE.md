## 🧪 GUIDE DE TEST - CHATBOT SERVICE

### 🚀 Démarrage rapide (4 étapes)

---

## ✅ Étape 1 : Préparer MySQL

Ouvrez MySQL Workbench ou un terminal MySQL et créez la base de données :

```sql
CREATE DATABASE IF NOT EXISTS talent_connect_chatbot;
```

> **Note** : Les tables seront créées automatiquement par Hibernate

---

## ✅ Étape 2 : Compiler le service

Ouvrez PowerShell et exécutez :

```powershell
cd "C:\Users\anefzi\OneDrive - Sopra Steria\Desktop\TalentConnect-App\TalentConnect-App-main\chatbot-service"
mvn clean package -DskipTests
```

Durée estimée : 2-3 minutes

---

## ✅ Étape 3 : Lancer le service

```powershell
# Option A : Avec Maven (Recommandé)
mvn spring-boot:run

# Option B : Avec le JAR directement
java -jar target/chatbot-service-0.0.1-SNAPSHOT.jar
```

**Vous devriez voir :**
```
INFO 12345 --- [main] c.c.ChatbotServiceApplication : Starting ChatbotServiceApplication
...
INFO 12345 --- [main] o.s.b.w.embedded.tomcat.TomcatWebServer : Tomcat started on port(s): 8083
```

✅ **Le service est prêt sur : http://localhost:8083**

---

## ✅ Étape 4 : Ouvrir l'interface Web de test

Double-cliquez sur le fichier `example.html` dans le dossier chatbot-service pour ouvrir l'interface de chat élégante.

---

## 🤖 TESTER LE CHATBOT

Une fois l'interface ouverte, essayez ces messages :

### ✅ Test 1 : Formule de politesse
```
Message : "Bonjour"
Réponse attendue : Message d'accueil du bot
```

### ✅ Test 2 : Recherche d'emploi
```
Message : "Je cherche un emploi"
Réponse attendue : Information sur les offres d'emploi disponibles
```

### ✅ Test 3 : Développement de carrière
```
Message : "Je veux développer ma carrière"
Réponse attendue : Information sur les formations et évolution professionnelle
```

### ✅ Test 4 : Avantages sociaux
```
Message : "Quels sont les avantages?"
Réponse attendue : Information sur les avantages
```

### ✅ Test 5 : Statut de candidature
```
Message : "Quel est le statut de ma candidature?"
Réponse attendue : Instructions pour vérifier le statut
```

### ✅ Test 6 : Au revoir
```
Message : "Au revoir"
Réponse attendue : Message de fermeture du bot
```

---

## 📡 TESTS AVANCÉS - API REST

### 📊 Récupérer l'historique des conversations

```powershell
curl -X GET "http://localhost:8083/api/chatbot/history/user_12345"
```

### 🔍 Récupérer les dernières conversations

```powershell
curl -X GET "http://localhost:8083/api/chatbot/recent/user_12345?limit=5"
```

### 🏥 Vérifier la santé du service

```powershell
curl -X GET "http://localhost:8083/api/chatbot/health"
```

### 🗑️ Effacer la conversation d'un utilisateur

```powershell
curl -X DELETE "http://localhost:8083/api/chatbot/history/user_12345"
```

---

## 🧪 TESTS UNITAIRES

```powershell
cd "C:\Users\anefzi\OneDrive - Sopra Steria\Desktop\TalentConnect-App\TalentConnect-App-main\chatbot-service"
mvn test
```

---

## 🐛 DÉPANNAGE

### ❌ Erreur : "Port 8083 is already in use"

```powershell
netstat -ano | findstr :8083
taskkill /PID <PID> /F
```

### ❌ Erreur : "Cannot connect to MySQL"

Vérifiez `application.properties` :
```ini
spring.datasource.url=jdbc:mysql://localhost:3306/talent_connect_chatbot
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD
```

---

## ✨ CHECKLIST DE TEST COMPLET

- [ ] MySQL démarré et base de données créée
- [ ] Service compilé (mvn clean package -DskipTests)
- [ ] Service démarré sur port 8083
- [ ] Interface HTML s'ouvre correctement
- [ ] WebSocket se connecte ("Connecté ✓")
- [ ] Envoi de messages fonctionne
- [ ] Bot répond correctement
- [ ] Messages sauvegardés en MySQL
- [ ] API REST retourne l'historique
- [ ] Tests unitaires passent (mvn test)

---

## 🎉 C'EST PRÊT !

Le chatbot backend est fonctionnel et prêt pour l'intégration frontend !

