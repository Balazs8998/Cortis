# CORTIS

CORTIS (CNC Operations Real-Time Intelligent System) is a data-driven backend system designed for CNC manufacturing environments.

The system aims to provide a unified and structured data model to manage:
- tools
- holders
- interfaces
- machines

and define compatibility relationships between them.

---

## 🎯 Objective

The primary goal of CORTIS is:

> To determine compatibility between tools, holders, interfaces, and machines based on structured data and logical rules — not hardcoded conditions.

---

## 🧠 Core Concept

CORTIS does not rely on static or hardcoded logic.

Compatibility is determined using:
- **feature definitions** (e.g., diameter, connection type, taper, etc.)
- **requirements** assigned to component types
- type-level abstraction (`tool_type`, `holder_type`, etc.)

### In simple terms:

> If two components satisfy the same mounting feature requirements → they are considered compatible.

---

## 🏗️ Architecture

The system is built around four main layers:

### 1. Feature Definitions
- `tool_feature_definition`
- `holder_feature_definition`
- `interface_feature_definition`
- `machine_feature_definition`

These define the available properties and characteristics of each domain.

---

### 2. Requirements (Rules)
- `*_type_mounting_feature_requirement`

These tables define which features are required for compatibility at the type level.

---

### 3. Types and Master Data
- `tool_type`, `holder_type`, `interface_type`, `machine_type`
- `tool_master`, `holder_master`, etc.

This layer models real-world components and their classifications.

---

### 4. Inventory (Real Data)
- `tool_inventory`
- `holder_inventory`
- `interface_inventory`
- `machine_inventory`

This layer stores actual company data such as stock, location, and quantities.

---

## ⚙️ Machine Tool Stations

Machine-specific tool mounting positions are handled separately.

The `machine_tool_station_category` table defines:
- which stations exist on a machine
- what type of mounting is allowed (holder-based, interface-based, etc.)

This allows modeling different machine behaviors such as:
- main spindle
- sub-spindle
- drilling units

---

## 📂 Project Structure

```text
db/
  query/
    00_baseline/
    01_core/
    02_specification/
    ...

docs/
  database/
    ER/
      cortis.png


🚧 Current Status
Database structure established
Feature-based model defined
ER diagram completed
Core compatibility logic designed


🔜 Next Steps
Implement initial compatibility queries
Validate Tool → Holder → Interface → Machine chain
Populate test data
Build backend logic / API layer

⚠️ Notes
This project is currently in the design and early implementation phase.

The goal is not a simple CRUD application, but a:

scalable, logic-driven decision support system


