# Smart Supply — Warehouse & Distribution Management System

**Smart Supply** is the warehouse and distribution backend of the Smart Ecosystem, used by **150+ companies**
to manage inventory, logistics and sales-agent operations.

> I was the **top contributor** to this codebase: about 4,250 of 12,187 commits, 2022–2025. Part of
> [smart-ecosystem](../README.md).

## Features
- **Warehouse:** stock-in, stock transfers (with acceptance-transfer acts), stock adjustments, inventory
  aggregation, product serials, containers and packages, price lists and discounts.
- **Sales & distribution:** orders and offline orders, sales-agent **mobile API**, route planning
  (`planroute`) and trips, KPI tracking, supervisors, customer branches and groups.
- **Payments & fiscalization:** payment-terminal integration (accept payments and generate receipts),
  billing, shifts, transactions, OFD fiscal receipts.
- **Government & partner integrations:** Soliq (tax committee), e-invoices (Factura), product marking
  (Asl Belgisi / Turon), E-IMZO digital signatures, product classifier (Tasnif), 1C, marketplace, Xtrace.
- **Reporting:** a report engine (datasets, execution, scheduled exports), **ClickHouse** for analytical data
  and log history, Excel/PDF export.
- **Background processing:** about 15 Quartz cron jobs, and RabbitMQ and Kafka producers/consumers.
- **Channels:** admin panel, customer cabinet, mobile app, Telegram bot and terminal APIs.

## Tech stack
**Java 21 · Spring Boot 3.3 · PostgreSQL · ClickHouse · Redis · RabbitMQ · Kafka · MinIO · Liquibase ·
MapStruct · OpenFeign · Resilience4j · Quartz · Apache POI / iText · JWT security · Micrometer/Prometheus ·
Docker · Maven** (generated with JHipster 7.3 and heavily extended)

## Project layout
```
src/main/java/uz/uzkassa/smartpossupply
├── domain/        JPA entities (stock, order, product, warehouse, billing, ...)
├── service/       business logic
├── web/rest/      REST API: admin / cabinet / mobile / terminal / telegram / integration / pub
├── integration/   external clients (Soliq, Factura, OFD, E-IMZO, 1C, Asl Belgisi, ...)
├── rabbitmq/      async producers & consumers
├── crons/         scheduled jobs (Quartz)
├── report/        report engine (dataset → execution → export)
└── config/, security/, aop/, mappers/, repository/
```

## Run
```bash
./mvnw spring-boot:run
```
Requires PostgreSQL, Redis, RabbitMQ and ClickHouse. All connection settings and secrets are
`${ENV_VAR}` placeholders (see `.env.example`).
