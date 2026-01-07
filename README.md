# Risk Factor — Risk-Aware Transaction Monitoring API

A Spring Boot service that evaluates transaction risk in real time, persists assessments in MongoDB, and exposes REST APIs for monitoring, filtering, and audit friendly visibility. Built to demonstrate practical fraud and risk detection patterns, observability, and with clean API design.

## Why this project matters

Financial platforms need fast, explainable risk assessments that can be audited and tuned. This service models a production style workflow: ingest a transaction, score risk using configurable rules, store the assessment, and expose the results via APIs and dashboards (metrics + Swagger).

## Features

1. **Realtime risk scoring with explainable reasons**
    - **How to see it:** Submit a transaction and inspect the response `score`, `level`, and `reason`.
    - **Endpoint:** `POST /transactions`

2. **Configurable risk rules** (amount thresholds, high risk countries, merchant categories, behavioral spikes)
    - **How to see it:** Update rule values in `application.properties` and submit test transactions.
    - **Config:** `risk.rules.*` in `src/main/resources/application.properties`

3. **Transaction listing with filtering + pagination**
    - **How to see it:** Call `GET /transactions` with query params (`customerId`, `riskLevel`, `fromDate`, `toDate`, `page`, `size`).

4. **Assessment history per transaction**
    - **How to see it:** Call `GET /transactions/{id}/assessments` to view prior assessments for a transaction.

5. **Customer risk profiling**
    - **How to see it:** Re-submit multiple transactions for the same customer and observe how average spend & prior risk influence scoring.

6. **Operational observability**
    - **How to see it:**
        - Metrics via Actuator: `GET /actuator/metrics` (and `risk_assessment_total`, `risk_assessment_failed_total`).
        - Correlation ID added to every request/response for traceability: `X-Correlation-ID` header.

7. **API documentation (Swagger UI)**
    - **How to see it:** `GET /swagger-ui.html`

## Roadmap

✓ **Domain modeling & data storage**
    - Defined transaction, and risk assessment documents.
    - Implemented MongoDB repositories for persistence.

✓ **Risk scoring engine**
    - Encapsulated rules in a dedicated service with explainable output.
    - Added risk levels (LOW, MEDIUM, HIGH) and scoring thresholds.

✓ **Transaction workflow & API design**
    - Built REST endpoints to submit transactions, list transactions, and fetch assessments.
    - Added DTOs and mapping for clean API boundaries.

✓ **Customer risk profiling** - Defined customer profiles to track historical spend and risk levels.
     Added rolling average spend and previous risk tracking to improve scoring accuracy.

✓ **Operational readiness**
    - Added correlation IDs to logs and responses for traceability.
    - Added Micrometer metrics and Actuator endpoints.
    - Added Swagger/OpenAPI documentation for quick API exploration.

✓ **Testing**
    - Added unit and controller tests for services, rules, DTOs, and API behavior.
## Future enhancements
- [ ] **Model Enhancements** - Improve error handling, add more transaction attributes (device info, IP address), add seed data + local docker compose for MongoDB.
- [ ] **Authentication** - Add Authentication using JWT or OAuth2 for secure API access.
- [ ] **Product Polishing** - Add dashboard for metrics & sample postman collection
- [ ] **Alerting integration** - Connect with alerting systems to notify risk teams of high-risk transactions.
## Tech stack

- **Java 17**, **Spring Boot 3.x**
- **MongoDB** (Spring Data)
- **Springdoc OpenAPI** (Swagger UI)
- **Micrometer + Spring Actuator** (metrics)
- **JUnit 5** (tests)

## Getting started

### Prerequisites

- Java 17+
- Maven (or use `./mvnw`)
- MongoDB instance (Atlas or local)

### Configure MongoDB

Update `spring.data.mongodb.uri` in `src/main/resources/application.properties` or set it as an environment override:

```bash
export SPRING_DATA_MONGODB_URI='mongodb://localhost:27017'
```

### Run the service

```bash
./mvnw spring-boot:run
```

The service will start at `http://localhost:8080`.

## API examples

### Submit a transaction

```bash
curl -X POST http://localhost:8080/transactions \
  -H 'Content-Type: application/json' \
  -d '{
    "customerId": "cust-001",
    "amount": 8500.00,
    "currency": "USD",
    "country": "US",
    "merchantCategory": "ELECTRONICS",
    "timestamp": "2024-08-21T12:30:00Z"
  }'
```

### List transactions (with filters)

```bash
curl "http://localhost:8080/transactions?customerId=cust-001&riskLevel=HIGH&page=0&size=5"
```

### Get assessments for a transaction

```bash
curl "http://localhost:8080/transactions/{transactionId}/assessments"
```

### Metrics & health

```bash
curl http://localhost:8080/actuator/health
curl http://localhost:8080/actuator/metrics/risk_assessment_total
```

## Real-world impact

- **Fraud prevention:** Flags unusually large transactions or high-risk geographies before funds settle.
- **Operational efficiency:** Gives risk teams prioritized, explainable alerts for faster investigation.
- **Customer protection:** Learns customer based spending patterns to reduce false positives.
- **Compliance readiness:** Stores assessment history and provides traceability via correlation IDs.
- **Scalable architecture:** Designed to integrate with payment pipelines, alerting systems, or case management tools.

## Testing

```bash
./mvnw test
```
