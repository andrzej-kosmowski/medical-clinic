# Medical Clinic API

REST API for managing a medical clinic: patients, doctors, facilities, users and visit booking.
Built with Java 21 and Spring Boot as a layered application with PostgreSQL, Liquibase migrations and unit tests for every controller and service.

## Features

- CRUD for **patients**, **doctors**, **facilities** and **users**, with pagination on list endpoints
- Assigning doctors to facilities and removing them again
- **Visit scheduling**: doctors publish visit slots, patients book or cancel them
- Visit search by specialization, date or time range, optionally only available slots (JPA Specifications)
- Business rules for visits: no overlapping visits for a doctor or a patient, no visits in the past, no double booking
- Consistent error responses from a global exception handler (one exception hierarchy per domain)
- OpenAPI (Swagger) documentation, structured JSON logging for ELK

## Tech stack

| Area | Technology |
| --- | --- |
| Language / framework | Java 21, Spring Boot 3, Spring Web |
| Persistence | Spring Data JPA, PostgreSQL, Liquibase |
| Mapping / boilerplate | MapStruct, Lombok |
| Docs | springdoc-openapi (Swagger UI) |
| Logging | Logback with Logstash encoder (ELK) |
| Tests | JUnit 5, Mockito, MockMvc, H2 |
| Infrastructure | Docker, Docker Compose |

## Architecture

```
controller  ->  service  ->  repository  ->  PostgreSQL
    |             |
   dto        mapper (MapStruct)
```

- `controller`: HTTP layer, request and response DTOs
- `service`: business rules and validation
- `repository` and `specification`: Spring Data repositories and dynamic visit queries
- `exception`: domain exceptions and `MedicalClinicExceptionHandler`
- `db/changelog`: Liquibase changelogs (patients, users, doctors, facilities, doctor_facilities, visits)

## Getting started

Requirements: JDK 21, Docker.

```bash
# 1. Build the jar (the Dockerfile copies it from target/)
./mvnw clean package -DskipTests

# 2. Create the external networks used by docker-compose (once)
docker network create elk-network
docker network create medical-network

# 3. Start the application, PostgreSQL and pgAdmin
docker compose up --build
```

| Service | URL |
| --- | --- |
| API | http://localhost:8082 |
| Swagger UI | http://localhost:8082/swagger-ui.html |
| pgAdmin | http://localhost:8889 (demo@example.com / secret) |

### Running locally without Docker for the app

```bash
docker compose up -d db
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5433/medicalclinic ./mvnw spring-boot:run
```

The API is then available on http://localhost:8080.

## Main endpoints

| Resource | Endpoints |
| --- | --- |
| `/patients` | `GET`, `GET /{id}`, `POST`, `PUT /{id}`, `DELETE /{id}`, `PATCH /{id}/password` |
| `/doctors` | `GET`, `GET /{id}`, `GET /specialization/{specialization}`, `GET /{id}/facilities`, `POST`, `PUT /{id}`, `DELETE /{id}`, `PATCH /{id}/facility`, `DELETE /{doctorId}/facility/{facilityId}` |
| `/facilities` | `GET`, `GET /{id}`, `GET /{id}/doctors`, `POST`, `PUT /{id}`, `DELETE /{id}` |
| `/users` | `GET`, `GET /{id}`, `POST`, `PUT /{id}`, `DELETE /{id}`, `PATCH /{id}/password` |
| `/visits` | `GET`, `GET /{id}`, `GET /patient/{patientId}`, `GET /doctor/{doctorId}`, `GET /doctor/{doctorId}/available`, `GET /search`, `POST`, `PATCH /{visitId}/patient/{patientId}`, `PATCH /{visitId}/cancel`, `DELETE /{visitId}` |

List endpoints accept standard Spring paging parameters (`page`, `size`, `sort`).

### Example: create a visit slot and book it

```bash
curl -X POST http://localhost:8082/visits \
  -H "Content-Type: application/json" \
  -d '{"doctorId": 1, "startTime": "2026-11-02T09:00:00", "endTime": "2026-11-02T09:30:00"}'

curl -X PATCH http://localhost:8082/visits/1/patient/1
```

### Example: search available visits

```bash
curl "http://localhost:8082/visits/search?specialization=Cardiologist&date=2026-11-02&availableOnly=true"
```

## Tests

```bash
./mvnw test
```

Service tests use Mockito, controller tests use MockMvc.

## Related projects

- [medical-clinic-proxy](https://github.com/andrzej-kosmowski/medical-clinic-proxy): microservice that exposes selected operations of this API and protects callers from its outages with a circuit breaker
