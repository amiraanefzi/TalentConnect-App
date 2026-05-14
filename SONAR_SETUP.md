# Configuration SonarCloud pour TalentConnect-App

## 🔐 Sécurité : Utiliser les variables d'environnement

**IMPORTANT :** Le token Sonar ne doit jamais être hard-codé dans les fichiers de contrôle de version !

### 1. En local (développement)

#### Option A : Via variable d'environnement (Recommandé)

```bash
# Linux/Mac
export SONAR_LOGIN="votre_token_sonar"
mvn clean verify sonar:sonar

# Windows PowerShell
$env:SONAR_LOGIN="votre_token_sonar"
mvn clean verify sonar:sonar

# Windows CMD
set SONAR_LOGIN=votre_token_sonar
mvn clean verify sonar:sonar
```

#### Option B : Via paramètre CLI

```bash
mvn clean verify sonar:sonar -Dsonar.login="votre_token_sonar"
```

#### Option C : Via fichier settings.xml (Maven)

Crée ou édite `~/.m2/settings.xml` :

```xml
<settings>
    <servers>
        <server>
            <id>sonarcloud.io</id>
            <username>amiraanefzi</username>
            <password>VOTRE_TOKEN_SONAR</password>
        </server>
    </servers>
</settings>
```

Puis exécute :
```bash
mvn clean verify sonar:sonar
```

### 2. En CI/CD (GitHub Actions)

Les secrets GitHub Actions sont définis dans `Settings > Secrets and variables > Actions`.

Voir `.github/workflows/sonarcloud.yml` pour l'utilisation.

## 📋 Récupérer votre token SonarCloud

1. Connectez-vous à [SonarCloud](https://sonarcloud.io)
2. Allez dans **Account > Security > Generate Tokens**
3. Copiez le token généré
4. **Ne le partagez jamais** ou ne le commitez **jamais**

## ✅ Commandes courantes

### Test local avec coverage
```bash
mvn clean verify sonar:sonar -Dsonar.login="$SONAR_LOGIN"
```

### Uniquement JaCoCo coverage (sans Sonar)
```bash
mvn clean verify
# Les rapports sont générés dans : 
# - auth-service/target/site/jacoco/index.html
# - job-service/target/site/jacoco/index.html
```

## 🚫 À ne jamais faire

- ❌ `git add sonar-project.properties` avec un token
- ❌ Commiter un pom.xml avec `<sonar.login>token_value</sonar.login>`
- ❌ Pousser des fichiers contenant des tokens sur GitHub

## 📝 .gitignore

Assurez-vous que `.gitignore` inclut :
```
# Credentials
.env
.env.local
settings.xml
```

## 🔍 Vérifier la sécurité

Avant de commiter, lancez :
```bash
# Chercher des tokens hard-codés
grep -r "sonar.login" . --include="*.xml" --include="*.properties"
# Ne devrait retourner que des commentaires
```

