# 💊 Pill Reminder — Spring Boot Backend

A production-ready REST API for managing medications, schedules, reminders and adherence tracking.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Framework | Spring Boot 3.2 |
| Security | Spring Security + JWT (HS256) |
| Persistence | Spring Data JPA + Hibernate |
| Database | MySQL (prod) / H2 (dev/test) |
| Scheduling | Spring `@Scheduled` |
| Email | Spring Mail (SMTP) |
| Docs | Springdoc OpenAPI / Swagger UI |
| Tests | JUnit 5 + Mockito + MockMvc |
| Build | Maven |
| Java | 17 |

---

## Project Structure

```
src/main/java/com/pillreminder/
├── PillReminderApplication.java
├── config/
│   ├── JwtUtil.java               # JWT generation & validation
│   ├── JwtAuthFilter.java         # Request filter — extracts JWT
│   ├── SecurityConfig.java        # Security chain, CORS, auth provider
│   └── OpenApiConfig.java         # Swagger / OpenAPI setup
├── controller/
│   ├── AuthController.java        # /api/auth/**
│   ├── UserController.java        # /api/users/**
│   ├── MedicationController.java  # /api/medications/**
│   ├── ReminderController.java    # /api/reminders/**
│   └── AdminController.java       # /api/admin/** (ROLE_ADMIN only)
├── dto/
│   └── Dtos.java                  # All request/response DTOs (static inner classes)
├── entity/
│   ├── User.java
│   ├── Medication.java
│   ├── ReminderLog.java
│   └── RefreshToken.java
├── enums/
│   ├── Role.java                  # ROLE_USER, ROLE_ADMIN
│   ├── Frequency.java             # DAILY, TWICE_DAILY, WEEKLY, CUSTOM…
│   └── ReminderStatus.java        # PENDING, TAKEN, MISSED, SNOOZED, SKIPPED
├── exception/
│   ├── GlobalExceptionHandler.java
│   ├── ResourceNotFoundException.java
│   ├── EmailAlreadyExistsException.java
│   ├── TokenRefreshException.java
│   └── ErrorResponse.java
├── repository/
│   ├── UserRepository.java
│   ├── MedicationRepository.java
│   ├── ReminderLogRepository.java
│   └── RefreshTokenRepository.java
├── scheduler/
│   └── ReminderScheduler.java     # Daily generation + overdue check
└── service/
    ├── AuthService.java / impl/AuthServiceImpl.java
    ├── UserService.java / impl/UserServiceImpl.java
    ├── MedicationService.java / impl/MedicationServiceImpl.java
    ├── ReminderService.java / impl/ReminderServiceImpl.java
    ├── NotificationService.java / impl/NotificationServiceImpl.java
    └── impl/UserDetailsServiceImpl.java

src/test/java/com/pillreminder/
├── service/
│   ├── AuthServiceTest.java
│   ├── MedicationServiceTest.java
│   └── ReminderServiceTest.java
└── controller/
    └── AuthControllerIntegrationTest.java
```

---

## Quick Start

### 1. Clone & configure

```bash
git clone https://github.com/your-org/pill-reminder.git
cd pill-reminder
```

Edit `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/pill_reminder?createDatabaseIfNotExist=true
    username: root
    password: your_password

  mail:
    username: your-email@gmail.com
    password: your-app-password   # Gmail App Password

app:
  jwt:
    secret: "your-256-bit-secret-key-here-must-be-long-enough"
  cors:
    allowed-origins:
      - http://localhost:3000
```

### 2. Run with MySQL

```bash
mvn spring-boot:run
```

### 3. Run with H2 (no MySQL needed)

```bash
mvn spring-boot:run -Dspring.profiles.active=h2
# H2 console: http://localhost:8080/h2-console
```

### 4. Run tests

```bash
mvn test -Dspring.profiles.active=h2
```

### 5. Swagger UI

```
http://localhost:8080/swagger-ui.html
```

---

## API Reference

