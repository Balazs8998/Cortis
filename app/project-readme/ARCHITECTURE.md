# Cortis Architecture

## 1. Project Overview

Cortis is a personal full-stack inventory and tool management system.

The long-term goal of the system is to manage tools, holders, interfaces, machines, compatibility rules, warehouse data, users, permissions, translations, and audit-related information.

The project currently focuses on building a stable core system before implementing the main business logic.

Current architecture:

* Backend: Spring Boot
* Frontend: JavaFX desktop client
* Database: PostgreSQL
* Authentication: JWT-based stateless authentication
* Authorization: role and permission based access control
* Translation: backend-driven translation system with frontend cache

---

## 2. Main Layers

The backend follows a layered structure.

```text
Controller
↓
Service
↓
Repository
↓
Database
```

### Controller Layer

The controller layer is responsible for handling HTTP requests and returning HTTP responses.

Controllers should not contain business logic.

Main responsibilities:

* receive requests
* validate request structure when needed
* call the appropriate service
* return response DTOs
* rely on global exception handling for error responses

### Service Layer

The service layer contains the main application logic.

Main responsibilities:

* authentication logic
* user-related logic
* permission-related logic
* translation loading
* chip authentication logic
* business rules
* logging important application events
* throwing domain-specific exceptions when something is invalid

### Repository Layer

The repository layer is responsible for database access.

Repositories should not contain business logic.

Main responsibilities:

* load entities from the database
* save entities
* search by unique fields such as username, email, chip code hash, role name, permission key

---

## 3. Authentication Flow

The system currently uses JWT-based authentication.

Basic login flow:

```text
JavaFX Login Form
↓
POST /api/auth/login
↓
AuthController
↓
AuthService
↓
User lookup
↓
Password validation
↓
JWT generation
↓
LoginResponse
↓
Frontend stores token in SessionManager
```

After successful login, the frontend stores the authenticated user's data and token in the session.

The token is sent with protected API requests using the Authorization header.

```text
Authorization: Bearer <token>
```

---

## 4. JWT Request Flow

Protected backend requests are processed through a JWT authentication filter.

```text
Incoming HTTP Request
↓
JwtAuthenticationFilter
↓
Extract Bearer token
↓
Validate token
↓
Load user details
↓
Build Authentication object
↓
Store authentication in SecurityContext
↓
Controller endpoint is called
```

If the token is missing, invalid, or expired, the request should be rejected by the security layer or handled through the configured exception flow.

---

## 5. Authorization Model

The system uses a role and permission based authorization model.

Main concept:

```text
User
↓
UserRole
↓
Role
↓
RolePermission
↓
Permission
```

A user can have one or more roles.

A role can have one or more permissions.

Permissions are converted into Spring Security authorities and are used to protect backend endpoints or business operations.

Example:

```text
USER_MANAGE
TOOL_READ
TOOL_CREATE
TOOL_UPDATE
TOOL_DELETE
```

The goal is to avoid hardcoding authorization only around roles like `ADMIN` or `USER`.

Permissions give the system more flexibility later.

---

## 6. Translation System

The backend provides translations to the frontend.

Basic flow:

```text
Frontend selects language
↓
GET /api/translation/{languageCode}
↓
TranslationController
↓
TranslationService
↓
Load all translations for selected language
↓
Return TranslationResponse
↓
Frontend stores translations in cache
```

The frontend uses a translation cache after login.

If a translation key is missing, the frontend should use a fallback value instead of crashing.

Current response structure:

```text
TranslationResponse
- translations: Map<String, String>
```

---

## 7. Exception Handling

The backend uses a global exception handling system.

The goal is to return consistent error responses instead of random exception messages.

Expected error response structure:

```json
{
  "timestamp": "...",
  "status": 400,
  "errorCode": "USER_NOT_FOUND",
  "messageKey": "error.user.not-found",
  "path": "/api/..."
}
```

Main goals:

* predictable API errors
* frontend-friendly translation keys
* no raw internal exception messages exposed to the client
* easier debugging through structured logs

---

## 8. Logging

The system uses structured logging for important backend events.

Logging should be used for:

* authentication attempts
* failed login attempts
* permission problems
* important service actions
* unexpected exceptions
* chip authentication events
* security-relevant decisions

Logging should not be used for every small line of code.

The goal is to make real problems traceable without turning the log into noise.

---

## 9. Chip Authentication

The system contains or plans support for chip-based login.

The chip code should not be stored as plain text.

The planned approach is to treat the chip code similarly to a password-like secret.

Basic concept:

```text
Chip code
↓
HMAC / hash process
↓
Stored chip code hash
↓
Lookup user by stored value
↓
Authenticate user
```

The chip authentication system should be separated from normal password login logic, but both should eventually produce an authenticated user session/token.

Security notes:

* raw chip codes should not be logged
* raw chip codes should not be stored
* chip secret configuration must be validated
* chip authentication failures should be logged carefully

---

## 10. Frontend Architecture

The frontend is currently a JavaFX desktop client.

Main responsibilities:

* display login screen
* send login request to backend
* store current session data
* store JWT token
* load translations
* display translated UI texts
* call protected backend endpoints
* later: show main menu, widgets, warehouse data, tool data, machine data

Important frontend components:

```text
LoginController
AuthApiClient
SessionManager
TranslationCache
MainView / MainMenu
```

The frontend should not contain backend business rules.

It should call backend APIs and display the result.

---

## 11. Database Structure

The database is PostgreSQL-based and separated into multiple schemas.

Known or planned schemas:

```text
core
specification
company
map
archive
personal
translation
```

### personal schema

Contains users, roles, permissions, and authentication-related data.

Important tables:

```text
personal.users
personal.roles
personal.user_roles
personal.permissions
personal.role_permissions
```

### translation schema

Contains translation-related data.

Important tables:

```text
translation.language
translation.keyword
translation.category
translation.text
```

The goal is to keep authentication, translation, and business data separated clearly.

---

## 12. Current Core Modules

Current or planned core modules:

```text
auth
security
user
role
permission
translation
exception
logging
chip
configuration
```

These modules form the foundation of the application.

The business logic should be built on top of this core.

---

## 13. Version Scope

### V0 - First usable core

V0 should focus on creating a stable foundation and moving toward business logic.

Planned V0 scope:

* JWT login
* protected endpoints
* role and permission loading
* translation loading
* global exception handling
* basic logging
* code cleanup
* first business CRUD modules

### V1 - Stronger security core

Planned V1 scope:

* temporary password generation
* force password change after first login
* password expiration policy
* token expiration handling
* audit log
* chip authentication hardening

### V2 - More advanced security and administration

Possible V2 scope:

* password reset flow
* login attempt lock
* rate limiting
* refresh token handling
* device/session overview
* permission management UI
* admin security dashboard

---

## 14. Development Rules

General project rules:

* Controllers should stay thin.
* Services should contain business logic.
* Repositories should only access data.
* DTOs should be used for API communication.
* Entities should not be exposed directly to the frontend.
* Exceptions should go through the global exception handler.
* Logs should contain useful technical context but no secrets.
* Security-sensitive values must never be logged.
* New features should be assigned to V0, V1, or V2 before implementation.

---

## 15. Current Priority

The current priority is not to endlessly expand the core system.

The current priority is:

```text
1. Clean up the existing core
2. Stabilize authentication and translation flow
3. Keep security improvements documented for V1
4. Start implementing the first real business logic
```

The core system should be strong enough to support the first business modules, but it does not need every advanced security feature before the first usable version.
