pipeline {
    agent {
        docker {
            // Image Maven avec Java 21 (correspondant au parent pom)
            image 'maven:3.9-eclipse-temurin-21'
            args '-v /root/.m2:/root/.m2'
        }
    }

    environment {
        MAVEN_OPTS    = '-Dmaven.repo.local=.m2/repository'
        DOCKERHUB_USER = 'amiraanefzi'
        IMAGE_PREFIX   = 'amiraanefzi'
    }

    stages {

        stage('Checkout') {
            steps { checkout scm }
        }

        // ── Build multi-module depuis la racine (UN seul appel Maven) ──
        stage('Build') {
            steps {
                sh './mvnw -B -DskipTests clean package'
            }
        }

        // ── Tests de chaque service (en parallèle) ────────────────────
        stage('Test') {
            parallel {
                stage('auth-service') {
                    steps {
                        sh './mvnw -B -pl auth-service test'
                    }
                    post {
                        always {
                            junit allowEmptyResults: true,
                                  testResults: 'auth-service/target/surefire-reports/*.xml'
                        }
                    }
                }
                stage('candidatures-service') {
                    steps {
                        sh './mvnw -B -pl candidatures-service test'
                    }
                    post {
                        always {
                            junit allowEmptyResults: true,
                                  testResults: 'candidatures-service/target/surefire-reports/*.xml'
                        }
                    }
                }
                stage('chatbot-service') {
                    steps {
                        sh './mvnw -B -pl chatbot-service test'
                    }
                    post {
                        always {
                            junit allowEmptyResults: true,
                                  testResults: 'chatbot-service/target/surefire-reports/*.xml'
                        }
                    }
                }
                stage('job-service') {
                    steps {
                        sh './mvnw -B -pl job-service test'
                    }
                    post {
                        always {
                            junit allowEmptyResults: true,
                                  testResults: 'job-service/target/surefire-reports/*.xml'
                        }
                    }
                }
            }
        }

        // ── Analyse SonarCloud ────────────────────────────────────────
        stage('SonarCloud') {
            when { branch 'main' }
            steps {
                withCredentials([string(credentialsId: 'sonar-token', variable: 'SONAR_TOKEN')]) {
                    sh """
                        ./mvnw -B verify sonar:sonar \\
                          -Dsonar.host.url=https://sonarcloud.io \\
                          -Dsonar.organization=amiraanefzi \\
                          -Dsonar.projectKey=TalentConnect-App \\
                          -Dsonar.login=\${SONAR_TOKEN}
                    """
                }
            }
        }

        // ── Docker Build (tous les services en parallèle) ─────────────
        stage('Docker Build') {
            when { branch 'main' }
            parallel {
                stage('auth-service') {
                    steps {
                        sh "docker build -f auth-service/Containerfile -t ${IMAGE_PREFIX}/auth-service:latest -t ${IMAGE_PREFIX}/auth-service:${env.BUILD_NUMBER} ."
                    }
                }
                stage('candidatures-service') {
                    steps {
                        sh "docker build -f candidatures-service/Containerfile -t ${IMAGE_PREFIX}/candidatures-service:latest -t ${IMAGE_PREFIX}/candidatures-service:${env.BUILD_NUMBER} ."
                    }
                }
                stage('chatbot-service') {
                    steps {
                        sh "docker build -f chatbot-service/Containerfile -t ${IMAGE_PREFIX}/chatbot-service:latest -t ${IMAGE_PREFIX}/chatbot-service:${env.BUILD_NUMBER} ."
                    }
                }
                stage('job-service') {
                    steps {
                        sh "docker build -f job-service/Containerfile -t ${IMAGE_PREFIX}/job-service:latest -t ${IMAGE_PREFIX}/job-service:${env.BUILD_NUMBER} ."
                    }
                }
            }
        }

        // ── Docker Push vers Docker Hub ───────────────────────────────
        stage('Docker Push') {
            when { branch 'main' }
            steps {
                script {
                    docker.withRegistry('https://index.docker.io/v1/', 'dockerhub-credentials') {
                        ['auth-service', 'candidatures-service', 'chatbot-service', 'job-service'].each { svc ->
                            sh "docker push ${IMAGE_PREFIX}/${svc}:latest"
                            sh "docker push ${IMAGE_PREFIX}/${svc}:${env.BUILD_NUMBER}"
                        }
                    }
                }
            }
        }
    }

    post {
        always {
            archiveArtifacts artifacts: '**/target/*.jar',
                             fingerprint: true,
                             allowEmptyArchive: true
        }
        success {
            echo '✅ Pipeline Jenkins terminé avec succès.'
        }
        failure {
            echo '❌ Pipeline Jenkins échoué. Consultez les logs.'
        }
    }
}
