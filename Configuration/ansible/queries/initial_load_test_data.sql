
-- Tabela: Facility
INSERT INTO Facility (address, city) VALUES
('ul. Centralna 1', 'Warszawa'),
('al. Jerozolimskie 123', 'Warszawa'),
('ul. Długa 45', 'Kraków');

---
-- Tabela: Specialization
INSERT INTO Specialization (name) VALUES
('Alergolog'),
('Anestezjolog'),
('Audiolog'),
('Chirurg'),
('Chirurg dziecięcy'),
('Chirurg naczyniowy'),
('Chirurg onkologiczny'),
('Chirurg plastyczny'),
('Chirurg stomatologiczny'),
('Ortopeda'),
('Dermatolog'),
('Diabetolog'),
('Endokrynolog'),
('Gastroenterolog'),
('Geriatra'),
('Ginekolog'),
('Hematolog'),
('Kardiolog'),
('Laryngolog'),
('Lekarz medycyny estetycznej'),
('Lekarz medycyny pracy'),
('Lekarz rodzinny'),
('Nefrolog'),
('Neonatolog'),
('Neurolog'),
('Neurochirurg'),
('Okulista'),
('Onkolog'),
('Ortopeda traumatolog'),
('Patomorfolog'),
('Pediatra'),
('Psychiatra'),
('Psychiatra dziecięcy'),
('Pulmonolog'),
('Radiolog'),
('Reumatolog'),
('Stomatolog'),
('Toksykolog'),
('Transplantolog'),
('Urolog'),
('Specjalista chorób zakaźnych');

---
-- Tabela: Service
-- Rodzaje usług: REGULAR_SERVICE, ADDITIONAL_SERVICE
-- Czas trwania (estimated_time) jest teraz wielokrotnością 15 minut.
INSERT INTO Service (name, price, type, estimated_time) VALUES
('Konsultacja alergologiczna', 200.00, 'REGULAR_SERVICE', 30),
('Konsultacja kardiologiczna', 250.00, 'REGULAR_SERVICE', 30),
('Badanie USG jamy brzusznej', 180.00, 'REGULAR_SERVICE', 30),
('Konsultacja dermatologiczna', 220.00, 'REGULAR_SERVICE', 30),
('Badanie okulistyczne', 150.00, 'ADDITIONAL_SERVICE', 15),
('Konsultacja neurologiczna', 240.00, 'REGULAR_SERVICE', 30),
('Konsultacja ortopedyczna', 230.00, 'REGULAR_SERVICE', 30),
('Konsultacja endokrynologiczna', 210.00, 'REGULAR_SERVICE', 30),
('Konsultacja ginekologiczna', 200.00, 'REGULAR_SERVICE', 30),
('Badanie RTG klatki piersiowej', 100.00, 'ADDITIONAL_SERVICE', 15),
('Konsultacja laryngologiczna', 180.00, 'REGULAR_SERVICE', 30),
('Konsultacja pediatryczna', 190.00, 'REGULAR_SERVICE', 30),
('Konsultacja psychiatryczna', 250.00, 'REGULAR_SERVICE', 30),
('Badanie spirometryczne', 120.00, 'ADDITIONAL_SERVICE', 15),
('Morfologia krwi', 100.00, 'ADDITIONAL_SERVICE', 15),
('Konsultacja nefrologiczna', 230.00, 'REGULAR_SERVICE', 30),
('Badanie EKG', 210.00, 'ADDITIONAL_SERVICE', 30);

---
-- Tabela: Specialization_Service (Powiązanie usług ze specjalizacjami)
INSERT INTO Specialization_Service (service_id, specialization_id) VALUES
(1, 1),    -- Konsultacja alergologiczna → Alergolog
(2, 18),   -- Konsultacja kardiologiczna → Kardiolog
(3, 14),   -- USG jamy brzusznej → Gastroenterolog
(4, 11),   -- Konsultacja dermatologiczna → Dermatolog
(6, 25),   -- Konsultacja neurologiczna → Neurolog
(7, 10),   -- Konsultacja ortopedyczna → Ortopeda
(8, 13),   -- Konsultacja endokrynologiczna → Endokrynolog
(9, 16),   -- Konsultacja ginekologiczna → Ginekolog
(11, 19),  -- Konsultacja laryngologiczna → Laryngolog
(12, 30),  -- Konsultacja pediatryczna → Pediatra
(13, 31),  -- Konsultacja psychiatryczna → Psychiatra
(15, 22);  -- Konsultacja nefrologiczna → Nefrolog


