# Smart HoReCa — Restaurant, Cafe & Hotel Management Backend

**Smart HoReCa** is the restaurant/cafe/hotel backend of the Smart Ecosystem. Part of
[smart-ecosystem](../README.md).

> I was a core developer of this service (about 60% of commits in 2024). My main areas were hall and table
> customization, the reservation (booking) system, staff push notifications, customer management and discounts.

## Features
- **Halls & tables:** configurable halls, hall items and tables, table statistics, hall-to-employee assignment.
- **Reservations:** booking of tables from the cabinet, admin panel and mobile app; a scheduled job
  (`ReservationCronJob`) handles reservation status changes.
- **Orders & menu:** menus, menu items, tech cards (recipes), orders and order items, bills and receipts, shifts, QR menu.
- **Staff notifications:** push notifications via **Firebase Cloud Messaging**, plus Telegram and SMS.
- **Customers & discounts:** customer base and discount/compensation handling.
- **Warehouse:** stock-in/out, transfers, adjustments, current-stock recalculation (`CurrentStockCronJob`).
- **Integrations:** billing service, Soliq (tax committee), Factura (e-invoices), Tasnif (product classifier),
  E-IMZO, payment terminals.
- **Channels:** separate REST layers for `admin`, `cabinet` (business owner), `mobile` (waiters), `terminal`,
  `billing` and public endpoints.

## Tech stack
**Java 21 · Spring Boot 3.3 · PostgreSQL · ClickHouse (log history) · Redis · RabbitMQ · Kafka · Firebase Admin SDK ·
MapStruct · Amazon S3-compatible storage · Docker · Maven**

## Running
```bash
./mvnw spring-boot:run
```

Configuration comes from environment variables (`${ENV_VAR}` placeholders in `application.yml`; see the root
[`.env.example`](../.env.example)).
