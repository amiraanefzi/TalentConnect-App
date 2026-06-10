// Script Groovy pour configurer le Pipeline Jenkins
// Exécute ceci dans Jenkins Script Console

import jenkins.model.Jenkins
import hudson.model.FreeStyleProject
import hudson.plugins.git.GitSCM
import org.jenkinsci.plugins.workflow.job.WorkflowJob
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition
import com.cloudbees.plugins.credentials.CredentialsProvider
import com.cloudbees.plugins.credentials.domains.Domain

def jenkins = Jenkins.getInstance()
def projectName = "TalentConnect-App"

// Vérifier si le job existe
def job = jenkins.getItem(projectName)

if (job == null) {
    println("Creating new Pipeline job: ${projectName}")

    // Créer un nouveau job Pipeline
    def flowDef = new CpsFlowDefinition(
        """
        pipeline {
            agent {
                docker {
                    image 'maven:3.9-eclipse-temurin-17'
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
                        sh './mvnw -B -DskipTests clean package'
                    }
                }
                stage('Build Services') {
                    parallel {
                        stage('Auth Service') {
                            steps {
                                sh './mvnw -B -f auth-service/pom.xml -DskipTests clean package'
                            }
                        }
                        stage('Chatbot Service') {
                            steps {
                                sh './mvnw -B -f chatbot-service/pom.xml -DskipTests clean package'
                            }
                        }
                        stage('Job Service') {
                            steps {
                                sh './mvnw -B -f job-service/pom.xml -DskipTests clean package'
                            }
                        }
                    }
                }
                stage('Test') {
                    steps {
                        sh './mvnw -B test'
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
        """, true
    )

    job = new WorkflowJob(jenkins, projectName)
    job.setDefinition(flowDef)
    jenkins.add(job, projectName)
    jenkins.save()

    println("✅ Pipeline job '${projectName}' created successfully!")
} else {
    println("Job '${projectName}' already exists")
}