---
-- Tabela: Subscription
INSERT INTO Subscription (name, price) VALUES
('Pakiet Podstawowy', 99.00),
('Pakiet Rozszerzony', 199.00),
('Pakiet Premium', 349.00);

---
-- Tabela: Subscription_Service (Powiązanie każdej subskrypcji z każdą usługą REGULAR)
-- Pakiet Podstawowy
INSERT INTO Subscription_Service (subscription_id, service_id, discount) VALUES
(1, 1, 15),
(1, 2, 15),
(1, 3, 20),
(1, 4, 15),
(1, 5, 0),
(1, 6, 15),
(1, 7, 15),
(1, 8, 0),
(1, 9, 15),
(1, 10, 0),
(1, 11, 15),
(1, 12, 15),
(1, 13, 10),
(1, 14, 0),
(1, 15, 10),
(1, 16, 10),
(1, 17, 40);

-- Pakiet Rozszerzony
INSERT INTO Subscription_Service (subscription_id, service_id, discount) VALUES
(2, 1, 50),
(2, 2, 50),
(2, 3, 60),
(2, 4, 50),
(2, 5, 40),
(2, 6, 50),
(2, 7, 50),
(2, 8, 40),
(2, 9, 50),
(2, 10, 40),
(2, 11, 50),
(2, 12, 50),
(2, 13, 45),
(2, 14, 40),
(2, 16, 45),
(2, 15, 45),
(2, 17, 70);

-- Pakiet Premium
INSERT INTO Subscription_Service (subscription_id, service_id, discount) VALUES
(3, 1, 100),
(3, 2, 100),
(3, 3, 100),
(3, 4, 100),
(3, 5, 100),
(3, 6, 100),
(3, 7, 100),
(3, 8, 100),
(3, 9, 100),
(3, 10, 100),
(3, 11, 100),
(3, 12, 100),
(3, 13, 100),
(3, 14, 100),
(3, 15, 100),
(3, 16, 100),
(3, 17, 100);

