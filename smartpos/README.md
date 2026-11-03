# SMARTPOS

## Overview
SMARTPOS is a point-of-sale system designed to manage sales, inventory, and supply receipts efficiently. The system is built using Java, Spring Boot, and Maven, and it integrates with various repositories and services to handle product and receipt data.

## Features
- Manage catalog of products
- Manage warehouse
- Manage receipts

## Technologies Used
- Java
- Spring Boot
- Maven
- RabbitMQ
- PostgreSQL
- Kafka
- ClickHouse
- MongoDB
- Redis

## Getting Started

### Prerequisites
- Java 21 or higher
- Maven 3.6.0 or higher
- A running instance of RabbitMQ
- A running instance of your PostgreSQL database
- A running instance of your Kafka server
- A running instance of ClickHouse
- A running instance of your Redis server
- A running instance of your MongoDB server

### Installation
1. Clone the repository:
    ```sh
    git clone http://internal.example.com/smartpos/smartpos-backend.git
    cd smartpos
    ```

2. Build the project:
    ```sh
    mvn clean install
    ```

3. Configure project settings in needed `application-{profile}.properties`.

4. Run the application:
    ```sh
    mvn spring-boot:run
    ```

### Installation PostgreSQL
1. Create docker container for PostgreSQL:
    ```sh
    docker run --name smartpos-db -e POSTGRES_PASSWORD=smartpos -e POSTGRES_USER=smartpos -e POSTGRES_DB=smartpos -p 5432:5432 -d postgres:16
    ```

### Installation MongoDB
1. Create docker container for MongoDB:
    ```sh
    docker run --name smartpos-mongo -p 27017:27017 -e MONGO_INITDB_ROOT_USERNAME=smartpos -e MONGO_INITDB_ROOT_PASSWORD=smartpos  -d mongo:7.0.14
    ```

### Installation Redis
1. Create docker container for Redis:
    ```sh
    docker run --name smartpos-redis -p 6379:6379 -d redis:7.4.0
    ```

### Installation ClickHouse
1. Create docker container for ClickHouse:
    ```sh
    docker run -d --name smartpos-clickhouse-server --ulimit nofile=262144:262144 -p 8123:8123 clickhouse/clickhouse-server:24.8.4
    ```

### Installation Kafka
1. Create docker container for kafka
    ```sh
   docker run -d -p 9092:9092 --name smartpos-kafka apache/kafka:3.8.0
   ```

### Installation RabbitMQ
1. Create docker container for RabbitMQ:
    ```sh
    docker run --name smartpos-rabbitmq -e RABBITMQ_DEFAULT_USER=guest -e RABBITMQ_DEFAULT_PASS=guest -p 5672:5672 -p 15672:15672 -d heidiks/rabbitmq-delayed-message-exchange:latest
    ```
