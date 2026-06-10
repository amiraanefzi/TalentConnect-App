# Jenkins CI for TalentConnect

This repository includes a `Jenkinsfile` at the root to build the application and service modules.

## What it does

- checks out the repository
- builds the root Spring Boot application
- builds `auth-service`, `chatbot-service`, and `job-service`
- runs unit tests
- archives `.jar` artifacts
- publishes JUnit results

## Pipeline setup

1. Create a new Jenkins Pipeline or Multibranch Pipeline job.
2. Point it at this repository.
3. Ensure the Jenkins agent can run Docker, or use a Linux agent with Maven and Java 17 installed.
4. The pipeline uses the `maven:3.10.2-eclipse-temurin-17` Docker image by default.

## If your Jenkins agent is not Docker-enabled

Modify the `Jenkinsfile` agent block to use `agent any` and ensure Java 17 and Maven are installed on the agent.

## Run locally with Jenkinsfile

Jenkins will automatically execute the root `Jenkinsfile` when the pipeline is triggered.

## Notes

- The pipeline archives all generated JARs under `**/target/*.jar`.
- JUnit test reports are collected from `**/target/surefire-reports/*.xml`.
- If you want SonarCloud integration, add a dedicated `sonar` stage and configure the `SONAR_TOKEN` secret.
