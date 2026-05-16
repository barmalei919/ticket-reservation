-- ============================================================
-- Тестовые данные для Bus Ticket Reservation System
-- Запустить один раз: psql -U postgres -d postgres -f init-data.sql
-- ============================================================

-- Маршруты
INSERT INTO routes (town_from, town_to, kilometres) VALUES
('Минск',   'Брест',    350),
('Минск',   'Гродно',   278),
('Минск',   'Гомель',   302),
('Минск',   'Витебск',  274),
('Брест',   'Гродно',   300),
('Гомель',  'Могилёв',  100),
('Минск',   'Могилёв',  199),
('Витебск', 'Гродно',   447);

-- Автобусы
INSERT INTO buses (plate_number, capacity) VALUES
('АА 1234-7', 20),
('ВВ 5678-7', 30);

-- Места для автобуса 1 (20 мест)
INSERT INTO seats (seat_number, bus_id)
SELECT gs, b.id FROM generate_series(1, 20) AS gs, buses b WHERE b.plate_number = 'АА 1234-7';

-- Места для автобуса 2 (30 мест)
INSERT INTO seats (seat_number, bus_id)
SELECT gs, b.id FROM generate_series(1, 30) AS gs, buses b WHERE b.plate_number = 'ВВ 5678-7';

-- Рейсы (статус APPROVED = активный рейс, на который можно купить билет)
INSERT INTO trips (time_start, time_end, price, status, route_id, bus_id)
SELECT '2026-05-20 07:00:00', '2026-05-20 12:30:00', 1800.00, 'APPROVED',
       r.id, b.id
FROM routes r, buses b
WHERE r.town_from = 'Минск' AND r.town_to = 'Брест' AND b.plate_number = 'АА 1234-7';

INSERT INTO trips (time_start, time_end, price, status, route_id, bus_id)
SELECT '2026-05-21 09:00:00', '2026-05-21 13:45:00', 1400.00, 'APPROVED',
       r.id, b.id
FROM routes r, buses b
WHERE r.town_from = 'Минск' AND r.town_to = 'Гродно' AND b.plate_number = 'ВВ 5678-7';

INSERT INTO trips (time_start, time_end, price, status, route_id, bus_id)
SELECT '2026-05-22 06:30:00', '2026-05-22 11:00:00', 1600.00, 'APPROVED',
       r.id, b.id
FROM routes r, buses b
WHERE r.town_from = 'Минск' AND r.town_to = 'Гомель' AND b.plate_number = 'АА 1234-7';

INSERT INTO trips (time_start, time_end, price, status, route_id, bus_id)
SELECT '2026-05-23 14:00:00', '2026-05-23 18:30:00', 1350.00, 'APPROVED',
       r.id, b.id
FROM routes r, buses b
WHERE r.town_from = 'Минск' AND r.town_to = 'Витебск' AND b.plate_number = 'ВВ 5678-7';

INSERT INTO trips (time_start, time_end, price, status, route_id, bus_id)
SELECT '2026-05-24 10:00:00', '2026-05-24 15:00:00', 1550.00, 'APPROVED',
       r.id, b.id
FROM routes r, buses b
WHERE r.town_from = 'Брест' AND r.town_to = 'Гродно' AND b.plate_number = 'АА 1234-7';