---
-- Tabela: User
-- Role: DOCTOR, PATIENT, ADMINISTRATOR, NURSE, WARD_CLERK
-- hasło dla wszystkich: 'Password123' (hash: $2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS)
INSERT INTO "User" (first_name, last_name, pesel, passport_number, email, address, password, phone_number, account_status, birthdate, pwz, role, subscription_id, email_notifications) VALUES
-- Lekarze
('Jan', 'Kowalski', NULL, NULL, 'jan.kowalski@documed.pl', 'ul. Lekarska 1, Warszawa', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '111222333', 'ACTIVE', '1980-05-10', '1234567', 'DOCTOR', NULL, true),
('Anna', 'Nowak', NULL, NULL, 'anna.nowak@documed.pl', 'ul. Medyczna 2, Kraków', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '444555666', 'ACTIVE', '1985-09-15', '7654321', 'DOCTOR', NULL, true),
('Piotr', 'Wiśniewski', NULL, NULL, 'piotr.wisniewski@documed.pl', 'ul. Szpitalna 3, Warszawa', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '777888999', 'ACTIVE', '1978-02-20', '1122334', 'DOCTOR', NULL, false),
('Katarzyna', 'Zielińska', NULL, NULL, 'k.zielinska@documed.pl', 'ul. Zdrowia 10, Gdańsk', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '100200300', 'ACTIVE', '1979-01-01', '1000001', 'DOCTOR', NULL, true),
('Marek', 'Kaczmarek', NULL, NULL, 'm.kaczmarek@documed.pl', 'ul. Medyczna 11, Poznań', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '200300400', 'ACTIVE', '1982-04-12', '1000002', 'DOCTOR', NULL, true),
('Agnieszka', 'Wójcik', NULL, NULL, 'a.wojcik@documed.pl', 'ul. Kliniczna 12, Wrocław', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '300400500', 'ACTIVE', '1987-08-23', '1000003', 'DOCTOR', NULL, true),
('Tomasz', 'Lewandowski', NULL, NULL, 't.lewandowski@documed.pl', 'ul. Zdrowotna 13, Katowice', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '400500600', 'ACTIVE', '1975-03-30', '1000004', 'DOCTOR', NULL, false),
('Ewa', 'Dąbrowska', NULL, NULL, 'e.dabrowska@documed.pl', 'ul. Lecznicza 14, Lublin', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '500600700', 'ACTIVE', '1984-06-18', '1000005', 'DOCTOR', NULL, true),
('Robert', 'Zając', NULL, NULL, 'r.zajac@documed.pl', 'ul. Sanatoryjna 15, Białystok', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '600700800', 'ACTIVE', '1981-11-25', '1000006', 'DOCTOR', NULL, true),
('Joanna', 'Mazur', NULL, NULL, 'j.mazur@documed.pl', 'ul. Uzdrowiskowa 16, Rzeszów', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '700800900', 'ACTIVE', '1990-09-10', '1000007', 'DOCTOR', NULL, false),
('Andrzej', 'Baran', NULL, NULL, 'a.baran@documed.pl', 'ul. Rehabilitacyjna 17, Szczecin', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '800900100', 'ACTIVE', '1976-07-07', '1000008', 'DOCTOR', NULL, true),
('Elżbieta', 'Szymańska', NULL, NULL, 'e.szymanska@documed.pl', 'ul. Specjalistyczna 18, Toruń', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '910020030', 'ACTIVE', '1983-10-19', '1000009', 'DOCTOR', NULL, true),
('Grzegorz', 'Pawlak', NULL, NULL, 'g.pawlak@documed.pl', 'ul. Medyków 19, Opole', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '920130040', 'ACTIVE', '1977-05-21', '1000010', 'DOCTOR', NULL, false),
('Małgorzata', 'Czarnecka', NULL, NULL, 'm.czarnecka@documed.pl', 'ul. Zdrowotna 20, Zielona Góra', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '930240050', 'ACTIVE', '1989-02-28', '1000011', 'DOCTOR', NULL, true),
('Krzysztof', 'Wieczorek', NULL, NULL, 'k.wieczorek@documed.pl', 'ul. Zgody 21, Olsztyn', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '940350060', 'ACTIVE', '1980-12-13', '1000012', 'DOCTOR', NULL, true),
('Beata', 'Michalska', NULL, NULL, 'b.michalska@documed.pl', 'ul. Nadziei 22, Kielce', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '950460070', 'ACTIVE', '1985-08-09', '1000013', 'DOCTOR', NULL, false),
('Damian', 'Olszewski', NULL, NULL, 'd.olszewski@documed.pl', 'ul. Kliniczna 23, Gorzów Wielkopolski', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '960570080', 'ACTIVE', '1981-06-15', '1000014', 'DOCTOR', NULL, true),
('Izabela', 'Król', NULL, NULL, 'i.krol@documed.pl', 'ul. Zdrowotna 24, Koszalin', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '970680090', 'ACTIVE', '1978-04-22', '1000015', 'DOCTOR', NULL, true),
('Sebastian', 'Kubiak', NULL, NULL, 's.kubiak@documed.pl', 'ul. Sanatoryjna 25, Legnica', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '980790100', 'ACTIVE', '1986-11-11', '1000016', 'DOCTOR', NULL, true),
('Natalia', 'Walczak', NULL, NULL, 'n.walczak@documed.pl', 'ul. Lecznicza 26, Elbląg', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '990810110', 'ACTIVE', '1991-01-17', '1000017', 'DOCTOR', NULL, false),
('Artur', 'Bąk', NULL, NULL, 'a.bak@documed.pl', 'ul. Medyczna 27, Tarnów', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '910920120', 'ACTIVE', '1983-03-03', '1000018', 'DOCTOR', NULL, true),
('Aleksandra', 'Głowacka', NULL, NULL, 'a.glowacka@documed.pl', 'ul. Tęczowa 28, Radom', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '921030130', 'ACTIVE', '1984-07-14', '1000019', 'DOCTOR', NULL, true),
('Jacek', 'Cieślak', NULL, NULL, 'j.cieslak@documed.pl', 'ul. Przychodnia 29, Siedlce', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '932140140', 'ACTIVE', '1977-10-01', '1000020', 'DOCTOR', NULL, false),
('Magdalena', 'Szulc', NULL, NULL, 'm.szulc@documed.pl', 'ul. Zdrowia 30, Suwałki', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '943250150', 'ACTIVE', '1990-02-08', '1000021', 'DOCTOR', NULL, true),
('Wojciech', 'Sawicki', NULL, NULL, 'w.sawicki@documed.pl', 'ul. Lekarska 31, Gniezno', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '954360160', 'ACTIVE', '1982-09-27', '1000022', 'DOCTOR', NULL, true),
('Karolina', 'Jaworska', NULL, NULL, 'k.jaworska@documed.pl', 'ul. Rehabilitacyjna 32, Tarnobrzeg', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '965470170', 'ACTIVE', '1988-06-05', '1000023', 'DOCTOR', NULL, true),
('Łukasz', 'Bielski', NULL, NULL, 'l.bielski@documed.pl', 'ul. Medyczna 33, Nowy Sącz', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '976580180', 'ACTIVE', '1979-12-31', '1000024', 'DOCTOR', NULL, true),
('Monika', 'Rogowska', NULL, NULL, 'm.rogowska@documed.pl', 'ul. Specjalistyczna 34, Kalisz', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '987690190', 'ACTIVE', '1985-04-11', '1000025', 'DOCTOR', NULL, true),
('Rafał', 'Lis', NULL, NULL, 'r.lis@documed.pl', 'ul. Lecznicza 35, Płock', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '998700200', 'ACTIVE', '1981-07-26', '1000026', 'DOCTOR', NULL, true),
('Maria', 'Sokołowska', NULL, NULL, 'm.sokolowska@documed.pl', 'ul. Tęczowa 44, Łódź', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '990880770', 'ACTIVE', '1986-12-03', '1000041', 'DOCTOR', NULL, true),
-- Pacjenci (id: 31, 32, 33, 34, 35)
('Alicja', 'Zielińska', '90010112345', NULL, 'alicja.zielinska@email.com', 'ul. Pacjenta 1, Warszawa', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '123123123', 'ACTIVE', '1990-01-01', NULL, 'PATIENT', 1, true),
('Tomasz', 'Wójcik', '88031554321', NULL, 'tomasz.wojcik@email.com', 'ul. Zdrowa 2, Kraków', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '321321321', 'ACTIVE', '1988-03-15', NULL, 'PATIENT', 2, true),
('Magdalena', 'Lis', '95072012345', NULL, 'magdalena.lis@email.com', 'ul. Spokojna 3, Warszawa', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '456456456', 'ACTIVE', '1995-07-20', NULL, 'PATIENT', 3, false),
('Krzysztof', 'Lopez', '82110512345', 'ADM12345', 'krzysztof.mazur@email.com', 'ul. Radosna 4, Warszawa', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '654654654', 'ACTIVE', '1982-11-05', NULL, 'PATIENT', NULL, true),
('Ewa', 'Krawczyk', '99123112345', NULL, 'ewa.krawczyk@email.com', 'ul. Cicha 5, Kraków', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '789789789', 'DEACTIVATED', '1999-12-31', NULL, 'PATIENT', NULL, true),
-- Personel (id: 9, 10, 11)
('Admin', 'Adminski', NULL, NULL, 'admin@documed.pl', 'ul. Główna 100, Warszawa', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '000000000', 'ACTIVE', '1990-01-01', NULL, 'ADMINISTRATOR', NULL, true),
('Katarzyna', 'Pielęgniarczyk', NULL, NULL, 'katarzyna.p@documed.pl', 'ul. Pomocna 1, Warszawa', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '101102103', 'ACTIVE', '1992-04-12', NULL, 'NURSE', NULL, true),
('Robert', 'Rejestrator', NULL, NULL, 'robert.r@documed.pl', 'ul. Biurowa 2, Kraków', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '104105106', 'ACTIVE', '1995-11-25', NULL, 'WARD_CLERK', NULL, true);


