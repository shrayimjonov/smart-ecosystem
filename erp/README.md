# Purpose

This project is an ERP System built using modern technologies to manage various business processes of Smart
Ecosystem (https://smartpos.uz). The system is divided into multiple modules, each addressing a different aspect of
enterprise resource management:

CRM (Customer Relationship Management)

HRM (Human Resource Management)

Billing

Inventory Management

Document Management

With this application, businesses can streamline their operations, automate workflows, and enhance productivity through
a robust, scalable, and flexible architecture.

# Tech Stack

Spring Boot: 3.3.1 https://spring.io/blog/2024/06/20/spring-boot-3-3-1-available-now

Java: 21 https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html

Maven: 4.0.0 https://maven.apache.org/docs/4.0.0-alpha-2/release-notes.html

Databases:

PostgreSQL: 14.00 https://www.postgresql.org/docs/release/14.0/

ClickHouse: 0.0.6. https://clickhouse.com/docs

RabbitMQ  https://www.rabbitmq.com

## Build and Run

1. <b> Build the project: </b>

<code> mvn clean install </code>

2. <b> Run the application: </b>

<code> mvn spring-boot:run </code>

The application will be available at: http://localhost:8080

# API Documentation

API documentation is generated using SpringDoc OpenAPI. After starting the application, access the documentation at:
http://localhost:8080/swagger-ui.html

# Java Coding Standards and API Design Guidelines

 <h2> Naming Conventions </h2>
 <b>1. Variable Naming: </b>

Use camelCase for variable names.
✅ firstName
❌ firstname, first_name
<b>2. DTO Naming Convention: </b>

Suffix Data Transfer Object (DTO) classes with DTO.
✅ CompanyDTO
❌ CompanyDto

<b>3. Constant Naming:</b>

Use UPPER_CASE with underscores for constant values.
✅ CONTENT_TYPE
❌ contentType, ContentType
<b>4. Entity Serialization: </b>

All domain entities must include a serialVersionUID field for serialization compatibility.
<h2> API Design </h2>
<b>5. Environment-Based API Separation:</b>

Divide APIs by environments:
<b> Admin: </b> Management and configuration endpoints </br>
<b> Cabinet: </b> User-specific or customer-facing endpoints </br>
<b> Public: </b> Open or publicly accessible endpoints </br>
<b> 6. API Versioning: </b>

Use versioning in API paths to handle changes and backward compatibility.
✅ /api/admin/v1/users
✅ /api/cabinet/v2/orders
✅ /api/public/v1/products

<h2>Query & Data Handling </h2>
<b> 7. Typed Queries: </b>

Use TypedQuery for JPQL or HQL queries to ensure type safety and avoid runtime issues.
<b> 8. Boolean Data Types:</b>

Use Boolean (wrapper class) for fields in filter classes to handle null values.
Use the primitive boolean type for all other classes, unless nullability is required.

