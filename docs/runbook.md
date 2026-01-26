# Runbook – Multi-Country Integration Hub

This document describes how to **build, deploy, run and verify** the Multi-Country Integration Hub.
It is intended as an **operational runbook**, not as a tutorial.

The goal of this document is to:
- Remove tribal knowledge
- Make the system reproducible
- Explain *why* decisions were made, not only *how*

---

## 1. Prerequisites

Required tooling:

- JDK 21
- Node.js (LTS)
- AWS CLI v2
- AWS credentials configured locally

Verify AWS credentials before starting:

```bash
    aws configure
    aws sts get-caller-identity
```
---
## 2. Project Structure (why it exists)
This project uses a multi-module Gradle structure to mirror real-world backend systems.

### Key Decisions
- The root project acts only as an aggregator and contains no application code.
- Each service is isolated in its own Gradle module.
- Infrastructure is defined separately using AWS CDK.
- Shared dependency versions are centralized using a Gradle version catalog.
- The structure is designed for scalability, clarity, and long-term maintainability.

### This separation avoids
- Tight coupling between services
- Complex build configurations
- Difficulties in managing dependencies

### Key Directories
- `infra/` – AWS infrastructure (IaC)
- `services/` – Backend services (Spring Boot)
- `docs/` – Architecture and design notes
- `build/` – Build outputs

Notes :
-  The root project does not contain a src/ directory by design.
-  Each service owns its own lifecycle and dependencies.

## 3. Gradle multi-module
Gradle is used in multi-module mode and each module has its own responsibilities.
The Gradle wrapper (gradlew) must always be used to ensure consistent builds across environments.

### Key files
- `settings.gradle.kts` – Defines included modules.
- `build.gradle.kts` (root) – Root build configuration.
- `build.gradle.kts` – Defines plugins and dependencies specific to each module.
- `gradle/libs.versions.toml` – Centralized dependency versions.
- `libs.versions.toml` – Centralized catalog for dependencies and plugins versions.

### Common commands

- List all modules detected by Gradle::
  ```bash
      ./gradlew projects
  ```
- Build all modules::
  ```bash
      ./gradlew clean build
  ```
  Run only the ingest API service:
- ```bash
      ./gradlew :services:ingest-api:bootRun
  ```
- Build only a specific module:
- ```bash
      ./gradlew :services:ingest-api:build
  ```

## 4. Infrastructure as Code (AWS CDK)
Infrastructure is provisioned using AWS CDK.

The stack creates:
- An SQS queue for asynchronous request processing
- A DynamoDB table to persist request state
- SSM parameters to expose infrastructure outputs to applications

Infrastructure philosophy
- Applications never hardcode infrastructure values
- Infrastructure publishes outputs
- Services discover configuration dynamically

This enables environment isolation and safer deployments.

### Key Commands
Move to the infrastructure directory:
```bash
    cd infra
```
Install dependencies:
```bash
    npm install
```
Synthesize CloudFormation templates:
```bash
    npx cdk synth
```
Bootstrap the AWS environment (once per account and region):
```bash
    npx cdk bootstrap
```
Deploy the infrastructure stack:
```bash
    npx cdk deploy
```
Destroy the infrastructure stack:
```bash
    npx cdk destroy
```
Preview infrastructure changes:
```bash
    npx cdk diff
```
Example with explicit environment and profile
```bash
    npx cdk synth -c env=dev --profile integration-hub-admin
```

### Notes
- Ensure AWS credentials have sufficient permissions.
- Use separate AWS profiles for different environments.

---

## 5. Runtime Configuration (SSM)
Runtime configuration is resolved via AWS SSM Parameter Store.

Services do not embed:
- queue URLs
- table names
- environment-specific identifiers

Parameter structure
- /integration-hub/{env}/sqs/requests/url

Verify parameter existence
```bash
    aws ssm get-parameter \
  --name "/integration-hub/dev/sqs/requests/url" \
  --with-decryption \
  --region eu-west-1
```
---

## 6. Running the application
Start the ingest API
```bash
    ./gradlew :services:ingest-api:bootRun -Dapp.env=dev
```
At startup, the service will:

- Resolve the AWS region
- Read configuration from SSM
- Create the SQS client
- Expose the HTTP API

Expected log behavior:
- No hardcoded infrastructure values
- Successful SSM resolution

## 7. End-to-end verification
This section verifies the complete request flow.

Step 1: Create a request
```bash
   curl -X POST http://localhost:8080/api/requests
```
Expected response:
```json
{
  "requestId": "e3c2b7c0-7b6a-4c41-9c89-..."
}
```
Step 2: Verify message in SQS
```bash
    aws sqs receive-message \
  --queue-url <QUEUE_URL> \
  --max-number-of-messages 1 \
  --region eu-west-1
```
Expected result:
- A message containing the request ID in the message body


## Mental Model

The system follows a clear responsibility flow:

CDK provisions infrastructure
-> Infrastructure publishes outputs to SSM
-> Applications resolve configuration at runtime
-> Api enqueues requests asynchronously
-> Worker process requests independently

This model prioritizes reliability, decoupling and operational clarity.






