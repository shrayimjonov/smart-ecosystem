# Smart Ecosystem — Retail, HoReCa & Supply-Chain Backend Platform

Backend services of **Smart Ecosystem** ([smartpos.uz](https://smartpos.uz)) — a commercial platform used by
retail businesses, restaurants and distributors in Uzbekistan: point-of-sale, warehouse/supply management,
restaurant management, product marking and an internal ERP.

> **Context.** Built by the engineering team at the Center for Digital Technology and Innovation (Uzkassa),
> where I worked as a Backend Engineer (Oct 2022 – May 2025). Published as a portfolio sample with the
> owner's confidential data removed — see [Security & sanitization](#security--sanitization).

## Applications

| Module | What it does | My role |
|---|---|---|
| [`supply/`](supply) | **Smart Supply** — warehouse management system used by 150+ companies: stock-in/out, transfers, adjustments, inventory, sales-agent mobile API, payment-terminal integration, reports | **Lead contributor** — top committer (≈4,250 of 12,187 commits, 2022–2025) |
| [`horeca/`](horeca) | **Smart HoReCa** — restaurant/hotel/cafe management: halls & tables, bookings, orders, staff notifications, customers, discounts | **Core developer** — ≈60% of commits (816 of 1,360, 2024) |
| [`smartpos/`](smartpos) | SmartPOS backend — product catalog, warehouse, receipts, fiscal integrations | Team project (reference) |
| [`erp/`](erp) | Internal ERP — CRM, HRM, billing, inventory, documents | Team project (reference) |
| [`silen/`](silen) | Product-marking (track & trace) integration with the national marking system | Team project (reference) |

## Tech stack

**Java 21 · Spring Boot 3.2–3.3 · PostgreSQL · ClickHouse · Redis · RabbitMQ · Kafka · MinIO/S3 · MongoDB ·
Liquibase · MapStruct · OpenFeign · Resilience4j · Quartz · Apache POI · Firebase (FCM) · Telegram Bot API ·
Docker · Maven**

## Highlights of my work

**Smart Supply**
- Designed and implemented core warehouse flows: stock-in, stock transfers, stock adjustments and inventory aggregation.
- Moved heavy analytical queries and reports to **ClickHouse**, which kept reporting fast on large datasets.
- **RabbitMQ**-based asynchronous processing and **Redis** caching for hot reference data.
- Integrations with tax-committee (Soliq) services, payment terminals (accepting payments, generating fiscal receipts) and 1C.
- REST API for the sales-agent mobile application.

**Smart HoReCa**
- Implemented hall and table customization, the booking system, staff push notifications (Firebase), customer management and discounts.

## Running locally

Each application is a standalone Spring Boot service:

```bash
cd supply            # or horeca / smartpos / erp / silen
./mvnw spring-boot:run
```

Infrastructure (PostgreSQL, Redis, RabbitMQ, etc.) is configured through environment variables —
see the root [`.env.example`](.env.example).

## Security & sanitization

This is a sanitized copy of private production code:
- All credentials, tokens, keys and certificates were removed; configuration values are `${ENV_VAR}` placeholders.
- Environment-specific profiles (`application-prod/dev/stage.yml`), CI/CD pipelines and Maven `settings.xml` were removed.
- Internal hostnames/IPs, personal data (names, phone numbers, e-mails) and business documents were removed or masked.
- The automated scrub also replaced some localized message texts (`i18n/messages*.properties`, mail templates) that
  contained words such as "password", "phone" or "PINFL" with `${...}` placeholders; those keys are not environment variables.
- Git history is not included.

Because of this, some integrations will not work out of the box — the code is published to demonstrate architecture and implementation quality.
