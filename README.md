# Bus Ticket Reservation System

REST API backend for online bus ticket booking with JWT authentication, role-based access control, and a static frontend.

## Tech Stack

- **Java 17**, **Spring Boot**
- **Spring Security** — JWT authentication (Access + Refresh tokens), BCrypt password hashing
- **Spring Data JPA** / **Hibernate** — ORM, `@Transactional`
- **PostgreSQL** — relational database
- **Lombok**, **Maven**

## Features

- User registration and login with JWT (Access: 30 min, Refresh: 1 day)
- Role-based access: `USER` and `ADMIN`
- Trip search by departure and destination
- Seat availability check per trip
- Booking flow: book → pay → cancel
- Passenger profile linked to user account
- Global exception handling via `@ControllerAdvice`
- DTO + Mapper pattern for clean layer separation
- Unit tests for `BusService` and `TripService`
- Static frontend (HTML/CSS/JS)

## Project Structure

```
src/main/java/.../
├── Controllers/
│   ├── admin/          # Admin-only endpoints
│   └── common/         # Public and user endpoints
├── Services/           # Business logic
├── Repositories/       # Spring Data JPA interfaces
├── Entities/           # JPA entities
├── DTO/                # Request/Response DTOs
├── Mappers/            # Entity ↔ DTO mapping
├── Enums/              # TripStatus, TicketStatus, UserRole
├── security/           # JwtService, JwtFilter, UserDetails
└── config/             # SecurityConfig
```

## API Endpoints

### Auth

| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| POST | `/api/users/register` | Public | Register new user |
| POST | `/api/users/login` | Public | Login, returns `{ token, refreshToken }` |
| GET | `/api/users/me` | Authenticated | Current user info |
| POST | `/api/users/{userId}/profile` | Authenticated | Create passenger profile |
| GET | `/api/users` | ADMIN | Get all users |

### Trips

| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| GET | `/api/trips/search?from=&to=` | Public | Search trips by route |
| GET | `/api/trips/{id}/seats` | Public | Available seats count |
| POST | `/api/trips` | ADMIN | Create trip |

### Bookings

| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| POST | `/api/bookings/book?userId=` | Authenticated | Book a ticket |
| PATCH | `/api/bookings/{ticketId}/pay` | Authenticated | Pay for ticket |
| DELETE | `/api/bookings/cancel/{ticketId}` | Authenticated | Cancel booking |
| GET | `/api/bookings/my` | Authenticated | My bookings |
| GET | `/api/bookings/available-seats?tripId=` | Authenticated | List available seats |
| GET | `/api/bookings/all` | ADMIN | All bookings |

### Admin

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/admin/buses` | Create bus |
| GET | `/api/admin/users` | Get all users |
| DELETE | `/api/admin/users/{id}` | Delete user |
| GET | `/api/admin/bookings/all` | All bookings |

### Other (Public)

- `GET /api/buses/**` — bus info
- `GET /api/routes/**` — route info
- `GET /api/seats/available/**` — available seats

## Getting Started

### Prerequisites

- Java 17+
- PostgreSQL
- Maven

### Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/barmalei919/ticket-reservation.git
   cd ticket-reservation
   ```

2. **Create a PostgreSQL database**
   ```sql
   CREATE DATABASE postgres;
   ```

3. **Configure `application.properties`**

   Edit `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
   spring.datasource.username=your_username
   spring.datasource.password=your_password

   jwt.secret=your_base64_encoded_secret
   jwt.access-expiration-minutes=30
   jwt.refresh-expiration-days=1
   ```

4. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

   The app will start on `http://localhost:8080`.

### Frontend

Static pages are available at:

| Page | URL |
|------|-----|
| Home | `http://localhost:8080/` |
| Trips | `http://localhost:8080/trips.html` |
| Booking | `http://localhost:8080/booking.html` |
| Auth | `http://localhost:8080/auth.html` |
| Profile | `http://localhost:8080/profile.html` |
| Admin | `http://localhost:8080/admin.html` |

## Authentication

All protected endpoints require a Bearer token in the `Authorization` header:

```
Authorization: Bearer <access_token>
```

On token expiry, use the refresh token:
```
POST /api/users/refresh
{ "refreshToken": "..." }
```
