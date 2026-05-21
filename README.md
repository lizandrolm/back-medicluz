# MedicLuz — Backend API

REST API for the MedicLuz Medical Management System.

## Stack

| Tech | Version |
|---|---|
| Java | 21 (LTS) |
| Spring Boot | 3.4.5 |
| Spring Security | 6.x (JWT stateless) |
| Spring Data JPA | 3.4.x |
| PostgreSQL | 16+ |
| Flyway | 10.x |
| JJWT | 0.12.6 |
| SpringDoc OpenAPI | 2.8.3 |
| Lombok | 1.18.36 |

## Getting started

### 1. Requirements
- JDK 21
- Maven 3.9+
- PostgreSQL 16 running locally

### 2. Database
```sql
CREATE DATABASE medicluz;
```

### 3. Configure
Edit `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/medicluz
spring.datasource.username=postgres
spring.datasource.password=your_password
app.jwt.secret=your_256bit_secret_here
```

### 4. Run
```bash
mvn spring-boot:run
```

### 5. Swagger UI
```
http://localhost:8080/api/swagger-ui.html
```

## Authentication flow

```
POST /api/auth/register   → create user
POST /api/auth/login      → { accessToken, refreshToken }
POST /api/auth/refresh-token
POST /api/auth/logout
```

Add header to protected requests:
```
Authorization: Bearer <accessToken>
```

## Default admin credentials (seeded by migration V1)
```
email:    admin@medicluz.com
password: Admin1234!
```

## API Endpoints

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| POST | /auth/register | Public | Register user |
| POST | /auth/login | Public | Login |
| POST | /auth/refresh-token | Public | Refresh token |
| POST | /auth/logout | JWT | Logout |
| GET | /patients | JWT | List patients |
| GET | /patients/{id} | JWT | Get patient |
| POST | /patients | JWT | Create patient |
| PUT | /patients/{id} | JWT | Update patient |
| PATCH | /patients/{id}/status | JWT | Change status |
| DELETE | /patients/{id} | ADMIN | Delete patient |
| GET | /appointments | JWT | List today's appointments |
| POST | /appointments | JWT | Create appointment |
| PUT | /appointments/{id} | JWT | Update appointment |
| PATCH | /appointments/{id}/status | JWT | Change status |
| DELETE | /appointments/{id} | JWT | Delete appointment |
