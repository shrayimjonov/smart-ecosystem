# Smart Ecosystem

Backend of Smart Ecosystem (smartpos.uz). Shops, restaurants and distributors in Uzbekistan use it for
point of sale, warehouse, restaurant management and product marking.

This is a cleaned copy of the real project. Passwords, keys, server addresses and personal data were
removed, so some integrations will not work as is.

## Apps

- supply - Smart Supply, warehouse management used by 150+ companies
- horeca - Smart HoReCa, management for restaurants, cafes and hotels
- smartpos - POS backend: products, warehouse, receipts
- erp - internal ERP: CRM, HR, billing, documents
- silen - product marking integration

## Smart Supply

- stock in, stock transfers, stock adjustments and inventory
- heavy reports on ClickHouse so they stay fast on big data
- RabbitMQ for background processing and Redis for caching
- integrations with the tax committee (Soliq), payment terminals (payments and receipts) and 1C
- REST API for the sales agent mobile app

## Smart HoReCa

- halls and tables setup, table booking, push notifications for staff (Firebase), customers and discounts

## Stack

Java 21, Spring Boot 3, PostgreSQL, ClickHouse, Redis, RabbitMQ, Kafka, MinIO, MongoDB, Liquibase,
MapStruct, OpenFeign, Resilience4j, Quartz, Apache POI, Firebase, Telegram Bot API, Docker, Maven.

## How to run

Each app is a separate Spring Boot service:

```bash
cd supply
./mvnw spring-boot:run
```

Settings come from environment variables, see `.env.example`.

## What was removed

Passwords, tokens, keys, certificates, prod/dev config profiles, CI files, Maven settings.xml, internal
hosts, personal data, business documents and git history.