### Authentication — `/api/auth`

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/register` | ✗ | Register new user |
| POST | `/login` | ✗ | Login → JWT tokens |
| POST | `/refresh` | ✗ | Refresh access token |
| POST | `/logout` | ✓ | Logout (invalidate refresh token) |

**Register request:**
```json
{
  "email": "user@example.com",
  "password": "securePass1",
  "fullName": "Jane Doe",
  "phone": "+91-9876543210",
  "timezone": "Asia/Kolkata"
}
```

**Auth response:**
```json
{
  "accessToken": "eyJhbGci...",
  "refreshToken": "uuid-refresh-token",
  "tokenType": "Bearer",
  "expiresIn": 86400,
  "user": { "id": 1, "email": "user@example.com", "fullName": "Jane Doe", ... }
}
```

---

### User — `/api/users`

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/me` | Get current user profile |
| PUT | `/me` | Update name, phone, timezone, notification prefs |
| POST | `/me/change-password` | Change password |
| DELETE | `/me` | Soft-delete account |

---

### Medications — `/api/medications`

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/` | Add new medication |
| GET | `/?activeOnly=true` | List medications |
| GET | `/{id}` | Get single medication |
| PUT | `/{id}` | Update medication |
| DELETE | `/{id}` | Soft-delete (deactivate) |
| GET | `/needs-refill` | Medications below refill threshold |

**Create medication request:**
```json
{
  "name": "Metformin",
  "dosage": "500mg",
  "frequency": "TWICE_DAILY",
  "startDate": "2025-01-01",
  "scheduledTimes": ["08:00", "20:00"],
  "instructions": "Take with food",
  "totalPills": 60,
  "refillReminderAt": 10,
  "color": "white",
  "shape": "oval"
}
```

**Frequency values:** `DAILY`, `TWICE_DAILY`, `THREE_TIMES_DAILY`, `FOUR_TIMES_DAILY`, `EVERY_OTHER_DAY`, `WEEKLY`, `CUSTOM`

---

### Reminders — `/api/reminders`

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/today` | Today's reminders |
| GET | `/?from=&to=` | Reminders in date range |
| GET | `/pending` | All pending reminders |
| GET | `/{id}` | Single reminder log |
| POST | `/{id}/take` | Mark as TAKEN |
| POST | `/{id}/miss` | Mark as MISSED |
| POST | `/{id}/snooze?minutes=10` | Snooze reminder |
| POST | `/{id}/skip?reason=...` | Skip with reason |
| GET | `/stats/adherence?from=&to=` | Adherence statistics |

**Adherence stats response:**
```json
{
  "userId": 1,
  "from": "2025-01-01",
  "to": "2025-01-31",
  "totalScheduled": 62,
  "taken": 58,
  "missed": 3,
  "snoozed": 1,
  "skipped": 0,
  "adherencePercentage": 93.5,
  "perMedication": [
    {
      "medicationId": 1,
      "medicationName": "Metformin",
      "totalScheduled": 62,
      "taken": 58,
      "missed": 3,
      "adherencePercentage": 93.5
    }
  ]
}
```

---

### Admin — `/api/admin` _(ROLE_ADMIN only)_

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/users` | All users (paginated) |
| GET | `/stats/summary` | Platform stats |
| POST | `/reminders/generate?date=` | Trigger reminder generation |
| POST | `/reminders/check-overdue` | Trigger overdue check |

---

## Scheduler Jobs

| Job | Schedule | Description |
|-----|----------|-------------|
| `generateDailyReminders` | `0 0 0 * * *` (midnight) | Create reminder logs for the day |
| `generateTomorrowReminders` | `0 0 23 * * *` (11 PM) | Pre-generate for next day |
| `checkOverdueReminders` | Every 15 minutes | Mark 30+ min overdue as MISSED |

---

## Security

- **Stateless JWT** — no sessions
- **Refresh token rotation** — old token invalidated on each refresh
- **BCrypt** password hashing (strength 10)
- **Role-based access** via `@PreAuthorize`
- **CORS** configurable per-environment

---

## Environment Variables (production)

```bash
SPRING_DATASOURCE_URL=jdbc:mysql://db-host:3306/pill_reminder
SPRING_DATASOURCE_USERNAME=pill_user
SPRING_DATASOURCE_PASSWORD=secret
SPRING_MAIL_USERNAME=noreply@yourapp.com
SPRING_MAIL_PASSWORD=smtp_password
APP_JWT_SECRET=your-256-bit-base64-secret
```

---

## Error Format

All errors return a consistent JSON body:

```json
{
  "timestamp": "2025-04-23T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "fieldErrors": {
    "email": "must be a well-formed email address",
    "password": "size must be between 8 and 2147483647"
  }
}
```
