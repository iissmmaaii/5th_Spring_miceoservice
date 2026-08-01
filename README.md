# CypherVault Core Banking Microservices

> **ظ…ظ„ط®طµ ط¹ط±ط¨ظٹ:** ظٹط­طھظˆظٹ ظ‡ط°ط§ ط§ظ„ظ…ط³طھظˆط¯ط¹ ط¹ظ„ظ‰ ط§ظ„ط®ط¯ظ…ط§طھ ط§ظ„ط®ظ„ظپظٹط© ط§ظ„ط£ط³ط§ط³ظٹط© ظ„ظ…ظ†طµط© **CypherVault**طŒ ط¨ظ…ط§ ظپظٹ ط°ظ„ظƒ ط§ظ„ظ‡ظˆظٹط© ظˆط§ظ„ظ…طµط§ط¯ظ‚ط©طŒ ط§ظ„ط­ط³ط§ط¨ط§طھ ظˆط§ظ„طھط­ظˆظٹظ„ط§طھطŒ ط¥ط¯ط§ط±ط© ظˆط«ط§ط¦ظ‚ ط§ظ„طھط­ظ‚ظ‚طŒ ط§ظ„ط¥ط´ط¹ط§ط±ط§طھطŒ ط¨ظˆط§ط¨ط© ط§ظ„ط¯ط®ظˆظ„طŒ ظˆط§ظ„طھظƒط§ظ…ظ„ ط§ظ„ظ…ظ‚ظٹظ‘ط¯ ظ…ط¹ ط§ظ„ط³ط¬ظ„ ط§ظ„ظ…ط¤ط³ط³ظٹ ط§ظ„ظ…ط±ط®ظ‘طµ.

CypherVault is a distributed digital-banking proof of concept. This repository contains the Java/Spring services that own the core business rules, coordinate long-running financial operations, publish domain events, and expose the secured API entry point used by the client applications.

## Repository role

This repository is the core backend and integration layer. It works together with the following repositories:

- Mobile client: https://github.com/iissmmaaii/cyphervault_mobile.git
- Chat and realtime support: https://github.com/iissmmaaii/chat_service.git
- AI, KYC and risk decision service: https://github.com/iissmmaaii/cyphervault-ai-service.git

## Main capabilities

- Public-key user registration and challenge-response authentication.
- JWT-based sessions and trusted identity propagation through the gateway.
- KYC document upload, administrative review and eligibility finalization.
- Bank-account opening, balance queries and signed money transfers.
- Long-running transfer saga with transactional outbox and reconciliation.
- Explainable transaction-risk decision integration.
- Kafka-based asynchronous coordination and duplicate-event protection.
- Controlled integration with a permissioned institutional record.
- Email notifications for important account and verification events.
- Health endpoints, OpenAPI documentation and resilience policies.

## Services

| Service | Port | Responsibility | Main storage/integration |
|---|---:|---|---|
| API Gateway | `8080` | JWT validation, trusted headers, routing, rate limiting and circuit breaking | Routes to public services |
| IAM Service | `8081` | Registration, public keys, challenges, signature verification and session creation | PostgreSQL `iam_db`, Kafka |
| Fabric Orchestrator | `8085` | The only application client allowed to submit/query institutional proofs | Hyperledger Fabric gateway, Kafka |
| Account Service | `8086` | Accounts, balances, transfers, saga state, outbox and reconciliation | PostgreSQL `cyphervault_account_db`, Kafka, AI service |
| File Service | `8087` | KYC file ownership, private storage, metadata and review state | MySQL `cyphervault_file_db`, Kafka |
| Notification Service | `8088` | Reliable customer/admin email notifications and deduplication | PostgreSQL `cyphervault_notification_db`, Kafka, SMTP |

The Fabric Orchestrator is an internal service and must not be exposed directly to untrusted clients.

## Architecture and request flow

```text
Mobile / Admin Client
        |
        v
API Gateway (8080)
        |
        +--> IAM Service (identity and authentication)
        +--> File Service (KYC documents)
        +--> Account Service (accounts and transfers)
        +--> Chat Service (separate repository)

Account Service --> AI Service (risk recommendation)
Account Service <--> Kafka <--> Fabric Orchestrator --> Permissioned record
Domain events --> Notification Service
```

Sensitive multi-stage operations return a trackable initial state. The final business result is applied only after the authoritative execution result is verified. Unique request/event identifiers prevent duplicate financial effects.

## Project structure

```text
5th_Spring_miceoservice/
â”œâ”€â”€ services/
â”‚   â”œâ”€â”€ api-gateway/                 # Secured public entry point
â”‚   â”œâ”€â”€ iam/                         # Identity, keys and authentication
â”‚   â”œâ”€â”€ account-service/             # Accounts, transfers, saga and outbox
â”‚   â”œâ”€â”€ file-service/                # KYC document storage and review
â”‚   â”œâ”€â”€ notification-service/        # Email notifications
â”‚   â””â”€â”€ fabric-orchestrator-service/ # Controlled institutional-record client
â”œâ”€â”€ infra/
â”‚   â”œâ”€â”€ kafka/                       # Local Kafka and Kafka UI compose file
â”‚   â””â”€â”€ fabric/                      # Local mount point for external Fabric material
â”œâ”€â”€ docs/                            # Report, diagrams and test evidence
â””â”€â”€ VERIFY_AND_BUILD_FIXED_SERVICES.ps1
```

