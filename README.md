# CORTIS

**CORTIS — CNC Operations Real-Time Intelligent System**

CORTIS is a manufacturing support system designed for CNC production environments.

The project is based on real manufacturing experience and aims to provide a structured platform for managing tooling data, machines, inventory, users, permissions and compatibility relationships.

> **Project status:** Active development / portfolio project  
> The authentication, authorization, translation and database foundations are implemented. Manufacturing and inventory modules are under development.

---

## Why CORTIS?

CNC manufacturing environments often store tooling, machine and compatibility information across spreadsheets, paper documents and isolated software systems.

CORTIS aims to provide one centralized system for:

- tools, holders and machine interfaces;
- machine and resource management;
- inventory and tooling availability;
- compatibility validation;
- company-specific manufacturing data;
- users, roles and permissions;
- multilingual user interfaces;
- future monitoring and reporting.

The project is not intended to be only a basic CRUD application. Its long-term goal is to model real relationships between manufacturing resources and support rule-based compatibility checks.

---

## Current Features

### Authentication and security

- username and password authentication;
- JWT-based stateless authentication;
- chip-card authentication;
- HMAC-SHA-256 protection for stored chip identifiers;
- database-backed user loading;
- role- and permission-based authorization;
- disabled-user handling;
- protected REST endpoints;
- structured authentication error responses.

### Backend

- REST API built with Spring Boot;
- Spring Security filter chain;
- Spring Data JPA and Hibernate;
- request DTO validation;
- centralized exception handling;
- environment-based configuration;
- development and production profiles;
- application health endpoint;
- structured application logging;
- request correlation using trace and span IDs.

### Database

- PostgreSQL database;
- Flyway database migrations;
- schema validation on startup;
- destructive Flyway clean operations disabled;
- domain separation using multiple PostgreSQL schemas;
- version-controlled database structure.

### Translation system

- database-driven translations;
- language, category, keyword and translation-text entities;
- translation API grouped by category;
- support for multiple user-interface languages.

### Desktop client

- JavaFX user interface;
- password login mode;
- chip-card login mode;
- communication with the Spring REST API;
- JWT session handling;
- API, network and unexpected error handling;
- multilingual user-interface support.

---

## Technology Stack

### Backend

- Java 21
- Spring Boot 3.5
- Spring Web
- Spring Security
- Spring Data JPA
- Hibernate
- Jakarta Bean Validation
- JJWT
- Maven
- Lombok

### Database

- PostgreSQL 16
- Flyway
- Docker Compose

### Desktop client

- Java 21
- JavaFX
- FXML
- Jackson
- Maven

### Testing and observability

- JUnit 5
- Mockito
- Spring Boot Test
- Spring Security Test
- Spring Boot Actuator
- Micrometer Tracing
- Brave

---

## Architecture

CORTIS currently consists of three main parts:

```text
┌─────────────────────────┐
│   JavaFX Desktop Client │
│                         │
│ Password / Chip Login   │
└────────────┬────────────┘
             │ HTTP / JSON
             │ JWT
┌────────────▼────────────┐
│  Spring Boot REST API   │
│                         │
│ Security                │
│ Authentication          │
│ Translation             │
│ Business Logic          │
└────────────┬────────────┘
             │ JPA / Hibernate
┌────────────▼────────────┐
│      PostgreSQL 16      │
│                         │
│ Flyway migrations       │
│ Multiple schemas        │
└─────────────────────────┘
```

The backend is responsible for authentication, authorization, validation, domain logic and database access.

The desktop client does not access the database directly. It communicates with the backend through the REST API.

---

## Project Structure

```text
Cortis/
├── app/
│   ├── backend/
│   │   ├── src/main/java/com/cortis/
│   │   ├── src/main/resources/
│   │   │   ├── db/migration/
│   │   │   ├── application.yml
│   │   │   ├── application-dev.yml
│   │   │   └── application-prod.yml
│   │   └── pom.xml
│   │
│   └── desktop-client/
│       ├── src/main/java/
│       ├── src/main/resources/
│       └── pom.xml
│
├── docker-compose.yml
├── .gitignore
└── README.md
```

---

## Database Schema Design

The database is divided into multiple PostgreSQL schemas.

| Schema | Responsibility |
|---|---|
| `core` | Shared system definitions and base structures |
| `specification` | Domain specifications, properties and type definitions |
| `company` | Company-specific machines, inventory and operational data |
| `map` | Mapping tables, relationships and compatibility rules |
| `archive` | Inactive and historical records |
| `personal` | Users, roles, permissions and authentication data |
| `translation` | Languages, keywords, categories and translated text |

This separation keeps unrelated responsibilities isolated and provides a foundation for future manufacturing modules.

---

## Security Design

### Password authentication

Users can authenticate with a username and password.

Passwords are stored as cryptographic hashes and are verified through Spring Security.

After successful authentication, the backend returns a signed JWT that is used for subsequent protected API requests.

### Chip-card authentication

The desktop client also supports authentication using a chip-card identifier.

