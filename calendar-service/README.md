# Calendar Service

Calendar microservice for SpringSuite - manages events, meetings, and scheduling.

## Endpoints

All endpoints are accessible via the API Gateway at `http://localhost:8080/api/calendar/events`

### Get All User Events
```http
GET /api/calendar/events
Header: X-User-Name: {username}
```

### Get Event by ID
```http
GET /api/calendar/events/{id}
Header: X-User-Name: {username}
```

### Create Event
```http
POST /api/calendar/events
Header: X-User-Name: {username}
Content-Type: application/json

{
  "title": "Team Meeting",
  "description": "Weekly sync",
  "location": "Conference Room A",
  "startTime": "2026-03-15T10:00:00",
  "endTime": "2026-03-15T11:00:00",
  "attendees": ["alice", "bob"]
}
```

### Update Event
```http
PUT /api/calendar/events/{id}
Header: X-User-Name: {username}
Content-Type: application/json

{
  "title": "Updated Meeting",
  "startTime": "2026-03-15T14:00:00",
  "endTime": "2026-03-15T15:00:00"
}
```

### Delete Event
```http
DELETE /api/calendar/events/{id}
Header: X-User-Name: {username}
```

### Get Events by Date Range
```http
GET /api/calendar/events/range?start=2026-03-01T00:00:00&end=2026-03-31T23:59:59
Header: X-User-Name: {username}
```

## Running with Docker

```bash
# Start all services including calendar
docker-compose up --build

# Access via Gateway
http://localhost:8080/api/calendar/events
```

## Running Locally

1. Start PostgreSQL:
   ```bash
   docker run -d --name calendar-db -e POSTGRES_DB=calendardb -e POSTGRES_PASSWORD=postgres -p 5433:5432 postgres:16-alpine
   ```

2. Run the service:
   ```bash
   ./gradlew bootRun
   ```

3. Access at: `http://localhost:8083/api/calendar/events`

## Event Model

| Field | Type | Description |
|-------|------|-------------|
| id | Long | Unique identifier |
| title | String | Event title (required, 1-200 chars) |
| description | String | Event details (max 1000 chars) |
| location | String | Event location |
| startTime | LocalDateTime | Start time (required, must be future) |
| endTime | LocalDateTime | End time (required, must be after start) |
| owner | String | Username of creator |
| attendees | List<String> | Invited usernames |
| createdAt | LocalDateTime | Creation timestamp |

## Security

- Authentication is handled by the API Gateway via JWT
- User identity is passed via `X-User-Name` header