## Technologies

- Java 17
- Spring Boot and Spring Cloud Gateway
- Spring Data JPA and Flyway
- PostgreSQL and MySQL
- Apache Kafka
- Resilience4j and Bucket4j
- JWT and Ed25519-based request proof
- Hyperledger Fabric Gateway
- Maven Wrapper
- Spring Boot Actuator and OpenAPI/Swagger

## Prerequisites

Install and configure:

- JDK 17
- Docker Desktop or Docker Engine
- PostgreSQL
- MySQL 8+
- A reachable Hyperledger Fabric network and the authorized client certificates
- The AI service and Chat service when their flows are required

Create the local databases before starting the services:

```sql
CREATE DATABASE iam_db;
CREATE DATABASE cyphervault_account_db;
CREATE DATABASE cyphervault_notification_db;
```

Create the MySQL database:

```sql
CREATE DATABASE cyphervault_file_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
```

## Configuration

The example source properties describe the expected local ports and dependencies. Before a public deployment:

1. Move passwords, JWT secrets, SMTP credentials and private paths to environment variables or an external secret store.
2. Use the same JWT secret in IAM and the API Gateway.
3. Use the same internal gateway secret in the API Gateway and Chat Service.
4. Configure Fabric channel, chaincode, peer endpoint, MSP identity and mounted certificates.
5. Never commit `.env`, private keys, keystores or production credentials.

## Local startup

### 1. Start Kafka

```powershell
cd infra\kafka
docker compose up -d
```

Kafka is exposed on `localhost:9092`; Kafka UI is available at `http://localhost:8089`.

### 2. Start each Spring service in a separate terminal

```powershell
cd services\iam
.\mvnw.cmd spring-boot:run
```

```powershell
cd services\file-service
.\mvnw.cmd spring-boot:run
```

```powershell
cd services\fabric-orchestrator-service
.\mvnw.cmd spring-boot:run
```

```powershell
cd services\account-service
.\mvnw.cmd spring-boot:run
```

```powershell
cd services\notification-service
.\mvnw.cmd spring-boot:run
```

```powershell
cd services\api-gateway
.\mvnw.cmd spring-boot:run
```

Recommended startup order:

```text
Databases -> Kafka -> Fabric network -> AI service -> IAM -> File ->
Fabric Orchestrator -> Account -> Notification -> Chat -> API Gateway
```

## Health and API documentation

| Component | Health | OpenAPI/Swagger |
|---|---|---|
| IAM | `http://localhost:8081/actuator/health` | `http://localhost:8081/swagger-ui.html` |
| Account | `http://localhost:8086/actuator/health` | `http://localhost:8086/swagger-ui.html` |
| File | `http://localhost:8087/actuator/health` | `http://localhost:8087/swagger-ui.html` |
| Notification | `http://localhost:8088/actuator/health` | `http://localhost:8088/swagger-ui.html` |
| Fabric Orchestrator | `http://localhost:8085/actuator/health` | Internal service |
| API Gateway | `http://localhost:8080/actuator/health` | `http://localhost:8080/swagger-ui.html` |

## Important public API groups

- `/api/auth/**` - registration, challenges and signature verification.
- `/api/files/**` - KYC upload, status and user-owned files.
- `/api/admin/files/**` - authorized KYC review.
- `/api/accounts/**` - account opening, balance, history and transfers.
- `/api/chat/**` - routed to the separate Chat Service.

## Tests and verification

Run the provided build verification script from the repository root:

```powershell
.\VERIFY_AND_BUILD_FIXED_SERVICES.ps1
```

Run tests for an individual service:

```powershell
cd services\account-service
.\mvnw.cmd test
```

The submission evidence should include unit/integration results, security tests, duplicate-transfer protection, load testing, stress testing, autoscaling evidence and distributed tracing screenshots under `docs/test-results/`.

## Security notes

- The customer private key stays on the client device.
- The gateway derives trusted identity context from the verified session.
- Services still enforce resource ownership after authentication.
- Operational databases remain private to their owning service.
- The permissioned record stores selected proofs, not identity documents or chat content.
- An uncertain transfer result is reconciled before any retry that could duplicate money movement.
## Monitoring and Observability

The CypherVault backend services are monitored using Prometheus and Grafana.

### Grafana Dashboard

- URL: [Open Grafana](http://172.29.5.41:30300)
- Username: `admin`
- Password: `d02ced2d9c96528173903f69459fb6a5f9dd!Aa9`

Grafana provides dashboards for monitoring:

- Spring Boot application metrics.
- HTTP request rates and response times.
- JVM memory and CPU usage.
- Kubernetes pods and deployments.
- Service availability and error rates.
- Horizontal Pod Autoscaler activity.

