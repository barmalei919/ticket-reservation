CREATE TABLE users
(
    id       BIGSERIAL PRIMARY KEY,
    name     VARCHAR(255) NOT NULL,
    email    VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role     VARCHAR(50)  NOT NULL
);

CREATE TABLE routes
(
    id         BIGSERIAL PRIMARY KEY,
    town_from  VARCHAR(255) NOT NULL,
    town_to    VARCHAR(255) NOT NULL,
    kilometres INTEGER      NOT NULL
);

CREATE TABLE buses
(
    id           BIGSERIAL PRIMARY KEY,
    capacity     INTEGER      NOT NULL,
    plate_number VARCHAR(255) UNIQUE
);

CREATE TABLE passengers
(
    id        BIGSERIAL PRIMARY KEY,
    name      VARCHAR(255),
    surname   VARCHAR(255),
    otchestvo VARCHAR(255),
    user_id   BIGINT UNIQUE REFERENCES users (id)
);

CREATE TABLE trips
(
    id         BIGSERIAL PRIMARY KEY,
    price      DOUBLE PRECISION NOT NULL,
    status     VARCHAR(50),
    time_start TIMESTAMP(6),
    time_end   TIMESTAMP(6),
    route_id   BIGINT REFERENCES routes (id),
    bus_id     BIGINT REFERENCES buses (id)
);

CREATE TABLE seats
(
    id          BIGSERIAL PRIMARY KEY,
    seat_number INTEGER NOT NULL,
    bus_id      BIGINT REFERENCES buses (id)
);

CREATE TABLE tickets
(
    id            BIGSERIAL PRIMARY KEY,
    status        VARCHAR(50),
    ticket_status VARCHAR(50),
    price         DOUBLE PRECISION NOT NULL,
    passenger_id  BIGINT REFERENCES passengers (id),
    bus_id        BIGINT REFERENCES buses (id),
    trip_id       BIGINT REFERENCES trips (id),
    seat_id       BIGINT REFERENCES seats (id)
);