Raw chip identifiers are not intended to be stored directly. Before database lookup, the identifier is:

1. normalized;
2. processed with HMAC-SHA-256;
3. compared with the stored HMAC value.

This prevents the original chip identifier from being recovered directly from the database.

### Authorization

Users can be connected to roles, and roles can contain permissions.

The authorization model is loaded from the database and integrated with Spring Security authorities.

---

## API Overview

### Authentication

```http
POST /api/auth/login
```

Example request:

```json
{
  "userName": "example-user",
  "password": "example-password"
}
```

```http
POST /api/auth/loginWithChip
```

Example request:

```json
{
  "chipCode": "EXAMPLE-CHIP-CODE"
}
```

Successful authentication returns a login response containing a JWT.

### Translations

```http
GET /api/translation/{languageCode}
```

Example:

```http
GET /api/translation/en
```

The endpoint returns translations grouped by their categories.

### Application health

```http
GET /actuator/health
```

---

## Running the Project Locally

### Requirements

Install the following tools:

- Java 21;
- Maven 3.9 or newer;
- Docker;
- Docker Compose;
- Git.

### 1. Clone the repository

```bash
git clone https://github.com/Balazs8998/Cortis.git
cd Cortis
```

### 2. Create the environment file

Create a `.env` file in the repository root:

```env
POSTGRES_DB=cortis
POSTGRES_USER=cortis_user
POSTGRES_PASSWORD=replace_with_a_secure_password
POSTGRES_PORT=5432

JWT_SECRET_KEY=replace_with_a_base64_encoded_secret
JWT_EXPIRATION_SECONDS=28800

CHIP_HMAC_SECRET=replace_with_a_base64_encoded_32_byte_secret
```

Generate a suitable Base64-encoded secret on Linux:

```bash
openssl rand -base64 32
```

Generate separate values for the JWT secret and the chip HMAC secret.

Never commit the `.env` file or real secrets to Git.

### 3. Start PostgreSQL

From the repository root:

```bash
docker compose up -d
```

Check the running container:

```bash
docker compose ps
```

### 4. Start the backend

```bash
cd app/backend
mvn spring-boot:run
```

The backend runs by default at:

```text
http://localhost:8080
```

Check its health:

```text
http://localhost:8080/actuator/health
```

Flyway automatically creates and updates the configured database schemas during startup.

### 5. Start the desktop client

Open another terminal:

```bash
cd app/desktop-client
mvn javafx:run
```

The backend must be running before using the desktop client.

---

## Running Tests

### Backend tests

```bash
cd app/backend
mvn test
```

### Desktop-client tests

```bash
cd app/desktop-client
mvn test
```

---

## Configuration

CORTIS uses environment variables for secrets and environment-specific settings.

| Variable | Purpose |
|---|---|
| `POSTGRES_DB` | PostgreSQL database name |
| `POSTGRES_USER` | PostgreSQL username |
| `POSTGRES_PASSWORD` | PostgreSQL password |
| `POSTGRES_PORT` | Local PostgreSQL port |
| `JWT_SECRET_KEY` | Base64-encoded JWT signing secret |
| `JWT_EXPIRATION_SECONDS` | JWT lifetime in seconds |
| `CHIP_HMAC_SECRET` | Base64-encoded 32-byte HMAC secret |

The default JWT expiration is eight hours.

Production secrets must never be stored directly in source-controlled configuration files.

---

## Development Status

### Implemented

- [x] PostgreSQL and Docker configuration
- [x] Flyway database migrations
- [x] multi-schema database structure
- [x] username and password authentication
- [x] JWT generation and validation
- [x] user, role and permission model
- [x] chip-card authentication
- [x] HMAC-protected chip-code lookup
- [x] database-driven translation system
- [x] centralized backend exception handling
- [x] JavaFX desktop login client
- [x] application health endpoint
- [x] structured logging and request tracing

### In progress / planned

- [ ] Angular web client
- [ ] inventory management
- [ ] tool and holder management
- [ ] machine and interface management
- [ ] compatibility validation engine
- [ ] company-specific data management
- [ ] audit logging
- [ ] tooling usage tracking
- [ ] production monitoring
- [ ] reporting and analytics
- [ ] expanded automated test coverage
- [ ] continuous-integration pipeline

---

## Project Motivation

CORTIS is a personal portfolio project created from practical CNC manufacturing experience.

Its purpose is to combine manufacturing-domain knowledge with modern software-development practices, including:

- layered backend architecture;
- secure authentication;
- relational data modeling;
- database migrations;
- REST API design;
- desktop-client integration;
- maintainable and extensible domain separation.

The project is being developed alongside professional full-stack Java training.

---

## Disclaimer

CORTIS is currently an educational and portfolio project under active development.

It is not production-ready and should not currently be used to manage safety-critical or production-critical manufacturing processes.

---

## License

No open-source license has been assigned to this repository yet.

The source code is publicly visible for portfolio, evaluation and demonstration purposes. Public availability does not automatically grant permission to copy, modify or redistribute the project.
