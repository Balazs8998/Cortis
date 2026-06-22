# Schemas

### The database is divided into multiple schemas for the following reasons:
- modularity
- better transparency
- separation of responsibilities
- easier maintainability
- better readability
- partial rebuildability

### Schema Overview
| Schema | Purpose |
|---|---|
| core | System tables and shared definitions |
| specification | Domain specifications, properties and rules |
| company | Company data and inventory information |
| map | Mapping and rule tables |
| archiv | Inactive entities and archived data |
| personal | Users and permissions |
| translation | Translation tables required for multilingual system support |

---

## Core

### The core schema contains the fundamental and commonly used definition tables of the system.

### Responsibilities:
- providing shared definitions
- creating a unified structure
- storing system-level base data

### Examples:
- feature definitions
- enum-like tables
- system-level metadata

---

## Specification

### The specification schema contains the specifications, properties and fundamental rules of domain entities.

### Responsibilities:
- organizing properties and rules by domain
- creating a unified domain structure

### Examples:
- type definitions
- master data
- mounting option definitions
- mounting feature requirement tables
- entity feature value tables

---

## Company

### The company schema contains company inventory and operational data, which are connected to the system through domain-specific master tables.

### Responsibilities:
- storing company inventory data
- storing company operational data
- separating private technological and usage data from the shared system

### Examples:
- inventory tables
- order tables
- statistical tables (later)

---

## Map

### The map schema contains the system's mapping, pairing and rule tables.

### Responsibilities:
- defining system-level rules
- storing domain-specific mapping and helper rules

### Note:
The schema currently serves as a prepared structure for future mapping and rule systems.

---

## Archiv

### The archiv schema is used for storing inactive company domain entities, statistics and related data.

### Responsibilities:
- archiving no longer used company data
- storing historical statistical data
- storing data of closed or inactive orders

---

## Personal

### The personal schema is used for identifying company employees, managing their permissions and responsibilities, and defining software access levels.

### Responsibilities:
- identifying users
- managing permissions and responsibilities
- tracking working hours and breaks (later)
- tracking workflows between departments (later)
- tracking employee status and workload (later)
- recording absences and their reasons (later)

### Examples:
- user data
- role definitions
- permission definitions
- user_role relations
- role_permission relations

---

## Translation

### The translation schema provides multilingual system support and ensures the extensibility of the translation system.

### Responsibilities:
- multilingual handling of software and database output texts

### Examples:
- language definitions
- keyword data
- category definitions
- central text mapping table

---

# Common Meta Fields

Some database tables contain shared audit and lifecycle tracking fields.

| Column | Description |
|---|---|
| created_by | user who created the record |
| created_at | record creation timestamp |
| updated_by | user who last modified the record |
| updated_at | last modification timestamp |
| deleted_at | logical deletion timestamp |