---
-- Tabela: Doctor_Specialization
INSERT INTO Doctor_Specialization (doctor_id, specialization_id) VALUES
(1, 1), (1, 4), (2, 2), (3, 3), (3, 5),
(12, 14),
(13, 15),
(14, 16),
(4, 17),
(5, 1),
(6, 4),
(7, 7),
(8, 2),
(9, 9),
(10, 14),
(11, 11),
(12, 13),
(13, 1),
(14, 2),
(15, 3),
(16, 18),
(16, 19),
(17, 19),
(18, 20),
(19, 21),
(20, 22),
(21, 23),
(22, 24),
(23, 25),
(24, 26),
(25, 27),
(26, 28),
(27, 29),
(28, 30),
(29, 31),
(30, 32),
(31, 33),
(32, 34),
(33, 35),
(34, 36),
(35, 37),
(36, 38),
(37, 38),
(37, 39),
(37, 22),
(38, 40);

---
-- Tabela: Worktime (Poniedziałek=1, Wtorek=2, ..., Niedziela=7)
-- Dr. Jan Kowalski (ID: 1)
INSERT INTO Worktime (user_id, day_of_week, start_time, end_time, facility_id) VALUES
(1, 1, '08:00:00', '12:00:00', 1), -- Pn, Warszawa Centralna
(1, 2, '08:00:00', '12:00:00', 1), -- Wt, Warszawa Centralna
(1, 3, '14:00:00', '18:00:00', 2), -- Śr, Warszawa Jerozolimskie
(1, 4, '08:00:00', '12:00:00', 1), -- Cz, Warszawa Centralna
(1, 5, '14:00:00', '18:00:00', 2); -- Pt, Warszawa Jerozolimskie
-- Dr. Anna Nowak (ID: 2)
INSERT INTO Worktime (user_id, day_of_week, start_time, end_time, facility_id) VALUES
(2, 1, '09:00:00', '13:00:00', 3), -- Pn, Kraków
(2, 2, '09:00:00', '13:00:00', 3), -- Wt, Kraków
(2, 3, '09:00:00', '13:00:00', 3), -- Śr, Kraków
(2, 4, '09:00:00', '13:00:00', 3), -- Cz, Kraków
(2, 5, '09:00:00', '13:00:00', 3); -- Pt, Kraków
-- Dr. Piotr Wiśniewski (ID: 3)
INSERT INTO Worktime (user_id, day_of_week, start_time, end_time, facility_id) VALUES
(3, 1, '14:00:00', '19:00:00', 1), -- Pn, Warszawa Centralna
(3, 2, '14:00:00', '19:00:00', 2), -- Wt, Warszawa Jerozolimskie
(3, 3, '08:00:00', '12:00:00', 1), -- Śr, Warszawa Centralna
(3, 4, '14:00:00', '19:00:00', 2), -- Cz, Warszawa Jerozolimskie
(3, 5, '08:00:00', '12:00:00', 1); -- Pt, Warszawa Centralna

