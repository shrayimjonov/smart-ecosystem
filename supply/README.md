# Smart Supply

Warehouse and distribution system, part of Smart Ecosystem. More than 150 companies use it to manage stock,
deliveries and sales agents.

## Features

- Warehouse: stock in, transfers with acceptance acts, adjustments, inventory, product serials, packages,
  price lists, discounts
- Sales: orders, offline orders, mobile API for sales agents, route planning, trips, KPI, supervisors
- Payments: payment terminal integration, receipts, billing, shifts, OFD fiscal receipts
- Integrations: Soliq (tax committee), Factura (e-invoices), Asl Belgisi and Turon (product marking),
  E-IMZO (digital signature), Tasnif, 1C, marketplace, Xtrace
- Reports: report engine with scheduled exports, ClickHouse for analytics and logs, Excel and PDF
- Background jobs: about 15 Quartz jobs, RabbitMQ and Kafka consumers
- APIs for admin panel, customer cabinet, mobile app, Telegram bot and terminals

## Stack

Java 21, Spring Boot 3.3, PostgreSQL, ClickHouse, Redis, RabbitMQ, Kafka, MinIO, Liquibase, MapStruct,
OpenFeign, Resilience4j, Quartz, Apache POI, iText, JWT, Prometheus, Docker, Maven.

## Code layout

```
src/main/java/uz/uzkassa/smartpossupply
  domain/        entities
  service/       business logic
  web/rest/      REST controllers (admin, cabinet, mobile, terminal, telegram, integration)
  integration/   clients for external services
  rabbitmq/      producers and consumers
  crons/         scheduled jobs
  report/        report engine
```

## How to run

```bash
./mvnw spring-boot:run
```

Needs PostgreSQL, Redis, RabbitMQ and ClickHouse. Settings come from environment variables, see
`../.env.example`.
