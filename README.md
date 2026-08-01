# CypherVault Core Banking Microservices

CypherVault is a distributed digital-banking proof of concept built using Java, Spring Boot, Kafka, PostgreSQL, Kubernetes, and Hyperledger Fabric.

This repository contains the core backend microservices responsible for authentication, account management, financial transfers, document verification, notifications, blockchain coordination, and secured API access.

## Main Components

| Component | Responsibility | Port |
|---|---|---:|
| API Gateway | Secured API entry point, routing, JWT validation, rate limiting, and circuit breaking | 8080 |
| IAM Service | User registration, authentication challenges, public keys, JWT issuance, and identity events | 8081 |
| Fabric Orchestrator Service | Controlled communication with Hyperledger Fabric and institutional records | 8085 |
| Account Service | Bank accounts, balances, financial transfers, transaction history, saga coordination, and outbox events | 8086 |
| File Service | KYC document storage, metadata management, and administrative review | 8087 |
| Notification Service | Email notifications and asynchronous notification processing | 8088 |

## Technology Stack

- Java
- Spring Boot
- Spring Cloud Gateway
- Spring Security
- PostgreSQL
- MySQL
- Apache Kafka
- Hyperledger Fabric
- Docker
- Kubernetes
- Prometheus
- Grafana
- Loki
- Tempo
- Maven
- PowerShell

## Architecture

The backend follows a distributed microservice architecture.

Each service owns its business logic and data storage. Services communicate through secured HTTP APIs and asynchronous Kafka events.

Main architectural principles:

- Independent service boundaries.
- Database ownership per service.
- JWT-based authentication and authorization.
- Asynchronous event-driven communication.
- Transactional outbox processing.
- Idempotent financial operations.
- Controlled access to Hyperledger Fabric.
- Kubernetes-based deployment and scaling.
- Centralized monitoring, logs, metrics, and traces.

## Project Structure

~~~text
5th_Spring_miceoservice/
|-- services/
|   |-- api-gateway/
|   |-- iam/
|   |-- account-service/
|   |-- file-service/
|   |-- notification-service/
|   `-- fabric-orchestrator-service/
|-- infrastructure/
|   |-- kafka/
|   `-- fabric/
|-- VERIFY_AND_BUILD_FIXED_SERVICES.ps1
`-- README.md
~~~

## Authentication Flow

The IAM authentication process follows these steps:

1. The user registers an account and public key.
2. IAM generates an authentication challenge.
3. The client signs the challenge using its private key.
4. IAM verifies the signature using the registered public key.
5. IAM issues a JWT access token.
6. The API Gateway validates the token before routing secured requests.

Private user keys are not stored by the backend services.

## Financial Transfer Flow

The Account Service coordinates financial transfers and protects the operation against duplicate processing.

The transfer workflow includes:

- Request validation.
- Authentication and authorization checks.
- Idempotency protection.
- Balance verification.
- Account balance updates.
- Transaction history creation.
- Domain event publication.
- Hyperledger Fabric coordination.
- Completion or failure notification.

## Event-Driven Communication

Apache Kafka is used for asynchronous communication between services.

Examples of domain events include:

- User public key registered.
- Bank account opened.
- Bank account opening failed.
- Money transfer completed.
- Money transfer failed.
- Notification requested.
- Institutional record requested.

Consumers use processed-event tracking and idempotency controls to prevent duplicate event handling.

## Hyperledger Fabric Integration

The Fabric Orchestrator Service is the only backend service allowed to communicate directly with Hyperledger Fabric.

It is responsible for:

- Submitting institutional records.
- Querying blockchain proofs.
- Coordinating transaction settlement records.
- Isolating Fabric SDK access from business services.
- Enforcing controlled blockchain access.

## Building the Services

Open Windows PowerShell inside the repository root and run:

~~~powershell
Set-ExecutionPolicy -Scope Process Bypass
.\VERIFY_AND_BUILD_FIXED_SERVICES.ps1
~~~

The script verifies and builds the supported Spring Boot services.

## Monitoring and Observability

The CypherVault backend services are monitored using Prometheus and Grafana.

### Grafana Dashboard

- URL: [Open Grafana](http://172.29.5.41:30300)
- Username: `admin`
- Password: `d02ced2d9c96528173903f69459fb6a5f9dd!Aa9`

Grafana dashboards provide visibility into:

- Spring Boot application metrics.
- HTTP request rates.
- Response times.
- Error rates.
- JVM memory and CPU usage.
- Kubernetes pods and deployments.
- Horizontal Pod Autoscaler activity.
- Service availability.
- Kafka and infrastructure metrics.

The Grafana URL is available only from the configured local network or through an authorized network connection.

## Security Controls

The platform includes the following security controls:

- JWT authentication.
- Ed25519 digital signatures.
- Public-key challenge verification.
- API Gateway request filtering.
- Rate limiting.
- Circuit breakers.
- Trusted-header validation.
- Service isolation.
- Database isolation.
- Idempotency protection.
- Duplicate-transfer prevention.
- Audit event publication.
- Controlled Hyperledger Fabric access.

## Related Applications

The CypherVault platform also includes:

- Flutter mobile client.
- React administrative dashboard.
- NestJS support chat service.
- FastAPI artificial-intelligence service.
- Hyperledger Fabric network and chaincode.