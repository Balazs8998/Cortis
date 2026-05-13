# Tool Mounting Feature Requirement

# README generated automatically. Verification still required.

## Overview

The `tool_mounting_feature_requirement` table defines which features are required
to identify and validate mounting compatibility for a given tool type and
mounting system.

This layer is part of the CORTIS compatibility engine and acts as a rule-based
mapping between:

- tool type
- mounting option
- required feature set

The goal is to avoid hardcoded compatibility logic and instead describe
compatibility through configurable metadata and feature requirements.

---

# Compatibility Philosophy

Not all tooling systems can be identified using the same logic.

Some tooling systems are mostly standardized and can be validated through
geometry and ISO form definitions.

Other systems, especially grooving and parting systems, are manufacturer-specific
and require product-family based identification.

Because of this, CORTIS supports multiple compatibility strategies.

---

# Mounting Requirement Strategies

## geometry

Compatibility is determined mainly through dimensional and geometric values.

Example:
- diameter
- length
- taper size

Typical usage:
- drills
- cylindrical tools
- simple holders

---

## form

Compatibility is determined through standardized form definitions.

Example:
- insert shape
- tolerance class
- relief angle

Typical usage:
- ISO insert systems

---

## form_and_geometry

Compatibility requires both:
- standardized form
- dimensional geometry

Example:
- insert family + insert size

---

## manufacturer_and_code

Used for proprietary or semi-proprietary systems where geometry alone is not
sufficient for reliable compatibility detection.

Compatibility is identified through:
- manufacturer
- mounting family/code
- critical reference dimensions

Typical usage:
- grooving systems
- parting systems
- proprietary insert platforms

Examples:
- Iscar Tang-Grip
- Sandvik CoroCut
- Applitec systems

In real machining environments these systems are usually identified by:
- manufacturer
- insert width
- mounting system family

rather than by pure ISO geometry.

---

# Current Feature Requirement Definitions

## Insert — ISO Standard

Mounting option:
`iso_standard`

Required features:
- insert_shape
- relief_angle
- tolerance_class
- mounting_type
- shape_size

Purpose:
Defines compatibility for standard ISO insert systems such as:
- DCMT
- CNMG
- VNMG

The combination of these features identifies the insert family and compatible
holder seat geometry.

---

## Insert — Grooving System

Mounting option:
`grooving_system`

Required features:
- shape_width
- manufacturer
- mounting_code

Purpose:
Used for grooving and parting systems where ISO geometry alone is insufficient.

These systems are commonly manufacturer-specific and require family/platform
identification.

---

## Drill — Clamping

Mounting option:
`clamping`

Required features:
- outer_diameter
- length

Purpose:
Defines compatibility for clamped drill systems based mainly on geometric
dimensions.

---

## Drill — Screw

Mounting option:
`screw`

Required features:
- outer_diameter
- length

Purpose:
Defines compatibility for screw-mounted drill systems.

---

## Boring Bar — Nut

Mounting option:
`nut`

Required features:
- manufacturer
- mounting_code
- size
- outer_diameter

Purpose:
Defines compatibility for boring bar systems using nut-based mounting methods.

Manufacturer-specific mounting codes are required because dimensional geometry
alone is not always sufficient.

---

# Design Notes

This system intentionally separates:
- geometry
- mounting logic
- manufacturer ecosystems
- compatibility rules

The compatibility engine is designed to remain extensible and configurable.

Additional feature requirements can be added later without redesigning the
database structure.

Potential future additions:
- clamp_style
- insert_height
- chipbreaker_family
- minimum_bore
- holder_series
- proprietary interface families

---

# Architectural Goal

The purpose of this layer is to:
- reduce hardcoded compatibility logic
- support multiple tooling ecosystems
- allow scalable compatibility expansion
- make the system AI-compatible in later stages
- keep compatibility rules data-driven