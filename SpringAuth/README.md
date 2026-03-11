# Auth Service

Authentication service for SpringSuite with JWT-based authentication.

## Endpoints

### Register
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "john",
  "email": "john@example.com",
  "password": "password123",
  "role": "USER"
}
```

### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "john",
  "password": "password123"
}
```

Response:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "john",
  "role": "USER"
}
```

## Default Users (Development)

| Username | Password | Role |
|----------|----------|------|
| admin    | admin123 | ADMIN |
| user     | user123  | USER  |

## Running with Docker

```bash
docker-compose up --build
```

## Running Locally

1. Start PostgreSQL database
2. Update `application.properties` with your database credentials
3. Run: `./gradlew bootRun`
