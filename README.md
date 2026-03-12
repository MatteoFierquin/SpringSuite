# SpringSuite

A collaborative suite with messaging, calendar, and docs applications built with Spring Boot microservices and Angular.

/!\ For full disclosure AI was used in this project, mainly to write tests faster, readme and style.

## Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    Angular Frontend                         │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                    API Gateway (8080)                       │
└─────────────────────────────────────────────────────────────┘
                            │
        ┌───────────────────┼───────────────────┐
        ▼                   ▼                   ▼
┌───────────────┐  ┌───────────────┐  ┌───────────────┐
│   Auth (8081) │  │  Calendar     │  │   Other       │
│   + Eureka    │  │   Service     │  │   Services    │
│   (8761)      │  │               │  │               │
└───────────────┘  └───────────────┘  └───────────────┘
```

## Projects

| Service              | Port | Description                       |
| -------------------- | ---- | --------------------------------- |
| **service-registry** | 8761 | Eureka Server - Service discovery |
| **api-gateway**      | 8080 | API Gateway - Routing & Auth      |
| **auth-service**     | 8081 | Authentication with JWT           |
| **calendar-service** | 8083 | Calendar & events (coming soon)   |

## Quick Start

### Run with Docker Compose

```bash
# Start all services
docker compose up --build

```

### Run Locally

1. **Start Service Registry:**

   ```bash
   cd service-registry
   ./gradlew bootRun
   ```

2. **Start Auth Service:**

   ```bash
   cd SpringAuth
   ./gradlew bootRun
   ```

3. **Access Eureka Dashboard:** http://localhost:8761

### Default Users

| Username | Password | Role  |
| -------- | -------- | ----- |
| admin    | admin123 | ADMIN |
| user     | user123  | USER  |

## Technology Stack

### Backend

- Spring Boot 3.4
- Spring Cloud (Eureka, Gateway)
- Spring Security + JWT
- PostgreSQL
- Docker

### Frontend

- Angular
- Angular Material / Tailwind CSS
- RxJS

## Creating a New Service

### 1. Generate Project with Spring Initializr

Add Dependencies:

- Spring Web
- Eureka Discovery Client
- Spring Security
- Spring Data JPA
- PostgreSQL Driver

### 2. Configure Application

Update `src/main/resources/application.yml`:

```yaml
spring:
  application:
    name: <service-name>-service
  datasource:
    url: jdbc:postgresql://localhost:5432/<service_name_db>
    username: postgres
    password: postgres
  jpa:
    hibernate:
      ddl-auto: update

server:
  port: <port>

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

### 3. Enable Discovery Client

Add `@EnableDiscoveryClient` to your main application class:

```java
package com.springSuite.<service>;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ServiceNameApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceNameApplication.class, args);
    }
}
```

### 4. Update Docker Compose

Add your service to the root `docker-compose.yml`:

```yaml
<service-name>-service:
  build: ./<service-name>-service
  container_name: <service-name>-service
  ports:
    - "<port>:<port>"
  environment:
    - SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/<service_name_db>
    - EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://service-registry:8761/eureka/
  depends_on:
    - postgres
    - service-registry
```

### 5. Register with API Gateway

Add routing configuration in `api-gateway/src/main/resources/application.yml`:

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: <service-name>-service
          uri: lb://<service-name>-service
          predicates:
            - Path=/api/<service>/**
```

### 6. Service Port Reference

| Service          | Port |
| ---------------- | ---- |
| auth-service     | 8081 |
| calendar-service | 8083 |
| <your-service>   | 808x |

### 7. Run Your Service

```bash
cd <service-name>-service
./gradlew bootRun
```

Verify it's registered in Eureka at http://localhost:8761