INSERT INTO Worktime (user_id, day_of_week, start_time, end_time, facility_id) VALUES
(4, 1, '14:00:00', '19:00:00', 1), -- Pn, Warszawa Centralna
(4, 2, '14:00:00', '19:00:00', 2), -- Wt, Warszawa Jerozolimskie
(4, 3, '08:00:00', '12:00:00', 1), -- Śr, Warszawa Centralna
(4, 4, '14:00:00', '19:00:00', 2), -- Cz, Warszawa Jerozolimskie
(4, 5, '08:00:00', '12:00:00', 1); -- Pt, Warszawa Centralna
INSERT INTO Worktime (user_id, day_of_week, start_time, end_time, facility_id) VALUES
(5, 1, '14:00:00', '19:00:00', 1), -- Pn, Warszawa Centralna
(5, 2, '14:00:00', '19:00:00', 2), -- Wt, Warszawa Jerozolimskie
(5, 3, '08:00:00', '12:00:00', 1), -- Śr, Warszawa Centralna
(5, 4, '14:00:00', '19:00:00', 2), -- Cz, Warszawa Jerozolimskie
(5, 5, '08:00:00', '12:00:00', 1); -- Pt, Warszawa Centralna
INSERT INTO Worktime (user_id, day_of_week, start_time, end_time, facility_id) VALUES
(6, 1, '14:00:00', '19:00:00', 1), -- Pn, Warszawa Centralna
(6, 2, '14:00:00', '19:00:00', 2), -- Wt, Warszawa Jerozolimskie
(6, 3, '08:00:00', '12:00:00', 1), -- Śr, Warszawa Centralna
(6, 4, '14:00:00', '19:00:00', 2), -- Cz, Warszawa Jerozolimskie
(6, 5, '08:00:00', '12:00:00', 1); -- Pt, Warszawa Centralna
INSERT INTO Worktime (user_id, day_of_week, start_time, end_time, facility_id) VALUES
(7, 1, '14:00:00', '19:00:00', 1), -- Pn, Warszawa Centralna
(7, 2, '14:00:00', '19:00:00', 2), -- Wt, Warszawa Jerozolimskie
(7, 3, '08:00:00', '12:00:00', 1), -- Śr, Warszawa Centralna
(7, 4, '14:00:00', '19:00:00', 2), -- Cz, Warszawa Jerozolimskie
(7, 5, '08:00:00', '12:00:00', 1); -- Pt, Warszawa Centralna
INSERT INTO Worktime (user_id, day_of_week, start_time, end_time, facility_id) VALUES
(8, 1, '14:00:00', '19:00:00', 1), -- Pn, Warszawa Centralna
(8, 2, '14:00:00', '19:00:00', 2), -- Wt, Warszawa Jerozolimskie
(8, 3, '08:00:00', '12:00:00', 1), -- Śr, Warszawa Centralna
(8, 4, '14:00:00', '19:00:00', 2), -- Cz, Warszawa Jerozolimskie
(8, 5, '08:00:00', '12:00:00', 1); -- Pt, Warszawa Centralna
INSERT INTO Worktime (user_id, day_of_week, start_time, end_time, facility_id) VALUES
(9, 1, '14:00:00', '19:00:00', 1), -- Pn, Warszawa Centralna
(9, 2, '14:00:00', '19:00:00', 2), -- Wt, Warszawa Jerozolimskie
(9, 3, '08:00:00', '12:00:00', 1), -- Śr, Warszawa Centralna
(9, 4, '14:00:00', '19:00:00', 2), -- Cz, Warszawa Jerozolimskie
(9, 5, '08:00:00', '12:00:00', 1); -- Pt, Warszawa Centralna
INSERT INTO Worktime (user_id, day_of_week, start_time, end_time, facility_id) VALUES
(10, 1, '14:00:00', '19:00:00', 1), -- Pn, Warszawa Centralna
(10, 2, '14:00:00', '19:00:00', 2), -- Wt, Warszawa Jerozolimskie
(10, 3, '08:00:00', '12:00:00', 1), -- Śr, Warszawa Centralna
(10, 4, '14:00:00', '19:00:00', 2), -- Cz, Warszawa Jerozolimskie
(10, 5, '08:00:00', '12:00:00', 1); -- Pt, Warszawa Centralna
INSERT INTO Worktime (user_id, day_of_week, start_time, end_time, facility_id) VALUES
(11, 1, '14:00:00', '19:00:00', 1), -- Pn, Warszawa Centralna
(11, 2, '14:00:00', '19:00:00', 2), -- Wt, Warszawa Jerozolimskie
(11, 3, '08:00:00', '12:00:00', 1), -- Śr, Warszawa Centralna
(11, 4, '14:00:00', '19:00:00', 2), -- Cz, Warszawa Jerozolimskie
(11, 5, '08:00:00', '12:00:00', 1); -- Pt, Warszawa Centralna
INSERT INTO Worktime (user_id, day_of_week, start_time, end_time, facility_id) VALUES
(12, 1, '14:00:00', '19:00:00', 1), -- Pn, Warszawa Centralna
(12, 2, '14:00:00', '19:00:00', 2), -- Wt, Warszawa Jerozolimskie
(12, 3, '08:00:00', '12:00:00', 1), -- Śr, Warszawa Centralna
(12, 4, '14:00:00', '19:00:00', 2), -- Cz, Warszawa Jerozolimskie
(12, 5, '08:00:00', '12:00:00', 1); -- Pt, Warszawa Centralna
INSERT INTO Worktime (user_id, day_of_week, start_time, end_time, facility_id) VALUES
(13, 1, '14:00:00', '19:00:00', 1), -- Pn, Warszawa Centralna
(13, 2, '14:00:00', '19:00:00', 2), -- Wt, Warszawa Jerozolimskie
(13, 3, '08:00:00', '12:00:00', 1), -- Śr, Warszawa Centralna
(13, 4, '14:00:00', '19:00:00', 2), -- Cz, Warszawa Jerozolimskie
(13, 5, '08:00:00', '12:00:00', 1); -- Pt, Warszawa Centralna
INSERT INTO Worktime (user_id, day_of_week, start_time, end_time, facility_id) VALUES
(14, 1, '14:00:00', '19:00:00', 1), -- Pn, Warszawa Centralna
(14, 2, '14:00:00', '19:00:00', 2), -- Wt, Warszawa Jerozolimskie
(14, 3, '08:00:00', '12:00:00', 1), -- Śr, Warszawa Centralna
(14, 4, '14:00:00', '19:00:00', 2), -- Cz, Warszawa Jerozolimskie
(14, 5, '08:00:00', '12:00:00', 1); -- Pt, Warszawa Centralna
INSERT INTO Worktime (user_id, day_of_week, start_time, end_time, facility_id) VALUES
(15, 1, '14:00:00', '19:00:00', 1), -- Pn, Warszawa Centralna
(15, 2, '14:00:00', '19:00:00', 2), -- Wt, Warszawa Jerozolimskie
(15, 3, '08:00:00', '12:00:00', 1), -- Śr, Warszawa Centralna
(15, 4, '14:00:00', '19:00:00', 2), -- Cz, Warszawa Jerozolimskie
(15, 5, '08:00:00', '12:00:00', 1); -- Pt, Warszawa Centralna

