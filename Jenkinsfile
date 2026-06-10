pipeline {
    agent {
        docker {
            image 'maven:3.10.2-eclipse-temurin-17'
            args '-v /root/.m2:/root/.m2'
        }
    }
    environment {
        MAVEN_OPTS = '-Dmaven.repo.local=.m2/repository'
    }
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        stage('Build Root App') {
            steps {
                script {
                    if (isUnix()) {
                        sh './mvnw -B -DskipTests clean package'
                    } else {
                        bat 'mvnw -B -DskipTests clean package'
                    }
                }
            }
        }
        stage('Build Services') {
            parallel {
                stage('Auth Service') {
                    steps {
                        script {
                            if (isUnix()) {
                                sh './mvnw -B -f auth-service/pom.xml -DskipTests clean package'
                            } else {
                                bat 'mvnw -B -f auth-service/pom.xml -DskipTests clean package'
                            }
                        }
                    }
                }
                stage('Chatbot Service') {
                    steps {
                        script {
                            if (isUnix()) {
                                sh './mvnw -B -f chatbot-service/pom.xml -DskipTests clean package'
                            } else {
                                bat 'mvnw -B -f chatbot-service/pom.xml -DskipTests clean package'
                            }
                        }
                    }
                }
                stage('Job Service') {
                    steps {
                        script {
                            if (isUnix()) {
                                sh './mvnw -B -f job-service/pom.xml -DskipTests clean package'
                            } else {
                                bat 'mvnw -B -f job-service/pom.xml -DskipTests clean package'
                            }
                        }
                    }
                }
            }
        }
        stage('Test') {
            steps {
                script {
                    if (isUnix()) {
                        sh './mvnw -B test'
                    } else {
                        bat 'mvnw -B test'
                    }
                }
            }
        }
    }
    post {
        always {
            archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true, allowEmptyArchive: true
            junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
        }
        success {
            echo 'Jenkins pipeline completed successfully.'
        }
        failure {
            echo 'Jenkins pipeline failed. Check the build logs for details.'
        }
    }
}
