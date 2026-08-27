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

---

### 1. Clone the repository

```bash
git clone https://github.com/Balazs8998/Cortis.git
cd Cortis
```

---

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

Generate a suitable Base64-encoded secret on Linux or macOS:

```bash
openssl rand -base64 32
```

Generate separate values for:

- `JWT_SECRET_KEY`
- `CHIP_HMAC_SECRET`

For example:

```env
JWT_SECRET_KEY=<first-generated-value>
CHIP_HMAC_SECRET=<second-generated-value>
```

Never commit the `.env` file or real secrets to Git.

> **Important:** Creating the `.env` file alone does not automatically make its values available to the Spring Boot application.
>
> Docker Compose automatically reads the `.env` file from the repository root, but the backend process must also receive these values through its environment.
>
> Follow one of the backend startup methods below.

---

### 3. Start PostgreSQL

From the repository root:

```bash
docker compose up -d
```

Check the running container:

```bash
docker compose ps
```

The PostgreSQL container uses the values defined in the repository-root `.env` file.

---

### 4. Start the backend

The backend can be started using IntelliJ IDEA or directly from a terminal.

Choose one of the following methods.

---

#### Option A — IntelliJ IDEA

Open the backend project in IntelliJ IDEA and locate:

```text
com.cortis.CortisApplication
```

Open:

```text
Run
→ Edit Configurations...
→ CortisApplication
```

Enable **EnvFile** and add the `.env` file from the repository root.

The expected project layout is:

```text
Cortis/
├── .env
├── app/
│   ├── backend/
│   └── desktop-client/
├── docker-compose.yml
└── README.md
```

The EnvFile entry should point to:

```text
.env
```

If the **EnvFile** option is not available in IntelliJ IDEA, install or enable the EnvFile integration/plugin first.

For local development, the Spring development profile can optionally be activated with:

```text
SPRING_PROFILES_ACTIVE=dev
```

After the environment file is configured, start:

```text
CortisApplication
```

---

#### Option B — Linux terminal

Open a terminal in the repository root.

Load all variables from `.env` into the current shell:

```bash
set -a
source .env
set +a
```

Then start the backend:

```bash
mvn -f app/backend/pom.xml spring-boot:run
```

The `set -a` command causes variables loaded from `.env` to be exported to child processes, including the Spring Boot application.

The exported variables remain available in the current terminal session.

---

#### Option C — macOS terminal

Open Terminal in the repository root.

The same approach works with the default macOS shell (`zsh`) and with `bash`.

Load the `.env` file:

```bash
set -a
source .env
set +a
```

Then start the backend:

```bash
mvn -f app/backend/pom.xml spring-boot:run
```

If you want to verify that a variable has been loaded before starting Spring Boot, you can run:

```bash
echo "$POSTGRES_DB"
```

It should return:

```text
cortis
```

Do not print secret values such as `JWT_SECRET_KEY` or `CHIP_HMAC_SECRET` in shared terminals, screenshots or logs.

---

### Backend URL

The backend runs by default at:

```text
http://localhost:8080
```

Check its health at:

```text
http://localhost:8080/actuator/health
```

Flyway database migrations are executed automatically during backend startup.

Hibernate validates the mapped database schema during startup.

---

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

If the backend tests require database or environment configuration, make sure the required environment variables are available first.

From the repository root on Linux or macOS:

```bash
set -a
source .env
set +a
```

Then run:

```bash
mvn -f app/backend/pom.xml test
```

Alternatively:

```bash
cd app/backend
mvn test
```

provided that the required environment variables are already available in the current shell.

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

The `.env` file is intentionally excluded from Git and must be created separately for each local development environment.

---

## Environment Loading Summary

### Docker Compose

Docker Compose automatically reads the repository-root `.env` file when started from the repository root:

```bash
docker compose up -d
```

### IntelliJ IDEA

The `.env` file must be added to the `CortisApplication` Run Configuration through EnvFile.

### Linux

```bash
set -a
source .env
set +a
mvn -f app/backend/pom.xml spring-boot:run
```

### macOS

```bash
set -a
source .env
set +a
mvn -f app/backend/pom.xml spring-boot:run
```

If Spring Boot cannot access the required environment variables, startup may fail with an error similar to:

```text
Could not resolve placeholder 'JWT_SECRET_KEY'
```

This normally means that the `.env` file exists but its values have not been loaded into the Spring Boot process environment.

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