---
-- Tabela: Free_days
INSERT INTO Free_days (user_id, start_date, end_date) VALUES
(1, '2025-07-21', '2025-08-01'), -- Urlop dr. Kowalskiego
(2, '2025-06-24', '2025-06-24'); -- Dzień wolny dr. Nowak

---
-- Tabela: Visit
-- Statusy: PLANNED, IN_PROGRESS, CANCELLED, CLOSED
INSERT INTO Visit (status, interview, diagnosis, recommendations, total_cost, facility_id, service_id, patient_information, patient_id, doctor_id) VALUES
('CLOSED', 'Pacjent zgłasza ból w klatce piersiowej od 2 tygodni.', 'Stabilna choroba wieńcowa', 'Zaleca się regularne przyjmowanie leków, kontrola za 3 miesiące.', 250.00, 1, 1, 'Mam bóle w klastce piersiowej.', 31, 1),
('CLOSED', 'Okresowa kontrola znamion skórnych.', 'Brak niepokojących zmian', 'Kolejna kontrola za rok.', 220.00, 3, 2, NULL, 32, 2),
('PLANNED', 'Pogorszenie ostrości wzroku w prawym oku.', NULL, NULL, 180.00, 2, 3, 'Pierwsza wizyta.', 34, 3),
('CANCELLED', NULL, NULL, NULL, 200.00, 1, 4, NULL, 33, 1),
('CLOSED', 'Pacjentka zgłasza wysypkę na przedramionach.', 'Alergiczne zapalenie skóry', 'Stosować maść z hydrokortyzonem, unikać alergenu.', 50.00, 3, 2, 'Mam wysypkę na przedramionach', 32, 2),
('PLANNED', NULL, NULL, NULL, 250.00, 1, 1, 'Pacjent z bólem w klatce piersiowej.', 33, 1),
('CLOSED', 'Ból głowy od tygodnia.', 'Migrena', 'Zalecane leki przeciwbólowe, kontrola za miesiąc.', 220.00, 2, 2, 'Mam migrenę.', 34, 2),
('CANCELLED', NULL, NULL, NULL, 200.00, 3, 4, NULL, 32, 3),
('CLOSED', 'Zmęczenie i senność.', 'Niedobór żelaza', 'Suplementacja żelaza, dieta bogata w żelazo.', 180.00, 1, 3, NULL, 35, 4),
('PLANNED', NULL, NULL, NULL, 250.00, 2, 1, 'Pierwsza konsultacja kardiologiczna.', 35, 5),
('PLANNED', NULL, NULL, NULL, 220.00, 2, 2, 'Podejrzenie alergii skórnej.', 31, 6),
('CLOSED', 'Kaszel suchy od 3 dni.', 'Zapalenie oskrzeli', 'Antybiotyk przez 5 dni, nawadnianie.', 180.00, 1, 3, NULL, 31, 7),
('PLANNED', NULL, NULL, NULL, 250.00, 3, 1, NULL, 32, 8),
('PLANNED', NULL, NULL, NULL, 250.00, 1, 1, NULL, 33, 9),
('CLOSED', 'Zawroty głowy, nudności.', 'Nadciśnienie', 'Zalecane pomiar ciśnienia, leki.', 250.00, 2, 1, NULL, 34, 10),
('PLANNED', NULL, NULL, NULL, 200.00, 2, 4, 'Wizyta dziecka z gorączką.', 35, 1),
('CLOSED', 'Kontrola znamion skórnych.', 'Brak zmian podejrzanych.', 'Kontrola za 12 miesięcy.', 220.00, 3, 2, NULL, 35, 12),
('CLOSED', 'Kontrola po zawale.', 'Stan stabilny.', 'Zalecane kontynuowanie leków.', 250.00, 1, 1, NULL, 31, 13),
('CANCELLED', NULL, NULL, NULL, 180.00, 3, 3, NULL, 33, 14);

