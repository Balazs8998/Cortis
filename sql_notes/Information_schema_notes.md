# PostgreSQL INFORMATION_SCHEMA Notes

---

# 1. information_schema.table_constraints

## Purpose

Stores all table constraints in the database.

Used for:
- finding existing constraints
- filtering constraint types
- checking PRIMARY KEY, FOREIGN KEY, UNIQUE, CHECK constraints

---

## Important columns

| Column | Meaning |
|---|---|
| constraint_name | Name of the constraint |
| table_name | Table where the constraint exists |
| constraint_type | Type of constraint |

---

## Common constraint types

| Type | Meaning |
|---|---|
| PRIMARY KEY | Main unique identifier |
| FOREIGN KEY | Reference to another table |
| UNIQUE | Values must be unique |
| CHECK | Validation rule |

---

## Simple example

```sql
SELECT constraint_name,
       table_name,
       constraint_type
FROM information_schema.table_constraints;
```

# 2. information_schema.key_column_usage

## Purpose

Shows which column participates in a constraint.

Most commonly used for:
- finding the FOREIGN KEY column in the current table
- identifying PRIMARY KEY columns
- checking which columns belong to constraints

---

## Important columns

| Column | Meaning |
|---|---|
| constraint_name | Related constraint name |
| table_name | Current table |
| column_name | Column used in the constraint |

---

## Example meaning
```sql

SELECT table_schema,
table_name,
column_name,
constraint_name
FROM information_schema.key_column_usage;
```

# 3. `information_schema.constraint_column_usage`

## Purpose

Shows which target column a constraint references.

Mostly used for:

- identifying where a `FOREIGN KEY` points

---

## Important columns

| Column | Meaning |
|---|---|
| `table_name` | Referenced table |
| `column_name` | Referenced column |

---

## Example meaning
```sql
SELECT table_name,
       column_name,
       constraint_name
FROM information_schema.constraint_column_usage;
```


# JOIN Warning Notes

## Important

When writing JOINs, always think carefully about:

- what uniquely connects the tables
- whether the joined columns are actually related
- whether the join can create duplicate rows

---

## Common mistake

Wrong thinking:

```sql 
JOIN table_b b ON a.column_name = b.column_name
```

Just because two columns have the same name,
does NOT mean they belong together.

This can create:

duplicate rows
false matches
exploded result sets
Better thinking

Ask yourself:

What is the REAL relationship between these tables?

Usually joins should use:

PRIMARY KEY ↔ FOREIGN KEY
constraint identifiers
unique identifiers
composite keys when necessary
INFORMATION_SCHEMA special case

Constraint-related tables are usually joined by:

constraint_name
+ constraint_schema

NOT by:

column_name

because many tables can contain the same column names.
