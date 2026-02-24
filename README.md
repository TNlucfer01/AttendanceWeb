# Attendance Web App

A college attendance management system with role-based access, leave workflows, timetable management, and automated reporting.

## Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java 17, Spring Boot 3.2.3 |
| Database | MariaDB (MySQL-compatible) |
| Auth | Spring Security + JWT |
| ORM | Spring Data JPA / Hibernate |
| Build | Maven |
| Templates | Thymeleaf (upcoming) |

## Prerequisites

- **Java 17+** — `java -version`
- **Maven 3.9+** — `mvn -version`
- **MariaDB / MySQL** — `mysql --version`

## Database Setup

```bash
# Start MariaDB
sudo systemctl start mariadb

# Create database and user
sudo mysql -u root -p
```

```sql
CREATE DATABASE attendancedb;
CREATE USER 'attendance'@'localhost' IDENTIFIED BY 'Attendance@123';
GRANT ALL PRIVILEGES ON attendancedb.* TO 'attendance'@'localhost';
FLUSH PRIVILEGES;
EXIT;
```

> Tables are auto-created by Hibernate on first run (`ddl-auto=update`).

## Run the App

```bash
# Compile
mvn compile

# Run
mvn spring-boot:run
```

The app starts on **http://localhost:8080**.

## Default Admin Account

| Field | Value |
|---|---|
| Email | `admin@attendance.com` |
| Password | `Admin@123` |

> You'll be prompted to change the password on first login (`mustChangePassword: true`).

## API Endpoints

### Auth (Public)

```bash
# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@attendance.com","password":"Admin@123"}'

# Response: { "token": "eyJ...", "role": "ADMIN", "mustChangePassword": true }
```

### Protected Endpoints (require `Authorization: Bearer <token>`)

```bash
# Get current user profile
curl http://localhost:8080/api/auth/me \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbkBhdHRlbmRhbmNlLmNvbSIsInVzZXJJZCI6MSwicm9sZSI6IkFETUlOIiwiaWF0IjoxNzcxODU2MTQwLCJleHAiOjE3NzE4NTc5NDB9.v97s4tM7dxSoCIjChQAP9T3At4sRwrhwmNOw5ZEZ5AF20hMdhNOG9ZV1xSsSML3JVB6i4JMT8PcUe7krwh4yRw"

# Change password
curl -X POST http://localhost:8080/api/auth/change-password \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"oldPassword":"Admin@123","newPassword":"NewAdmin@1"}'
```

### Role-Based URL Patterns

| Path | Required Role |
|---|---|
| `/api/admin/**` | ADMIN |
| `/api/principal/**` | PRINCIPAL |
| `/api/hod/**` | HOD |
| `/api/staff/**` | STAFF |
| `/api/student/**` | STUDENT |

## Project Structure

```
src/main/java/com/attendance/
├── AttendanceApplication.java
├── config/          # Security, JWT config
├── auth/            # Login, JWT, filters
├── model/           # JPA entities (20)
│   └── enums/       # Status enums (13)
├── repository/      # JPA repositories
├── dto/             # Request/Response objects
├── exception/       # Global error handler
└── seed/            # Admin + config seeder
```

## Configuration

All config is in `src/main/resources/application.properties`:

| Property | Default | Description |
|---|---|---|
| `server.port` | `8080` | App port |
| `spring.datasource.url` | `jdbc:mariadb://localhost:3306/attendancedb` | DB connection |
| `app.jwt.expiration-ms` | `1800000` (30 min) | JWT token expiry |
| `app.admin.email` | `admin@attendance.com` | Seeded admin email |
| `app.admin.password` | `Admin@123` | Seeded admin password |

## User Roles (Hierarchy)

```
Admin → creates → Principal
  Principal → creates → Department + HOD
    HOD → creates → ClassRoom, Subject, Staff, Timetable
      Staff (Class Advisor) → creates → Students
      Staff (Subject Staff) → marks → Attendance
        Student → submits → Leave/OD requests
```

## License

Internal project — not for distribution.