---
-- Tabela: Additional_service
INSERT INTO Additional_service (description, date, fulfiller_id, patient_id, service_id) VALUES
('Wyniki nie zostały jeszcze zamieszczone', '2025-08-11', 1, 32, 5), -- Wykonawca: Lekarz Jan Kowalski (ID: 1)
('Hemoglobina (HGB): 14,2 g/dL (norma: kobiety 12,0–16,0; mężczyźni 14,0–18,0)
Hematokryt (HCT): 42,0% (norma: kobiety 36–46; mężczyźni 40–54)
Erytrocyty (RBC): 4,7 mln/µL (norma: kobiety 3,8–5,2; mężczyźni 4,2–5,6)
Leukocyty (WBC): 6,4 tys./µL (norma: 4,0–10,0)
Płytki krwi (PLT): 250 tys./µL (norma: 150–400)', '2025-08-11', 37, 33, 15), -- Wykonawca: Pielęgniarka Katarzyna (ID: 10)
('Rytm: zatokowy, miarowy
Częstość akcji serca: 72/min
Oś elektryczna serca: prawidłowa
Załamki P: prawidłowe, przed każdym zespołem QRS
Odstęp PQ: 0,16 s (norma: 0,12–0,20 s)', '2025-08-11', 37, 34, 17);

---
-- Tabela: Feedback
INSERT INTO Feedback (rating, text, visit_id) VALUES
(5, 'Bardzo profesjonalne podejście do pacjenta, wszystko dokładnie wyjaśnione.', 1),
(4, 'Wizyta przebiegła sprawnie, ale było małe opóźnienie.', 2);

---
-- Tabela: Referral
INSERT INTO Referral (visit_id, diagnosis, type, expiration_date, status) VALUES
(2, 'Podejrzenie czerniaka', 'Do chirurga onkologa', '2026-08-12', 'ISSUED');

DO $$
DECLARE
    slot_date DATE := CURRENT_DATE - INTERVAL '7 days';
    end_date DATE := CURRENT_DATE + INTERVAL '14 days';
    wt RECORD;
    slot_start TIME;
    slot_end TIME;
BEGIN
    WHILE slot_date < end_date LOOP
        FOR wt IN
            SELECT * FROM Worktime
            WHERE day_of_week = EXTRACT(DOW FROM slot_date)::int
        LOOP
            slot_start := wt.start_time;

            WHILE slot_start < wt.end_time LOOP
                slot_end := slot_start + INTERVAL '15 minutes';

                IF NOT EXISTS (
                    SELECT 1 FROM Time_slot
                    WHERE doctor_id = wt.user_id
                      AND facility_id = wt.facility_id
                      AND date = slot_date
                      AND start_time = slot_start
                      AND end_time = slot_end
                ) THEN
                    INSERT INTO Time_slot (doctor_id, facility_id, date, start_time, end_time, is_busy)
                    VALUES (wt.user_id, wt.facility_id, slot_date, slot_start, slot_end, false);
                END IF;

                slot_start := slot_end;
            END LOOP;
        END LOOP;

        slot_date := slot_date + INTERVAL '1 day';
    END LOOP;
END
$$;


UPDATE time_slot SET visit_id = 1, is_busy = true WHERE id = 2;
UPDATE time_slot SET visit_id = 2, is_busy = true WHERE id = 3258;
UPDATE time_slot SET visit_id = 3, is_busy = true WHERE id = 2455;
UPDATE time_slot SET visit_id = 5, is_busy = true WHERE id = 851;
UPDATE time_slot SET visit_id = 6, is_busy = true WHERE id = 2184;
UPDATE time_slot SET visit_id = 7, is_busy = true WHERE id = 846;
UPDATE time_slot SET visit_id = 9, is_busy = true WHERE id = 887;
UPDATE time_slot SET visit_id = 10, is_busy = true WHERE id = 1963;
UPDATE time_slot SET visit_id = 11, is_busy = true WHERE id = 1999;
UPDATE time_slot SET visit_id = 12, is_busy = true WHERE id = 394;
UPDATE time_slot SET visit_id = 13, is_busy = true WHERE id = 3392;
UPDATE time_slot SET visit_id = 14, is_busy = true WHERE id = 3408;
UPDATE time_slot SET visit_id = 15, is_busy = true WHERE id = 443;
UPDATE time_slot SET visit_id = 16, is_busy = true WHERE id = 3017;
UPDATE time_slot SET visit_id = 17, is_busy = true WHERE id = 1006;
UPDATE time_slot SET visit_id = 18, is_busy = true WHERE id = 1023;
