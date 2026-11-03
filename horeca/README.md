# Smart HoReCa

Backend for restaurants, cafes and hotels, part of Smart Ecosystem.

## Features

- Halls and tables: halls, tables, table stats, assigning staff to halls
- Booking: table reservations from the cabinet, admin panel and mobile app, with a scheduled job for
  reservation status
- Orders and menu: menus, recipes (tech cards), orders, bills, receipts, shifts, QR menu
- Notifications: Firebase push, Telegram and SMS
- Customers and discounts
- Warehouse: stock in and out, transfers, adjustments, stock recalculation job
- Integrations: billing, Soliq, Factura, Tasnif, E-IMZO, payment terminals
- Separate APIs for admin, cabinet (owner), mobile (waiters), terminal and billing

## Stack

Java 21, Spring Boot 3.3, PostgreSQL, ClickHouse, Redis, RabbitMQ, Kafka, Firebase, MapStruct, S3 storage,
Docker, Maven.

## How to run

```bash
./mvnw spring-boot:run
```

Settings come from environment variables, see `../.env.example`.
