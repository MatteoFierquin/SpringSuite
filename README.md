# SpringSuite

A collaborative suite with messaging, calendar, and docs applications built with Spring Boot microservices and Angular.

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

| Service | Port | Description |
|---------|------|-------------|
| **service-registry** | 8761 | Eureka Server - Service discovery |
| **api-gateway** | 8080 | API Gateway - Routing & Auth |
| **auth-service** | 8081 | Authentication with JWT |
| **calendar-service** | 8083 | Calendar & events (coming soon) |

## Quick Start

### Run with Docker Compose

```bash
# Start all services
docker-compose up --build

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

| Username | Password | Role |
|----------|----------|------|
| admin | admin123 | ADMIN |
| user | user123 | USER |

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

