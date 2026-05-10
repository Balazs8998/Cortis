# CORTIS

CORTIS (CNC Operations Real-Time Intelligent System) is a modular, data-driven backend system designed for CNC manufacturing environments.

The project aims to provide a scalable and structured architecture for:

* inventory management
* tooling compatibility validation
* machine and resource management
* production-related data handling
* future monitoring and analytical systems

CORTIS is designed around database-driven logic and domain separation instead of hardcoded workflows.

---

# 🎯 Objective

The primary goal of CORTIS is to create a unified manufacturing support system capable of:

* managing tools, holders, interfaces, machines and materials
* validating compatibility relationships
* organizing company-specific technological data
* supporting future monitoring and decision systems
* providing a scalable architecture for long-term development

---

# 🧠 Core Concept

CORTIS is built around structured domain logic.

The system separates:

* shared system definitions
* domain specifications
* company-specific operational data
* mapping and rule systems
* user and permission handling
* multilingual support

Compatibility and logic are not determined by hardcoded conditions.

Instead, the system uses:

* feature definitions
* type abstractions
* mapping tables
* compatibility rules
* domain relationships

This allows the architecture to remain flexible, extensible and reusable.

---

# 🏗️ Database Architecture

The database is separated into multiple schemas to improve:

* modularity
* readability
* maintainability
* scalability
* separation of responsibilities

## Schema Overview

| Schema        | Purpose                                     |
| ------------- | ------------------------------------------- |
| core          | System tables and shared definitions        |
| specification | Domain specifications, properties and rules |
| company       | Company data and inventory information      |
| map           | Mapping and rule tables                     |
| archiv        | Inactive entities and archived data         |
| personal      | Users and permissions                       |
| translation   | Translation system tables                   |

---

# ⚙️ Main System Areas

## Core

Contains shared system-level structures.

Examples:

* feature definitions
* enum-like structures
* metadata tables
* shared base definitions

---

## Specification

Contains domain specifications and rule definitions.

Examples:

* type definitions
* master data
* mounting option definitions
* feature requirement structures
* entity feature values

---

## Company

Contains company-specific operational and inventory data.

Examples:

* inventory tables
* order tables
* company-specific machine and tooling data
* future statistical data

---

## Map

Contains mapping, compatibility and rule-related structures.

Examples:

* mapping tables
* helper rule structures
* compatibility support tables

---

## Archiv

Contains inactive or historical company data.

Examples:

* inactive entities
* archived orders
* historical statistics

---

## Personal

Contains user and permission related structures.

Examples:

* users
* roles
* permissions
* user-role relations

---

## Translation

Provides multilingual support for the system.

Examples:

* language definitions
* translation keywords
* categories
* translation text relations

---

# 📂 Project Structure

```text
cortis/
├── app/
│   ├── core/
│   ├── domain/
│   ├── services/
│   ├── repositories/
│   └── api/
│
├── data/
├── scripts/
├── docs/
├── test/
└── alembic/
```

---

# 🚧 Current Status

Current development focuses on:

* database architecture
* schema separation
* domain modeling
* compatibility structure design
* documentation
* backend restructuring

The project is currently in active architecture and backend development.

---

# 🔜 Planned Features

Planned future systems include:

* compatibility validation engine
* inventory workflow handling
* production monitoring
* runtime statistics
* tooling usage tracking
* machine performance analysis
* reporting systems
* API and backend services

---

# ⚠️ Notes

CORTIS is not intended to be a simple CRUD application.

The long-term goal is to build a scalable manufacturing support and decision system capable of handling complex industrial workflows and compatibility logic.
