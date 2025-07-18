--Czyszcenie istniejących danych

DO $$
DECLARE
    row RECORD;
BEGIN
    EXECUTE 'SET session_replication_role = replica';
    FOR row IN
        SELECT tablename
        FROM pg_tables
        WHERE schemaname = 'public'
    LOOP
        EXECUTE format('TRUNCATE TABLE public.%I RESTART IDENTITY CASCADE', row.tablename);
    END LOOP;
    EXECUTE 'SET session_replication_role = origin';
END $$;



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
('Badanie USG jamy brzusznej', 180.00, 'ADDITIONAL_SERVICE', 30),
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
('Badanie EKG', 210.00, 'ADDITIONAL_SERVICE', 30),
('Konsultacja urologiczna', 200.00, 'REGULAR_SERVICE', 30),
('Konsultacja diabetologiczna', 200.00, 'REGULAR_SERVICE', 30),
('Konsultacja audiologiczna', 200.00, 'REGULAR_SERVICE', 30),
('Badanie medycyny pracy', 200.00, 'REGULAR_SERVICE', 30);


---
-- Tabela: Specialization_Service (Powiązanie usług ze specjalizacjami)
INSERT INTO Specialization_Service (service_id, specialization_id) VALUES
(1, 1),
(2, 18),
(4, 11),
(5, 27),
(6, 25),
(7, 10),
(8, 13),
(9, 16),
(11, 19),
(12, 31),
(13, 32),
(16, 23),
(18, 40),
(19, 12),
(20, 3),
(21, 21);


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
('Jan', 'Kowalski', NULL, NULL, 'jan.kowalski@documed.pl', NULL, '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', NULL, 'ACTIVE', NULL, '1234567', 'DOCTOR', NULL, true),
('Anna', 'Nowak', NULL, NULL, 'anna.nowak@documed.pl', NULL, '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', NULL, 'ACTIVE', NULL, '7654321', 'DOCTOR', NULL, true),
('Piotr', 'Wiśniewski', NULL, NULL, 'piotr.wisniewski@documed.pl', NULL, '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', NULL, 'ACTIVE', NULL, '1122334', 'DOCTOR', NULL, false),
('Katarzyna', 'Zielińska', NULL, NULL, 'k.zielinska@documed.pl', NULL, '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', NULL, 'ACTIVE', NULL, '1000001', 'DOCTOR', NULL, true),
('Marek', 'Kaczmarek', NULL, NULL, 'm.kaczmarek@documed.pl', NULL, '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', NULL, 'ACTIVE', NULL, '1000002', 'DOCTOR', NULL, true),
('Agnieszka', 'Wójcik', NULL, NULL, 'a.wojcik@documed.pl', NULL, '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', NULL, 'ACTIVE', NULL, '1000003', 'DOCTOR', NULL, true),
('Tomasz', 'Lewandowski', NULL, NULL, 't.lewandowski@documed.pl', NULL, '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', NULL, 'ACTIVE', NULL, '1000004', 'DOCTOR', NULL, false),
('Ewa', 'Dąbrowska', NULL, NULL, 'e.dabrowska@documed.pl', NULL, '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', NULL, 'ACTIVE', NULL, '1000005', 'DOCTOR', NULL, true),
('Robert', 'Zając', NULL, NULL, 'r.zajac@documed.pl', NULL, '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', NULL, 'ACTIVE', NULL, '1000006', 'DOCTOR', NULL, true),
('Joanna', 'Mazur', NULL, NULL, 'j.mazur@documed.pl', NULL, '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', NULL, 'ACTIVE', NULL, '1000007', 'DOCTOR', NULL, false),
('Andrzej', 'Baran', NULL, NULL, 'a.baran@documed.pl', NULL, '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', NULL, 'ACTIVE', NULL, '1000008', 'DOCTOR', NULL, true),
('Elżbieta', 'Szymańska', NULL, NULL, 'e.szymanska@documed.pl', NULL, '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', NULL, 'ACTIVE', NULL, '1000009', 'DOCTOR', NULL, true),
('Grzegorz', 'Pawlak', NULL, NULL, 'g.pawlak@documed.pl', NULL, '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', NULL, 'ACTIVE', NULL, '1000010', 'DOCTOR', NULL, false),
('Małgorzata', 'Czarnecka', NULL, NULL, 'm.czarnecka@documed.pl', NULL, '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', NULL, 'ACTIVE', NULL, '1000011', 'DOCTOR', NULL, true),
('Krzysztof', 'Wieczorek', NULL, NULL, 'k.wieczorek@documed.pl', NULL, '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', NULL, 'ACTIVE', NULL, '1000012', 'DOCTOR', NULL, true),
('Beata', 'Michalska', NULL, NULL, 'b.michalska@documed.pl', NULL, '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', NULL, 'ACTIVE', NULL, '1000013', 'DOCTOR', NULL, false),
('Damian', 'Olszewski', NULL, NULL, 'd.olszewski@documed.pl', NULL, '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', NULL, 'ACTIVE', NULL, '1000014', 'DOCTOR', NULL, true),
('Izabela', 'Król', NULL, NULL, 'i.krol@documed.pl', NULL, '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', NULL, 'ACTIVE', NULL, '1000015', 'DOCTOR', NULL, true),
('Sebastian', 'Kubiak', NULL, NULL, 's.kubiak@documed.pl', NULL, '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', NULL, 'ACTIVE', NULL, '1000016', 'DOCTOR', NULL, true),
('Natalia', 'Walczak', NULL, NULL, 'n.walczak@documed.pl', NULL, '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', NULL, 'ACTIVE', NULL, '1000017', 'DOCTOR', NULL, false),
('Artur', 'Bąk', NULL, NULL, 'a.bak@documed.pl', NULL, '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', NULL, 'ACTIVE', NULL, '1000018', 'DOCTOR', NULL, true),
('Aleksandra', 'Głowacka', NULL, NULL, 'a.glowacka@documed.pl', NULL, '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', NULL, 'ACTIVE', NULL, '1000019', 'DOCTOR', NULL, true),
('Jacek', 'Cieślak', NULL, NULL, 'j.cieslak@documed.pl', NULL, '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', NULL, 'ACTIVE', NULL, '1000020', 'DOCTOR', NULL, false),
('Magdalena', 'Szulc', NULL, NULL, 'm.szulc@documed.pl', NULL, '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', NULL, 'ACTIVE', NULL, '1000021', 'DOCTOR', NULL, true),
('Wojciech', 'Sawicki', NULL, NULL, 'w.sawicki@documed.pl', NULL, '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', NULL, 'ACTIVE', NULL, '1000022', 'DOCTOR', NULL, true),
('Karolina', 'Jaworska', NULL, NULL, 'k.jaworska@documed.pl', NULL, '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', NULL, 'ACTIVE', NULL, '1000023', 'DOCTOR', NULL, true),
('Łukasz', 'Bielski', NULL, NULL, 'l.bielski@documed.pl', NULL, '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', NULL, 'ACTIVE', NULL, '1000024', 'DOCTOR', NULL, true),
('Monika', 'Rogowska', NULL, NULL, 'm.rogowska@documed.pl', NULL, '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', NULL, 'ACTIVE', NULL, '1000025', 'DOCTOR', NULL, true),
('Rafał', 'Lis', NULL, NULL, 'r.lis@documed.pl', NULL, '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', NULL, 'ACTIVE', NULL, '1000026', 'DOCTOR', NULL, true),
('Maria', 'Sokołowska', NULL, NULL, 'm.sokolowska@documed.pl', NULL, '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', NULL, 'ACTIVE', NULL, '1000041', 'DOCTOR', NULL, true),

-- Admnistratorzy
('Adam', 'Adminski', NULL, NULL, 'admin@documed.pl', NULL, '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', NULL, 'ACTIVE', NULL, NULL, 'ADMINISTRATOR', NULL, true),
('Krzysztof', 'Kaminski', NULL, NULL, 'k.admin@documed.pl', NULL, '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', NULL, 'ACTIVE', NULL, NULL, 'ADMINISTRATOR', NULL, true),

--Pielęgrarnki
('Katarzyna', 'Pielęgniarczyk', NULL, NULL, 'katarzyna.p@documed.pl', NULL, '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', NULL, 'ACTIVE', NULL, NULL, 'NURSE', NULL, true),
('Anna', 'Szczepaniak', NULL, NULL, 'anna.s@documed.pl', NULL, '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', NULL, 'ACTIVE', NULL, NULL, 'NURSE', NULL, true),
('Ewelina', 'Nowakowska', NULL, NULL, 'ewelina.n@documed.pl', NULL, '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', NULL, 'ACTIVE', NULL, NULL, 'NURSE', NULL, true),
('Arnold', 'Boczek', NULL, NULL, 'arnold.b@documed.pl', NULL, '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', NULL, 'ACTIVE', NULL, NULL, 'NURSE', NULL, true),

--Rejestratorzy
('Robert', 'Rejestratorski', NULL, NULL, 'robert.r@documed.pl', NULL, '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', NULL, 'ACTIVE', NULL, NULL, 'WARD_CLERK', NULL, true),
('Marta', 'Maciejeska', NULL, NULL, 'marta.r@documed.pl', NULL, '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', NULL, 'ACTIVE', NULL, NULL, 'WARD_CLERK', NULL, true),
('Karolina', 'Kowalska', NULL, NULL, 'karolina.r@documed.pl', NULL, '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', NULL, 'ACTIVE', NULL, NULL, 'WARD_CLERK', NULL, true),
('Magdalena', 'Bażant', NULL, NULL, 'magdalena.r@documed.pl', NULL, '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', NULL, 'ACTIVE', NULL, NULL, 'WARD_CLERK', NULL, true),
('Marcelina', 'Zuch', NULL, NULL, 'marcelina.r@documed.pl', NULL, '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', NULL, 'ACTIVE', NULL, NULL, 'WARD_CLERK', NULL, true),

-- Pacjenci
('Alicja', 'Zielińska', '90010112345', NULL, 'alicja.zielinska@email.com', 'ul. Pacjenta 1, Warszawa', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '123123123', 'ACTIVE', '1990-01-01', NULL, 'PATIENT', 1, true),
('Tomasz', 'Wójcik', '88031554321', NULL, 'tomasz.wojcik@email.com', 'ul. Zdrowa 2, Kraków', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '321321321', 'ACTIVE', '1985-03-15', NULL, 'PATIENT', 2, true),
('Magdalena', 'Lis', '95072012345', NULL, 'magdalena.lis@email.com', 'ul. Spokojna 3, Warszawa', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '456456456', 'ACTIVE', '1985-07-20', NULL, 'PATIENT', 3, false),
('Krzysztof', 'Lopez', NULL, 'AD4412345', 'krzysztof.mazur@email.com', 'ul. Radosna 4, Warszawa', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '654654654', 'ACTIVE', '1982-11-05', NULL, 'PATIENT', NULL, true),
('Ewa', 'Krawczyk', '99123112345', NULL, 'ewa.krawczyk@email.com', 'ul. Cicha 5, Kraków', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '789789789', 'DEACTIVATED', '1965-12-31', NULL, 'PATIENT', NULL, true),
('Sonia', 'Teper', '10030346421', NULL, 'sonia.teper32@email.com', 'ul. Wieniawskiego 73, Lubartów', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '555555555', 'ACTIVE', '1991-08-18', NULL, 'PATIENT', 3, false),
('Mateusz', 'Klinger', '60060740049', NULL, 'mateusz.klinger748@documed.pl', 'ul. Koszykowa 86, Warszawa', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '555555555', 'ACTIVE', '1992-12-31', NULL, 'PATIENT', NULL, true),
('Apolonia', 'Szyba', '83091332379', NULL, 'apolonia.szyba859@email.pl', 'ul. Mazowiecka 33, Warszawa', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '555555555', 'ACTIVE', '1995-08-11', NULL, 'PATIENT', NULL, true),
('Krystian', 'Tylec', '39060529938', NULL, 'krystian.tylec460@pacjent.pl', 'ul. Grochowska 124, Warzszawa', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '555555555', 'ACTIVE', '1996-12-28', NULL, 'PATIENT', 1, true),
('Sylwia', 'Friedrich', '91100778320', NULL, 'sylwia.friedrich165@pacjent.pl', 'ul. Warszawska 33, Kraków', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '555555555', 'ACTIVE', '1997-12-11', NULL, 'PATIENT', 1, true),
('Natan', 'Szymajda', '17082467021', NULL, 'natan.szymajda158@pacjent.pl', 'ul. Bolońska 87, Warszawa', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '555555555', 'ACTIVE', '1998-12-31', NULL, 'PATIENT', 1, false),
('Olga', 'Gromala', '68082203478', NULL, 'olga.gromala129@email.pl', 'ul. Koszykowa 86, Warszawa', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '555555555', 'ACTIVE', '1993-12-18', NULL, 'PATIENT', NULL, false),
('Oliwier', 'Morawiak', '55070650035', NULL, 'oliwier.morawiak877@email.com', 'ul. Krakowska 817, Warszawa', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '555555555', 'DEACTIVATED', '1999-12-11', NULL, 'PATIENT', 2, true),
('Karina', 'Mik', '80111083934', NULL, 'karina.mik804@documed.pl', 'ul. Dextera Morgana 77, Warszawa', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '555555555', 'ACTIVE', '1988-11-01', NULL, 'PATIENT', NULL, true),
('Dariusz', 'Kucharz', '01212264480', NULL, 'dariusz.kucharz951@email.com', 'ul. Warnejska 87, Warszawa', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '555555555', 'ACTIVE', '1977-12-12', NULL, 'PATIENT', 1, false),
('Nicole', 'Migała', NULL, 'KS5143176', 'nicole.migała269@documed.pl', 'ul. Wieniawskiego 73, Lubartów', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '555555555', 'ACTIVE', '1999-12-31', NULL, 'PATIENT', NULL, true),
('Filip', 'Szybiak', '93022705327', NULL, 'filip.szybiak517@pacjent.pl', 'ul. Doaksa 37, Warszawa', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '555555555', 'ACTIVE', '1999-12-31', NULL, 'PATIENT', 3, true),
('Lidia', 'Lisik', '78090370758', NULL, 'lidia.lisik842@pacjent.pl', 'ul. Długa 8, Warszawa', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '555555555', 'ACTIVE', '1977-05-31', NULL, 'PATIENT', NULL, true),
('Fryderyk', 'Hutnik', '24221725591', NULL, 'fryderyk.hutnik869@email.com', 'ul. Końska 87, Warszawa', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '555555555', 'ACTIVE', '1999-12-31', NULL, 'PATIENT', NULL, true),
('Monika', 'Loska', '68081415904', NULL, 'monika.loska664@pacjent.pl', 'ul. Oazowa 11, Warszawa', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '555555555', 'ACTIVE', '1999-12-31', NULL, 'PATIENT', 2, true),
('Tymoteusz', 'Morgała', '38101151244', NULL, 'tymoteusz.morgała285@pacjent.pl', 'ul. Ruda 10, Warszawa', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '555555555', 'ACTIVE', '1977-09-22', NULL, 'PATIENT', NULL, false),
('Roksana', 'Torbus', '34082858123', NULL, 'roksana.torbus251@email.com', 'ul. Koszykowa 86, Warszawa', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '555555555', 'ACTIVE', '1999-12-01', NULL, 'PATIENT', NULL, true),
('Mateusz', 'Lesner', '69012451444', NULL, 'mateusz.lesner578@email.pl', 'ul. Warszawska 33, Kraków', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '555555555', 'ACTIVE', '1999-11-05', NULL, 'PATIENT', NULL, true),
('Ewelina', 'Krępa', '23230946979', NULL, 'ewelina.krępa249@pacjent.pl', 'ul. Jubilerska 7, Warszawa', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '555555555', 'ACTIVE', '1999-12-31', NULL, 'PATIENT', 3, true),
('Szymon', 'Raźny', '29032359823', NULL, 'szymon.raźny883@email.com', 'ul. Wieniawskiego 73, Lubartów', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '555555555', 'DEACTIVATED', '1999-12-31', NULL, 'PATIENT', NULL, true),
('Monika', 'Romejko', '10101179046', NULL, 'monika.romejko838@pacjent.pl', 'ul. Grochowska 124, Warszawa', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '555555555', 'ACTIVE', '1999-09-22', NULL, 'PATIENT', NULL, false),
('Jędrzej', 'Jonczyk', '20061125537', NULL, 'jędrzej.jonczyk82@email.com', 'ul. Ostrobramska 177, Warszawa', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '555555555', 'ACTIVE', '1977-12-31', NULL, 'PATIENT', NULL, true),
('Marcelina', 'Pleśniak', NULL, 'NV2113495', 'marcelina.pleśniak631@pacjent.pl', 'ul. Klimeckiego 63, Warszawa', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '555555555', 'ACTIVE', '1999-12-31', NULL, 'PATIENT', NULL, false),
('Miłosz', 'Migda', '00290299975', NULL, 'miłosz.migda542@email.com', 'ul. Hutnicza 98, Warszawa', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '555555555', 'ACTIVE', '1999-12-31', NULL, 'PATIENT', 1, true),
('Eliza', 'Pastuszko', '95072403941', NULL, 'eliza.pastuszko667@email.pl', 'ul. Kanałowa 7, Warszawa', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '555555555', 'ACTIVE', '1999-12-11', NULL, 'PATIENT', NULL, false),
('Dominik', 'Cop', '84102808432', NULL, 'dominik.cop891@email.pl', 'ul. Kameckiego 91, Warszawa', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '555555555', 'ACTIVE', '1977-12-31', NULL, 'PATIENT', NULL, true),
('Elżbieta', 'Litewka', '36022391657', NULL, 'elżbieta.litewka822@email.com', 'ul. Marsa 124, Warszawa', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '555555555', 'ACTIVE', '1999-12-02', NULL, 'PATIENT', 2, true),
('Oskar', 'Klaus', '35071375012', NULL, 'oskar.klaus568@pacjent.pl', 'ul. Gontarska 12, Warszawa', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '555555555', 'ACTIVE', '2000-08-03', NULL, 'PATIENT', NULL, true),
('Ewa', 'Trznadel', '33090729575', NULL, 'ewa.trznadel480@email.com', 'ul. Koszykowa 86, Warszawa', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '555555555', 'ACTIVE', '1977-12-25', NULL, 'PATIENT', NULL, true),
('Nikodem', 'Elsner', NULL, 'AG3392834', 'nikodem.elsner350@email.pl', 'ul. Ryana Goslinga 2077, Warszawa', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '555555555', 'ACTIVE', '1999-12-31', NULL, 'PATIENT', NULL, true),
('Julianna', 'Filus', '37022848989', NULL, 'julianna.filus659@pacjent.pl', 'ul. Wieniawskiego 73, Lubartów', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '555555555', 'ACTIVE', '1999-12-26', NULL, 'PATIENT', NULL, true),
('Olgierd', 'Parzonka', '12121668913', NULL, 'olgierd.parzonka564@email.pl', 'ul. Ostrobramska 177, Warszawa', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '555555555', 'ACTIVE', '1999-12-28', NULL, 'PATIENT', NULL, true),
('Kaja', 'Gumienny', '74081289838', NULL, 'kaja.gumienny159@email.pl', 'ul. Kameralna 51, Warszawa', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '555555555', 'ACTIVE', '1999-12-08', NULL, 'PATIENT', NULL, true),
('Paweł', 'Borkiewicz', '32021167127', NULL, 'paweł.borkiewicz79@email.com', 'ul. Warszawska 33, Kraków', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '555555555', 'ACTIVE', '1977-03-30', NULL, 'PATIENT', 2, true),
('Eliza', 'Brzykcy', '79040156350', NULL, 'eliza.brzykcy989@pacjent.pl', 'ul. Grochowska 124, Warzszawa', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '555555555', 'ACTIVE', '2000-12-30', NULL, 'PATIENT', NULL, true),
('Dominik', 'Garbaciak', '10020276361', NULL, 'dominik.garbaciak78@email.com', 'ul. Medyczna 27, Warszawa', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '555555555', 'ACTIVE', '1993-07-31', NULL, 'PATIENT', NULL, true),
('Kamila', 'Stachów', '93100707643', NULL, 'kamila.stachów412@email.com', 'ul. Koszykowa 86, Warszawa', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '555555555', 'ACTIVE', '1999-05-31', NULL, 'PATIENT', NULL, true),
('Jędrzej', 'Szeja', '84110714116', NULL, 'jędrzej.szeja41@email.com', 'ul. Końska 87, Warszawa', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '555555555', 'DEACTIVATED', '1977-12-01', NULL, 'PATIENT', 3, false),
('Dorota', 'Dorynek', '84030887208', NULL, 'dorota.dorynek829@email.com', 'ul. Lubelska 47, Warszawa', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '555555555', 'ACTIVE', '1999-06-30', NULL, 'PATIENT', NULL, true),
('Tymon', 'Szymula', '45041469023', NULL, 'tymon.szymula374@pacjent.pl', 'ul. Warszawska 33, Kraków', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '555555555', 'DEACTIVATED', '1999-12-31', NULL, 'PATIENT', 1, true),
('Dagmara', 'Plak', '26120256529', NULL, 'dagmara.plak671@email.pl', 'ul. Ostrobramska 177, Warszawa', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '555555555', 'ACTIVE', '1977-12-31', NULL, 'PATIENT', NULL, true),
('Leonard', 'Ratka', '42081029699', NULL, 'leonard.ratka846@pacjent.pl', 'ul. Grochowska 4, Warszawa', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '555555555', 'ACTIVE', '1999-02-27', NULL, 'PATIENT', 3, true),
('Klara', 'Bela', '12221215606', NULL, 'klara.bela41@email.com', 'ul. Barmańska 127, Warszawa', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '555555555', 'ACTIVE', '1965-12-31', NULL, 'PATIENT', NULL, true),
('Ksawery', 'Kopczyk', '04241449280', NULL, 'ksawery.kopczyk447@email.pl', 'ul. Wieniawskiego 73, Lubartów', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '555555555', 'ACTIVE', '1999-12-04', NULL, 'PATIENT', 1, true),
('Sylwia', 'Policht', '94081482499', NULL, 'sylwia.policht554@pacjent.pl', 'ul. Kameckiego 27, Warszawa', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '555555555', 'ACTIVE', '1965-12-24', NULL, 'PATIENT', NULL, false),
('Oskar', 'Kielich', '36021713425', NULL, 'oskar.kielich295@email.pl', 'ul. Koszykowa 86, Warszawa', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '555555555', 'ACTIVE', '1999-12-26', NULL, 'PATIENT', 3, true),
('Karina', 'Hajdas', '33120495683', NULL, 'karina.hajdas989@email.com', 'ul. Grochowska 14, Warszawa', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '555555555', 'ACTIVE', '1999-12-31', NULL, 'PATIENT', 1, true),
('Adrian', 'Kwak', '99121673479', NULL, 'adrian.kwak306@pacjent.pl', 'ul. Hutnicza 124, Warszawa', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '555555555', 'ACTIVE', '1977-12-31', NULL, 'PATIENT', NULL, true),
('Nela', 'Zaucha', '12112766138', NULL, 'nela.zaucha898@pacjent.pl', 'ul. Koszykowa 86, Warszawa', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '555555555', 'ACTIVE', '1999-12-31', NULL, 'PATIENT', NULL, true),
('Fryderyk', 'Kuciel', '72041653994', NULL, 'fryderyk.kuciel683@pacjent.pl', 'ul. Warszawska 33, Kraków', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '555555555', 'ACTIVE', '1999-11-28', NULL, 'PATIENT', NULL, true),
('Dorota', 'Konat', '56121963357', NULL, 'dorota.konat330@email.com', 'aleja Chopina 29/34, Grodzisk Mazowiecki', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '5210223992', 'DEACTIVATED', '1971-02-20', NULL, 'PATIENT', NULL, false),
('Adam', 'Giec', '06251022260', NULL, 'adam.giec165@email.com', 'al. Robotnicza 119, Oleśnica', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '1669756911', 'ACTIVE', '1962-06-07', NULL, 'PATIENT', NULL, true),
('Aurelia', 'Piórek', '11302832666', NULL, 'aurelia.piórek940@email.pl', 'aleja Lipca 92/84, Świętochłowice', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '1139321523', 'ACTIVE', '1973-10-08', NULL, 'PATIENT', NULL, false),
('Robert', 'Garncarek', '70080444760', NULL, 'robert.garncarek97@email.pl', 'aleja Drzymały 65/63, Turek', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '1796960720', 'ACTIVE', '1954-03-03', NULL, 'PATIENT', NULL, true),
('Urszula', 'Gasek', '94041603625', NULL, 'urszula.gasek541@pacjent.pl', 'ul. Wieniawskiego 73, Lubartów', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '0527086404', 'ACTIVE', '1976-10-08', NULL, 'PATIENT', 2, true),
('Eryk', 'Gałaj', '93072463853', NULL, 'eryk.gałaj129@pacjent.pl', 'aleja Zwycięstwa 95, Swarzędz', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '3001458622', 'ACTIVE', '1944-02-16', NULL, 'PATIENT', NULL, true),
('Sandra', 'Kohnke', '13061010435', NULL, 'sandra.kohnke869@email.com', 'al. Kolejowa 713, Dzierżoniów', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '6354044618', 'DEACTIVATED', '1988-11-25', NULL, 'PATIENT', 2, true),
('Witold', 'Simon', '99050789650', NULL, 'witold.simon791@email.pl', 'ul. Baczynskiego 679, Mińsk Mazowiecki', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '8173805569', 'ACTIVE', '1989-03-13', NULL, 'PATIENT', 3, true),
('Roksana', 'Hallmann', '18021113085', NULL, 'roksana.hallmann744@email.pl', 'plac Majowa 02/87, Zambrów', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '7706881315', 'DEACTIVATED', '1989-04-11', NULL, 'PATIENT', NULL, true),
('Norbert', 'Furgała', '59011556240', NULL, 'norbert.furgała143@email.pl', 'pl. Listopada 708, Czerwionka-Leszczyny', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '1216084985', 'ACTIVE', '1990-09-11', NULL, 'PATIENT', NULL, true),
('Malwina', 'Miąsko', '22072839742', NULL, 'malwina.miąsko955@pacjent.pl', 'plac Zbożowa 62/32, Lubliniec', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '4875472625', 'ACTIVE', '1956-07-24', NULL, 'PATIENT', NULL, true),
('Damian', 'Bluszcz', '96110101838', NULL, 'damian.bluszcz93@email.pl', 'plac Morcinka 103, Ząbki', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '5455797757', 'ACTIVE', '1943-09-22', NULL, 'PATIENT', NULL, true),
('Roksana', 'Zubel', '05301093249', NULL, 'roksana.zubel935@email.pl', 'plac Konwaliowa 868, Bełchatów', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '2475060377', 'ACTIVE', '1958-04-14', NULL, 'PATIENT', NULL, true),
('Błażej', 'Wlazły', '18011082926', NULL, 'błażej.wlazły990@email.pl', 'pl. Morelowa 59/23, Skarżysko-Kamienna', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '7716712532', 'ACTIVE', '1955-08-06', NULL, 'PATIENT', 2, false),
('Ada', 'Nitkiewicz', '62090629875', NULL, 'ada.nitkiewicz490@pacjent.pl', 'aleja Chełmońskiego 49/01, Bydgoszcz', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '6357872537', 'ACTIVE', '1972-06-16', NULL, 'PATIENT', 1, true),
('Mateusz', 'Piejko', '91081088700', NULL, 'mateusz.piejko874@documed.pl', 'pl. Strumykowa 16/44, Sanok', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '0820783716', 'ACTIVE', '1973-10-02', NULL, 'PATIENT', 3, true),
('Dagmara', 'Trojanowicz', '12321706794', NULL, 'dagmara.trojanowicz198@pacjent.pl', 'ulica Daszyńskiego 355, Goleniów', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '2120271494', 'ACTIVE', '1956-09-27', NULL, 'PATIENT', NULL, true),
('Gabriel', 'Zaczyk', '21322779218', NULL, 'gabriel.zaczyk95@email.pl', 'ulica Okulickiego 309, Kutno', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '4739180209', 'ACTIVE', '1977-03-18', NULL, 'PATIENT', NULL, false),
('Julianna', 'Szajner', '69032248293', NULL, 'julianna.szajner81@pacjent.pl', 'ul. Cicha 46/26, Gniezno', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '6535931356', 'ACTIVE', '1967-08-30', NULL, 'PATIENT', 1, true),
('Ksawery', 'Langa', '43010508346', NULL, 'ksawery.langa451@pacjent.pl', 'aleja Gołębia 10, Przemyśl', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '3073532425', 'ACTIVE', '1990-09-24', NULL, 'PATIENT', NULL, true),
('Olga', 'Maziarczyk', '75110454481', NULL, 'olga.maziarczyk642@pacjent.pl', 'aleja Nowowiejska 07, Toruń', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '3039268863', 'ACTIVE', '1957-01-21', NULL, 'PATIENT', 2, true),
('Ernest', 'Pacia', '44050200911', NULL, 'ernest.pacia351@email.pl', 'ul. Daszyńskiego 76, Piekary Śląskie', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '5787493445', 'DEACTIVATED', '1963-02-20', NULL, 'PATIENT', NULL, true),
('Ida', 'Tórz', '15062803304', NULL, 'ida.tórz3@email.com', 'plac Pszenna 31, Bochnia', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '3349071186', 'ACTIVE', '1986-12-08', NULL, 'PATIENT', NULL, true),
('Józef', 'Towarek', '37080666512', NULL, 'józef.towarek853@email.com', 'plac Miłosza 338, Białystok', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '7593505175', 'ACTIVE', '2000-09-14', NULL, 'PATIENT', NULL, true),
('Gaja', 'Bobryk', '69093038871', NULL, 'gaja.bobryk311@email.com', 'pl. Konstytucji 3 Maja 86/37, Piła', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '4720157709', 'ACTIVE', '1954-03-26', NULL, 'PATIENT', NULL, false),
('Jacek', 'Węgrzynowicz', '29051275791', NULL, 'jacek.węgrzynowicz194@email.com', 'al. Osiedlowa 622, Luboń', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '3515009864', 'ACTIVE', '1986-01-17', NULL, 'PATIENT', 2, true),
('Marianna', 'Mila', '25021542607', NULL, 'marianna.mila762@email.pl', 'ulica Chmielna 422, Koszalin', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '2927555284', 'ACTIVE', '1948-12-03', NULL, 'PATIENT', 1, true),
('Rafał', 'Sąsiadek', '79052868162', NULL, 'rafał.sąsiadek260@email.pl', 'pl. Kopernika 94, Nowy Targ', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '2945546674', 'ACTIVE', '1966-10-12', NULL, 'PATIENT', 2, false),
('Aniela', 'Strug', '14062177310', NULL, 'aniela.strug927@email.pl', 'al. Konstytucji 3 Maja 35/83, Żary', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '8467872611', 'ACTIVE', '1986-09-15', NULL, 'PATIENT', 3, false),
('Albert', 'Niedbał', '20010298435', NULL, 'albert.niedbał9@email.com', 'ulica Rycerska 50, Zambrów', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '1836595762', 'ACTIVE', '1994-01-12', NULL, 'PATIENT', NULL, true),
('Eliza', 'Kolek', '74063044598', NULL, 'eliza.kolek23@pacjent.pl', 'ul. Szarych Szeregów 44, Sopot', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '0581550828', 'ACTIVE', '1997-11-11', NULL, 'PATIENT', NULL, true),
('Kazimierz', 'Plaskota', '94033063024', NULL, 'kazimierz.plaskota129@email.pl', 'ul. Mokra 197, Kędzierzyn-Koźle', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '0674269325', 'ACTIVE', '1973-04-15', NULL, 'PATIENT', NULL, true),
('Ida', 'Stoltman', '35042622776', NULL, 'ida.stoltman98@documed.pl', 'ulica Urocza 957, Krotoszyn', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '1201022396', 'DEACTIVATED', '1955-06-23', NULL, 'PATIENT', 1, true),
('Dominik', 'Perlik', '98031057827', NULL, 'dominik.perlik871@email.pl', 'al. Rolna 74/39, Kluczbork', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '5327402803', 'ACTIVE', '1965-10-01', NULL, 'PATIENT', 1, true),
('Natasza', 'Jarczyk', '16061701365', NULL, 'natasza.jarczyk188@pacjent.pl', 'aleja Dąbrowskiego 17, Zabrze', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '5109403831', 'ACTIVE', '1944-12-03', NULL, 'PATIENT', NULL, true),
('Leon', 'Kawala', '63062419467', NULL, 'leon.kawala232@email.com', 'ul. Agrestowa 640, Zgorzelec', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '1239498243', 'ACTIVE', '1963-07-30', NULL, 'PATIENT', NULL, true),
('Monika', 'Wojtalik', '90031086478', NULL, 'monika.wojtalik764@email.com', 'plac Jana Pawła II 58/37, Świdnica', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '1685592998', 'ACTIVE', '1991-11-23', NULL, 'PATIENT', 1, true),
('Marcin', 'Burkiewicz', '16022416039', NULL, 'marcin.burkiewicz804@pacjent.pl', 'pl. Jana 871, Skarżysko-Kamienna', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '0009116299', 'ACTIVE', '1961-06-04', NULL, 'PATIENT', 2, true),
('Elżbieta', 'Tytko', '11242053767', NULL, 'elżbieta.tytko359@email.com', 'aleja Kolejowa 816, Chorzów', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '5576279620', 'ACTIVE', '1981-12-28', NULL, 'PATIENT', 2, true),
('Franciszek', 'Szczypek', '09211173321', NULL, 'franciszek.szczypek408@email.pl', 'ul. Skłodowskiej-Curie 63, Września', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '1930084089', 'ACTIVE', '1954-07-24', NULL, 'PATIENT', NULL, true),
('Kamila', 'Ziegert', '79092087073', NULL, 'kamila.ziegert637@pacjent.pl', 'plac Ludowa 979, Piekary Śląskie', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '8915752351', 'ACTIVE', '1996-11-19', NULL, 'PATIENT', 2, false),
('Maciej', 'Wąsek', '10022134795', NULL, 'maciej.wąsek44@email.com', 'aleja Zielna 65/05, Ruda Śląska', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '9537881441', 'ACTIVE', '1973-07-21', NULL, 'PATIENT', NULL, true),
('Marika', 'Giża', '05240152528', NULL, 'marika.giża544@documed.pl', 'pl. Torowa 90/26, Chojnice', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '0243110023', 'ACTIVE', '1978-03-07', NULL, 'PATIENT', NULL, true),
('Miłosz', 'Hutyra', '12091899478', NULL, 'miłosz.hutyra618@pacjent.pl', 'pl. Zaułek 872, Rzeszów', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '6113656953', 'ACTIVE', '1954-05-22', NULL, 'PATIENT', 2, true),
('Kalina', 'Staniaszek', '79040592684', NULL, 'kalina.staniaszek259@email.com', 'ulica Wiosenna 38/30, Pszczyna', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '2111235474', 'ACTIVE', '1953-07-05', NULL, 'PATIENT', 3, true),
('Adam', 'Zięcik', '68081416660', NULL, 'adam.zięcik213@pacjent.pl', 'ulica Stycznia 77, Wyszków', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '7702081603', 'ACTIVE', '1963-03-31', NULL, 'PATIENT', NULL, true),
('Lidia', 'Solis', '70121739172', NULL, 'lidia.solis843@email.com', 'ulica Żeglarska 62, Stalowa Wola', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '6494448687', 'ACTIVE', '1954-10-07', NULL, 'PATIENT', NULL, true),
('Szymon', 'Kalisiak', '54012892908', NULL, 'szymon.kalisiak424@email.com', 'al. Pocztowa 41/90, Toruń', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '2054920004', 'ACTIVE', '1991-10-22', NULL, 'PATIENT', 2, true),
('Elżbieta', 'Wałdoch', '96123131736', 'VT29964', 'elżbieta.wałdoch18@pacjent.pl', 'pl. Gdańska 51, Tarnowskie Góry', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '8119070554', 'ACTIVE', '1979-10-10', NULL, 'PATIENT', 1, true),
('Franciszek', 'Ankiewicz', '78080648597', NULL, 'franciszek.ankiewicz108@email.pl', 'al. Stycznia 26/94, Siemianowice Śląskie', '$2a$10$2bv1RJMphiI3vSubTl9Q3OJg7ukLpsLU6V6j5/ueyG2LnVRUHS4MS', '7022318841', 'ACTIVE', '1997-10-30', NULL, 'PATIENT', 1, true);

---
-- Tabela: Doctor_Specialization
-- Przypisanie specjalizacji każdemu lekarzowi
INSERT INTO Doctor_Specialization (doctor_id, specialization_id) VALUES
(1, 22),
(1, 1),
(2, 2),
(3, 27),
(3, 3),
(4, 6),
(5, 7),
(5, 4),
(6, 8),
(7, 10),
(8, 11),
(9, 12),
(10, 13),
(11, 14),
(11, 19),
(12, 16),
(13, 17),
(13, 40),
(14, 20),
(15, 21),
(15, 15),
(16, 18),
(16, 23),
(17, 25),
(17, 26),
(18, 27),
(19, 28),
(20, 29),
(21, 30),
(22, 31),
(22, 5),
(22, 24),
(23, 32),
(23, 33),
(24, 34),
(25, 35),
(26, 36),
(27, 37),
(28, 38),
(28, 9),
(29, 39),
(30, 41);

-- Przypisanie nieobsadzonych specjalizacji lekarzom
INSERT INTO Doctor_Specialization (doctor_id, specialization_id)
SELECT (SELECT id FROM "User" WHERE role = 'DOCTOR' ORDER BY random() LIMIT 1),
       s.id
  FROM Specialization s
 WHERE NOT EXISTS (
     SELECT 1 FROM Doctor_Specialization ds
      WHERE ds.specialization_id = s.id
   );


---
-- Tabela: Worktime (Poniedziałek=1, Wtorek=2, ..., Niedziela=7)
DO $$
DECLARE
    doctor RECORD;
    day_num INT;
    start_hour INT;
    end_hour INT;
    start_time TIME;
    end_time TIME;
    random_facility_id INT;
BEGIN
    FOR doctor IN SELECT id FROM "User" WHERE role = 'DOCTOR' and account_status = 'ACTIVE' LOOP
        FOR day_num IN 1..7 LOOP

            IF day_num IN (6, 7) THEN
                start_time := TIME '00:00';
                end_time := TIME '00:00';
            ELSE
                LOOP
                    start_hour := FLOOR(RANDOM() * 9 + 7);  -- godziny od 7:00 do 15:00
                    end_hour := FLOOR(RANDOM() * 11 + 10);  -- godziny od 10:00 do 20:00
                    EXIT WHEN end_hour > start_hour;
                END LOOP;

                start_time := make_time(start_hour, 0, 0);
                end_time := make_time(end_hour, 0, 0);
            END IF;

            random_facility_id := FLOOR(RANDOM() * 3 + 1);

            INSERT INTO Worktime (user_id, day_of_week, start_time, end_time, facility_id)
            VALUES (doctor.id, day_num, start_time, end_time, random_facility_id);
        END LOOP;
    END LOOP;
END;
$$;

---
-- Tabela: Free_days
INSERT INTO Free_days (user_id, start_date, end_date) VALUES
(1, CURRENT_DATE + INTERVAL '3 days', CURRENT_DATE + INTERVAL '14 days'), -- Urlop dr. Kowalskiego
(2, CURRENT_DATE + INTERVAL '5 days', CURRENT_DATE + INTERVAL '7 days'), -- Dzień wolny dr. Nowak
(22, CURRENT_DATE + INTERVAL '7 days', CURRENT_DATE + INTERVAL '7 days');


--Tworzenie time_slot
DO $$
DECLARE
    slot_date DATE := CURRENT_DATE - INTERVAL '7 days';
    end_date DATE := CURRENT_DATE + INTERVAL '21 days';
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

--Zajmij sloty dla dni urlopowych
UPDATE Time_slot ts
SET is_busy = TRUE
FROM Free_days fd
WHERE ts.doctor_id = fd.user_id
AND ts.date BETWEEN fd.start_date AND fd.end_date;

---
-- Tabela: Visit
-- Statusy: PLANNED, IN_PROGRESS, CANCELLED, CLOSED

INSERT INTO Visit (status, interview, diagnosis, recommendations, total_cost, facility_id, service_id, patient_information, patient_id, doctor_id, date, start_time, end_time) VALUES
('CLOSED', 'Pacjent zgłasza duszność przy wysiłku, która nasila się podczas wchodzenia po schodach. Odczuwa również zmęczenie i lekkie zawroty głowy.', 'Zdiagnozowano łagodne nadciśnienie.', 'kontrola ciśnienia, ograniczenie soli.', 250.00, 1, 2, 'Duszność przy wysiłku', 42, 16, CURRENT_DATE - 10, '10:00', '10:30'),
('CLOSED', 'Pacjentka skarży się na uporczywy świąd skóry, zwłaszcza w nocy. Zauważyła suchość i zaczerwienienia na ramionach i nogach. Objawy nasilają się po kąpieli.', 'Zdiagnozowano atopowe zapalenie skóry.', 'krem sterydowy 2x dziennie.', 220.00, 2, 4, 'Świąd skóry', 43, 8, CURRENT_DATE - 8, '12:00', '12:30'),
('CLOSED', 'Pacjent odczuwał nieregularne kołatanie serca, szczególnie w sytuacjach stresowych. Zgłasza również uczucie lęku i niepokoju. Nie zaobserwował omdleń.', 'Niewielkie zaburzenia rytmu serca.', 'EKG, unikanie kofeiny.', 250.00, 1, 2, 'Kołatanie serca', 44, 16, CURRENT_DATE - 12, '09:00', '09:30'),
('CLOSED', 'Dziecko ma kaszel od 2 dni, głównie suchy, nasilający się w nocy. Zgłasza również katar i lekki ból gardła. Bez gorączki.', 'Infekcja górnych dróg oddechowych.', 'syrop wykrztuśny, odpoczynek.', 190.00, 3, 12, 'Kaszel u dziecka', 45, 22, CURRENT_DATE - 5, '11:00', '11:30'),
('CLOSED', 'Pacjentka zgłasza nieregularne cykle miesiączkowe, o różnej długości i nasileniu krwawienia. Odczuwa również bóle podbrzusza w trakcie cyklu.', 'Nie stwierdzono nieprawidłowości w badaniu.', 'obserwacja przez 3 miesiące.', 200.00, 2, 9, 'Nieregularne miesiączki', 46, 12, CURRENT_DATE - 6, '14:00', '14:30'),
('CLOSED', 'Pacjent zgłasza ból w lędźwiach, który promieniuje do lewej nogi. Ból nasila się przy schylaniu i podnoszeniu ciężkich przedmiotów. Odczuwa mrowienie w stopie.', 'Podejrzenie dyskopatii.', 'rezonans kręgosłupa.', 230.00, 3, 7, 'Ból pleców', 47, 7, CURRENT_DATE - 11, '08:30', '09:00'),
('CLOSED', 'Pacjent z bólem głowy o charakterze pulsującym, zlokalizowanym po jednej stronie głowy. Zgłasza również światłowstręt i nudności. Ataki pojawiają się 2-3 razy w miesiącu.', 'Podejrzenie migreny.', 'dzienniczek bólu, leki PRN.', 240.00, 1, 6, 'Ból głowy', 48, 17, CURRENT_DATE - 14, '10:30', '11:00'),
('CLOSED', 'Pacjent prosi o badania okresowe wymagane przez pracodawcę. Nie zgłasza żadnych dolegliwości. Wcześniejsze badania były w normie.', 'Wyniki w normie.', 'kolejne badania za rok.', 200.00, 2, 21, 'Badania okresowe', 49, 15, CURRENT_DATE - 20, '13:00', '13:30'),
('CLOSED', 'Problemy z koncentracją i snem.', 'Rozpoznano łagodną depresję.', 'konsultacja psychiatryczna, terapia.', 250.00, 2, 13, 'Bezsenność i brak koncentracji', 50, 23, CURRENT_DATE - 9, '15:00', '15:30'),
('CLOSED', 'Badania kontrolne po przebytej grypie.', 'Wszystko w normie.', 'odpoczynek, nawadnianie.', 250.00, 2, 2, 'Kontrola po chorobie', 51, 16, CURRENT_DATE - 7, '09:30', '09:45'),
('CLOSED', 'Pacjent zgłasza ból w klatce piersiowej o charakterze pieczenia, nasilający się po posiłkach. Czasami odczuwa również zgagę i kwaśny posmak w ustach.', 'Podejrzenie refluksu.', 'kontrola gastrologiczna.', 180.00, 2, 11, 'Ból klatki piersiowej', 52, 11, CURRENT_DATE - 4, '13:30', '14:00'),
('CLOSED', 'Dziecko ma biegunkę od 3 dni, luźne stolce 4-5 razy dziennie. Bez gorączki i wymiotów. Apetyt osłabiony. Pije wodę chętnie.', 'Łagodna infekcja jelitowa.', 'probiotyki, nawodnienie.', 190.00, 3, 12, 'Biegunka u dziecka', 53, 22, CURRENT_DATE - 3, '08:00', '08:30'),
('CLOSED', 'Pacjentka z uciążliwym kaszlem, który utrzymuje się od kilku tygodni. Kaszel jest suchy, nasila się po kontakcie z kurzem i pyłkami. Czasami duszności.', 'Podejrzenie alergii.', 'testy alergiczne.', 200.00, 1, 1, 'Kaszel alergiczny', 54, 1, CURRENT_DATE - 13, '16:00', '16:30'),
('CLOSED', 'Problemy z oddawaniem moczu, w tym częstomocz nocny i osłabiony strumień moczu. Odczuwa parcie na pęcherz.', 'Zdiagnozowano łagodny przerost prostaty.', 'leczenie farmakologiczne.', 200.00, 1, 18, 'Problemy urologiczne', 55, 30, CURRENT_DATE - 15, '09:00', '09:30'),
('CLOSED', 'Badanie kontrolne EKG zgodnie z zaleceniami kardiologa. Nie zgłasza żadnych dolegliwości ze strony układu krążenia.', 'EKG prawidłowe.', 'kontrola za rok.', 250.00, 2, 2, 'Kontrola EKG', 56, 16, CURRENT_DATE - 5, '11:30', '12:00'),
('CLOSED', 'Badanie morfologii krwi, w celu ogólnej kontroli stanu zdrowia. Odczuwa lekkie zmęczenie, ale nie zgłasza innych dolegliwości.', 'Wyniki morfologii w normie.', 'dieta bogata w żelazo.', 250.00, 1, 2, 'Kontrola morfologii', 57, 16, CURRENT_DATE - 8, '08:00', '08:15'),
('CLOSED', 'Zawroty głowy, które pojawiają się nagle i trwają krótko. Dodatkowo odczuwa drętwienie kończyn, zwłaszcza dłoni i stóp.', 'Brak zmian neurologicznych.', 'obserwacja, RTG kręgosłupa szyjnego.', 240.00, 3, 6, 'Zawroty głowy', 58, 17, CURRENT_DATE - 2, '14:30', '15:00'),
('CLOSED', 'Nadwaga i prośba o konsultację w sprawie planu żywieniowego. Jest świadomy potrzeby zmiany trybu życia i zwiększenia aktywności fizycznej.', 'Wskazana redukcja masy ciała.', 'dietetyk, aktywność fizyczna.', 200.00, 2, 19, 'Nadwaga', 59, 9, CURRENT_DATE - 6, '10:00', '10:30'),
('CLOSED', 'Podejrzenie niedoczynności tarczycy, odczuwa zmęczenie, przyrost masy ciała i suchość skóry. Wyniki badań wskazały na podwyższone TSH.', 'TSH podwyższone.', 'badania hormonalne.', 210.00, 2, 8, 'Podejrzenie niedoczynności', 60, 10, CURRENT_DATE - 11, '09:00', '09:30'),
('CLOSED', 'Silny ból ucha, nasilający się w nocy. Odczuwa również osłabienie słuchu i szumy w uchu. Bez gorączki.', 'Zapalenie ucha środkowego.', 'antybiotyk, kontrola za 7 dni.', 180.00, 3, 11, 'Ból ucha', 61, 11, CURRENT_DATE - 10, '15:00', '15:30'),

-- Zaplanowane wizyty (PLANNED)
('PLANNED', NULL, NULL, NULL, 250.00, 2, 2, 'Badanie EKG', 62, 16, CURRENT_DATE + 1, '10:00', '10:30'),
('PLANNED', NULL, NULL, NULL, 190.00, 3, 12, 'Konsultacja pediatryczna', 63, 22, CURRENT_DATE + 2, '11:00', '11:30'),
('PLANNED', NULL, NULL, NULL, 200.00, 2, 21, 'Badania profilaktyczne', 64, 15, CURRENT_DATE + 3, '09:00', '09:30'),
('PLANNED', NULL, NULL, NULL, 200.00, 1, 1, 'Kontrola alergologiczna', 65, 1, CURRENT_DATE + 4, '13:00', '13:30'),
('PLANNED', NULL, NULL, NULL, 240.00, 1, 6, 'Zawroty głowy', 66, 17, CURRENT_DATE + 5, '10:00', '10:30'),
('PLANNED', NULL, NULL, NULL, 210.00, 1, 8, 'Konsultacja endokrynologiczna', 67, 10, CURRENT_DATE + 6, '09:00', '09:30'),
('PLANNED', NULL, NULL, NULL, 220.00, 2, 4, 'Kontrola dermatologiczna', 68, 8, CURRENT_DATE + 7, '11:00', '11:30'),
('PLANNED', NULL, NULL, NULL, 180.00, 3, 11, 'USG kontrolne', 69, 11, CURRENT_DATE + 8, '14:00', '14:30'),
('PLANNED', NULL, NULL, NULL, 250.00, 1, 13, 'Kontrola psychiatryczna', 70, 23, CURRENT_DATE + 9, '13:00', '13:30'),
('PLANNED', NULL, NULL, NULL, 180.00, 2, 11, 'Chrypka', 71, 11, CURRENT_DATE + 10, '15:00', '15:30'),
('PLANNED', NULL, NULL, NULL, 200.00, 1, 21, 'Medycyna pracy', 72, 15, CURRENT_DATE + 11, '09:00', '09:30'),
('PLANNED', NULL, NULL, NULL, 230.00, 1, 16, 'Konsultacja nefrologiczna', 73, 16, CURRENT_DATE + 12, '08:30', '09:00'),
('PLANNED', NULL, NULL, NULL, 230.00, 3, 7, 'Ból kolana', 74, 7, CURRENT_DATE + 13, '10:30', '11:00'),
('PLANNED', NULL, NULL, NULL, 200.00, 2, 19, 'Problemy ze słuchem', 75, 9, CURRENT_DATE + 14, '13:00', '13:30'),
('PLANNED', NULL, NULL, NULL, 250.00, 3, 13, 'Kontrola psychiatryczna', 76, 23, CURRENT_DATE + 15, '09:30', '10:00'),
('PLANNED', NULL, NULL, NULL, 250.00, 1, 2, 'Badanie profilaktyczne', 77, 16, CURRENT_DATE + 10, '08:00', '08:15'),
('PLANNED', NULL, NULL, NULL, 200.00, 2, 18, NULL, 78, 30, CURRENT_DATE + 11, '10:00', '10:30'),
('PLANNED', NULL, NULL, NULL, 180.00, 3, 11, NULL, 79, 11, CURRENT_DATE + 12, '11:00', '11:30'),
('PLANNED', NULL, NULL, NULL, 200.00, 1, 1, NULL, 80, 1, CURRENT_DATE + 13, '12:00', '12:30'),
('PLANNED', NULL, NULL, NULL, 210.00, 2, 8, NULL, 81, 10, CURRENT_DATE + 14, '09:00', '09:30'),
('PLANNED', NULL, NULL, NULL, 230.00, 3, 7, NULL, 82, 7, CURRENT_DATE + 15, '13:00', '13:15'),
('PLANNED', NULL, NULL, NULL, 230.00, 1, 7, NULL, 83, 7, CURRENT_DATE + 16, '10:30', '11:00'),
('PLANNED', NULL, NULL, NULL, 250.00, 2, 2, NULL, 84, 16, CURRENT_DATE + 17, '14:00', '14:30'),
('PLANNED', NULL, NULL, NULL, 240.00, 3, 6, NULL, 85, 17, CURRENT_DATE + 18, '09:00', '09:30'),
('PLANNED', NULL, NULL, NULL, 250.00, 1, 13, NULL, 86, 23, CURRENT_DATE + 19, '10:00', '10:30'),
('PLANNED', NULL, NULL, NULL, 220.00, 2, 4, NULL, 87, 8, CURRENT_DATE + 20, '11:30', '12:00'),
('PLANNED', NULL, NULL, NULL, 250.00, 3, 2, NULL, 88, 16, CURRENT_DATE + 21, '12:30', '13:00'),
('PLANNED', NULL, NULL, NULL, 200.00, 1, 20, NULL, 89, 3, CURRENT_DATE + 22, '09:30', '09:45'),
('PLANNED', NULL, NULL, NULL, 200.00, 2, 9, NULL, 90, 12, CURRENT_DATE + 23, '10:30', '11:00'),
('PLANNED', NULL, NULL, NULL, 190.00, 3, 12, NULL, 91, 22, CURRENT_DATE + 24, '11:30', '12:00'),

('CANCELLED', NULL, NULL, NULL, 200.00, 3, 9, 'Częste zawroty głowy', 92, 12, CURRENT_DATE + 5, '08:00', '08:30'),
('CANCELLED', NULL, NULL, NULL, 250.00, 1, 2, NULL, 93, 16, CURRENT_DATE + 6, '10:00', '10:30'),
('CANCELLED', NULL, NULL, NULL, 220.00, 2, 4, NULL, 94, 8, CURRENT_DATE + 7, '12:00', '12:30'),
('CANCELLED', NULL, NULL, NULL, 210.00, 1, 8, NULL, 95, 10, CURRENT_DATE + 8, '09:00', '09:30'),
('CANCELLED', NULL, NULL, NULL, 250.00, 2, 2, NULL, 96, 16, CURRENT_DATE + 9, '14:30', '15:00');


-- Zajęcie timeslotów
DO $$
DECLARE
    v RECORD;
    slot RECORD;
    slot_count INT;
BEGIN
    FOR v IN
        SELECT * FROM Visit
        WHERE status = 'PLANNED'
    LOOP
        slot_count := EXTRACT(EPOCH FROM (v.end_time - v.start_time)) / 60 / 15;

        FOR slot IN
            SELECT * FROM Time_slot
            WHERE doctor_id = v.doctor_id
              AND date = v.date
              AND is_busy = false
              AND start_time >= v.start_time
              AND end_time <= v.end_time
            ORDER BY start_time
            LIMIT slot_count
        LOOP
            UPDATE Time_slot
            SET is_busy = true,
                visit_id = v.id
            WHERE id = slot.id;
        END LOOP;
    END LOOP;
END $$;


---
-- -- Tabela: Additional_service
INSERT INTO Additional_service (description, date, fulfiller_id, patient_id, service_id) VALUES
('W badaniu USG jamy brzusznej nie stwierdzono istotnych zmian patologicznych. Narządy miąższowe o prawidłowej echogeniczności i wielkości.', CURRENT_DATE - INTERVAL '17 days', 33, 42, 3),
('Ciśnienie wewnątrzgałkowe: OP 16 mmHg, OL 17 mmHg (norma: 10-21 mmHg). Dno oka bez zmian patologicznych. Ostrość widzenia prawidłowa.', CURRENT_DATE - INTERVAL '18 days', 34, 43, 5),
('Na zdjęciu RTG klatki piersiowej widoczne prawidłowo upowietrznione płuca, bez zmian ogniskowych i nacieków. Sylwetka serca prawidłowa.', CURRENT_DATE - INTERVAL '19 days', 35, 44, 10),
('Wynik spirometrii w normie. FVC 95%, FEV1 92%. Nie stwierdzono obturacji ani restrykcji. Test wysiłkowy ujemny.', CURRENT_DATE - INTERVAL '20 days', 36, 45, 14),
('Wyniki morfologii krwi: WBC 7.1 tys./µL, RBC 4.5 mln/µL, HGB 13.8 g/dL, PLT 280 tys./µL. Wszystkie parametry w granicach normy.', CURRENT_DATE - INTERVAL '21 days', 33, 46, 15),
('Rytm zatokowy. HR 68/min. Bez zmian w odcinku ST-T. QRS wąskie. Bez zaburzeń rytmu serca.', CURRENT_DATE - INTERVAL '22 days', 34, 47, 17),
('USG jamy brzusznej: Wątroba, pęcherzyk żółciowy, trzustka i śledziona bez zmian. Nerki prawidłowej wielkości i echogeniczności.', CURRENT_DATE - INTERVAL '23 days', 1, 48, 3),
('Badanie okulistyczne: Brak wad refrakcji. Siatkówka prawidłowa. Pole widzenia bez ubytków. Zastosowano krople do rozszerzenia źrenic.', CURRENT_DATE - INTERVAL '24 days', 35, 49, 5),
('RTG klatki piersiowej: Czyste pola płucne. Brak powiększonych węzłów chłonnych. Przepona wolna.', CURRENT_DATE - INTERVAL '25 days', 36, 50, 10),
('Spirometria: FVC 89% normy, FEV1 85% normy. Wskazana kontrola za pół roku. Pacjentka paląca, zalecono rzucenie palenia.', CURRENT_DATE - INTERVAL '26 days', 33, 51, 14),
('Morfologia krwi: Leukocyty 5.2 tys./µL, Neutrofile 60%, Limfocyty 30%. Bez odchyleń od normy.', CURRENT_DATE - INTERVAL '27 days', 2, 52, 15),
('Badanie EKG: Rytm zatokowy 75/min. Odcinek PR i QT w normie. Bez istotnych zmian niedokrwiennych.', CURRENT_DATE - INTERVAL '28 days', 34, 53, 17),
('USG jamy brzusznej: Moczowody nieposzerzone. Pęcherz moczowy o prawidłowej ścianie, bez złogów. Nie stwierdzono wolnego płynu.', CURRENT_DATE - INTERVAL '29 days', 35, 54, 3),
('Ciśnienie wewnątrzgałkowe: OP 18 mmHg, OL 19 mmHg. Korekcja wady wzroku nie jest konieczna.', CURRENT_DATE - INTERVAL '30 days', 36, 55, 5),
('Morfologia krwi: Wszystkie parametry w normie, bez cech anemii czy stanu zapalnego.', CURRENT_DATE - INTERVAL '31 days', 33, 56, 15),
('Szczegółowe badanie USG jamy brzusznej z oceną przepływów. Bez zmian.', CURRENT_DATE - INTERVAL '1 day', 34, 57, 3),
('Kontrolne badanie okulistyczne po leczeniu zapalenia spojówek. Poprawa stanu.', CURRENT_DATE - INTERVAL '2 days', 35, 58, 5),
('RTG klatki piersiowej - ocena po infekcji. Obraz radiologiczny w normie.', CURRENT_DATE - INTERVAL '3 days', 36, 59, 10),
('Test spirometryczny: wynik zbliżony do poprzedniego, bez istotnych zmian.', CURRENT_DATE - INTERVAL '4 days', 33, 60, 14),
('Morfologia kontrolna: brak odchyleń, parametry w zakresie referencyjnym.', CURRENT_DATE - INTERVAL '5 days', 34, 61, 15),
('EKG spoczynkowe: rytm zatokowy, bez cech niedokrwienia.', CURRENT_DATE - INTERVAL '6 days', 35, 62, 17),
('USG jamy brzusznej - ocena wątroby i pęcherzyka żółciowego. Wynik prawidłowy.', CURRENT_DATE - INTERVAL '7 days', 1, 63, 3),
('Okulistyczne badanie dna oka. Bez patologii.', CURRENT_DATE - INTERVAL '8 days', 36, 64, 5),
('RTG klatki piersiowej - diagnostyka kaszlu. Płuca czyste.', CURRENT_DATE - INTERVAL '9 days', 33, 65, 10),
('Spirometria - ocena pojemności życiowej płuc. Wynik dobry.', CURRENT_DATE - INTERVAL '10 days', 34, 66, 14),
('Morfologia krwi z rozmazem: wszystkie frakcje leukocytów w normie.', CURRENT_DATE - INTERVAL '11 days', 35, 67, 15),
('EKG po wysiłku: brak arytmii, prawidłowa reakcja serca na obciążenie.', CURRENT_DATE - INTERVAL '12 days', 36, 68, 17),
('USG nerek i dróg moczowych. Nie stwierdzono kamicy ani zastoju.', CURRENT_DATE - INTERVAL '13 days', 33, 69, 3),
('Pomiar ciśnienia wewnątrzgałkowego. Wskazania do kontroli za 6 miesięcy.', CURRENT_DATE - INTERVAL '14 days', 34, 70, 5),
('Morfologia krwi: Wyniki nie wykazują żadnych niepokojących zmian.', CURRENT_DATE - INTERVAL '15 days', 2, 71, 15),
('Kontrolne USG tarczycy - bez nowych zmian ogniskowych.', CURRENT_DATE - INTERVAL '32 days', 35, 72, 3),
('Badanie okulistyczne u pacjenta z cukrzycą. Stan siatkówki stabilny.', CURRENT_DATE - INTERVAL '33 days', 36, 73, 5),
('RTG kręgosłupa szyjnego - ocena po urazie. Bez cech złamania.', CURRENT_DATE - INTERVAL '34 days', 33, 74, 10),
('Spirometria z próbą rozkurczową. Nieznaczna poprawa parametrów.', CURRENT_DATE - INTERVAL '35 days', 34, 75, 14),
('Morfologia krwi obwodowej. Wszystkie wskaźniki w normie.', CURRENT_DATE - INTERVAL '36 days', 35, 76, 15),
('EKG - badanie kontrolne. Rytm zatokowy, bez arytmii.', CURRENT_DATE - INTERVAL '37 days', 36, 77, 17),
('USG piersi - badanie przesiewowe. Wynik prawidłowy, bez zmian podejrzanych.', CURRENT_DATE - INTERVAL '38 days', 1, 78, 3),
('Pomiar ostrości widzenia. Wymagana korekcja okularowa.', CURRENT_DATE - INTERVAL '39 days', 33, 79, 5),
('RTG stawu kolanowego. Brak zmian zwyrodnieniowych.', CURRENT_DATE - INTERVAL '40 days', 34, 80, 10),
('Morfologia krwi - kontrola po leczeniu anemii. Poziom hemoglobiny wzrósł.', CURRENT_DATE - INTERVAL '41 days', 35, 81, 15),
('Badanie słuchu u dziecka - audiometria tonalna. Wynik prawidłowy dla wieku.', CURRENT_DATE - INTERVAL '42 days', 36, 100, 3),
('Kontrolne badanie stóp u diabetyka. Brak zmian troficznych.', CURRENT_DATE - INTERVAL '43 days', 33, 101, 14),
('Ocena stanu skóry po zabiegu dermatologicznym. Prawidłowe gojenie.', CURRENT_DATE - INTERVAL '44 days', 34, 102, 15),
('EKG po rekonwalescencji. Rytm miarowy, bez nieprawidłowości.', CURRENT_DATE - INTERVAL '45 days', 35, 103, 17),
('USG jamy brzusznej - ocena trzustki. Nie stwierdzono patologii.', CURRENT_DATE - INTERVAL '46 days', 2, 104, 3),
('Badanie dna oka pod kątem jaskry. Ciśnienie w normie.', CURRENT_DATE - INTERVAL '47 days', 36, 105, 5),
('RTG klatki piersiowej w projekcji bocznej. Bez cech płynu w opłucnej.', CURRENT_DATE - INTERVAL '48 days', 33, 106, 10),
('Spirometria - ocena po rehabilitacji pulmonologicznej. Poprawa FEV1.', CURRENT_DATE - INTERVAL '49 days', 34, 107, 14),
('Morfologia krwi - kontrola poziomu płytek krwi. W normie.', CURRENT_DATE - INTERVAL '3 days', 35, 108, 15),
('EKG - diagnostyka bólu w klatce piersiowej. Bez zmian ischemicznych.', CURRENT_DATE - INTERVAL '6 days', 36, 109, 17);


---
-- Tabela: Feedback
INSERT INTO Feedback (rating, text, visit_id) VALUES
(5, 'Wizyta u Pana Doktora Jana Kowalskiego była bardzo udana, pełen profesjonalizm i empatia. Polecam!', 1),
(4, 'Pani Doktor Anna Nowak szczegółowo wyjaśniła plan leczenia, czas oczekiwania był akceptowalny.', 2),
(3, 'Wizyta była szybka i konkretna, jednak brakowało mi bardziej osobistego podejścia.', 4),
(5, 'Bardzo miła i pomocna obsługa, zarówno w rejestracji, jak i podczas wizyty.', 9),
(2, 'Długi czas oczekiwania na wizytę, mimo wcześniejszej rejestracji.', 15),
(5, 'Wszystko przebiegło sprawnie i bezproblemowo. Polecam usługi tej placówki.', 7),
(3, 'Otrzymałam potrzebne informacje, ale wizyta była bardzo krótka.', 8),
(1, 'Bardzo negatywne doświadczenie, lekarz był spóźniony i wydawał się zniecierpliwiony.', 17),
(5, 'Pani Doktor jest świetnym specjalistą, czułam się zaopiekowana.', 19);

---
-- Tabela: Referral
INSERT INTO Referral (visit_id, diagnosis, type, expiration_date, status) VALUES
(1, 'REUMATOLOG: Podejrzenie dny moczanowej', 'TO_SPECIALIST', '2026-10-01', 'ISSUED'),
(2, 'Ostre zapalenie wyrostka robaczkowego', 'TO_HOSPITAL', '2026-09-15', 'ISSUED'),
(3, 'Ciągłe zmęczenie, podejrzenie anemii', 'FOR_DIAGNOSTICS', '2026-11-20', 'ISSUED'),
(4, 'Stan po złamaniu kończyny dolnej', 'FOR_REHABILITATION', '2027-01-05', 'ISSUED'),
(5, 'Przewlekłe bóle stawów, reumatoidalne zapalenie stawów', 'TO_SANATORIUM', '2026-12-10', 'ISSUED'),
(6, 'Zaawansowana choroba Alzheimera', 'FOR_LONG_TERM_CARE', '2027-03-01', 'ISSUED'),
(7, 'Objawy depresji klinicznej', 'FOR_PSYCHIATRIC_CARE', '2026-08-25', 'ISSUED'),
(8, 'DIABETOLOG: Kontrola cukrzycy, niewyrównany poziom glukozy', 'TO_SPECIALIST', '2026-10-28', 'ISSUED'),
(11, 'ORTOPEDA: Przewlekły ból pleców', 'TO_SPECIALIST', '2027-02-14', 'ISSUED'),
(12, 'Zaburzenia rytmu serca', 'FOR_DIAGNOSTICS', '2026-11-01', 'ISSUED'),
(13, 'DERMATOLOG: Podejrzenie nowotworu skóry', 'TO_SPECIALIST', '2026-09-05', 'ISSUED'),
(14, 'Niedrożność jelit', 'TO_HOSPITAL', '2026-08-10', 'ISSUED'),
(15, 'Uszkodzenie stawu kolanowego', 'FOR_REHABILITATION', '2027-04-20', 'ISSUED'),
(16, 'KARDIOLOG: Wysokie ciśnienie krwi', 'TO_SPECIALIST', '2026-12-01', 'ISSUED'),
(17, 'Problemy z równowagą', 'FOR_DIAGNOSTICS', '2027-01-15', 'ISSUED'),
(18, 'Stan po udarze mózgu', 'FOR_REHABILITATION', '2027-05-01', 'ISSUED'),
(19, 'LARYNGOLOG: Zapalenie zatok przynosowych', 'TO_SPECIALIST', '2026-09-30', 'ISSUED'),
(20, 'Kryzys psychiczny', 'FOR_PSYCHIATRIC_CARE', '2026-11-25', 'ISSUED');

---
-- Tabela: Prescription
INSERT INTO Prescription (visit_id, date, status, expiration_date) VALUES
(1, (SELECT date FROM Visit WHERE id = 1), 'ISSUED', CURRENT_DATE + INTERVAL '40 days'),
(2, (SELECT date FROM Visit WHERE id = 2), 'ISSUED', CURRENT_DATE + INTERVAL '40 days'),
(3, (SELECT date FROM Visit WHERE id = 3), 'ISSUED', CURRENT_DATE + INTERVAL '40 days'),
(4, (SELECT date FROM Visit WHERE id = 4), 'ISSUED', CURRENT_DATE + INTERVAL '40 days'),
(5, (SELECT date FROM Visit WHERE id = 5), 'ISSUED', CURRENT_DATE + INTERVAL '40 days'),
(6, (SELECT date FROM Visit WHERE id = 6), 'ISSUED', CURRENT_DATE + INTERVAL '40 days'),
(7, (SELECT date FROM Visit WHERE id = 7), 'ISSUED', CURRENT_DATE + INTERVAL '40 days'),
(8, (SELECT date FROM Visit WHERE id = 8), 'ISSUED', CURRENT_DATE + INTERVAL '40 days'),
(9, (SELECT date FROM Visit WHERE id = 9), 'ISSUED', CURRENT_DATE + INTERVAL '40 days'),
(10, (SELECT date FROM Visit WHERE id = 10), 'ISSUED', CURRENT_DATE + INTERVAL '40 days'),
(11, (SELECT date FROM Visit WHERE id = 11), 'ISSUED', CURRENT_DATE + INTERVAL '40 days'),
(12, (SELECT date FROM Visit WHERE id = 12), 'ISSUED', CURRENT_DATE + INTERVAL '40 days'),
(13, (SELECT date FROM Visit WHERE id = 13), 'ISSUED', CURRENT_DATE + INTERVAL '40 days'),
(14, (SELECT date FROM Visit WHERE id = 14), 'ISSUED', CURRENT_DATE + INTERVAL '40 days'),
(15, (SELECT date FROM Visit WHERE id = 15), 'ISSUED', CURRENT_DATE + INTERVAL '40 days');

INSERT INTO medicine_prescription (medicine_id, prescription_id, amount) VALUES
(100000801, 1, 3),
(100000037, 1, 1),
(100000095, 2, 2),
(100000250, 3, 1),
(100000296, 3, 2),
(100000356, 4, 3),
(100000391, 5, 1),
(100000505, 5, 2),
(100000600, 6, 1),
(100000623, 7, 2),
(100000729, 8, 3),
(100000801, 9, 1),
(100000818, 9, 2),
(100000936, 10, 1),
(100000988, 10, 3),
(100001019, 11, 2),
(100001120, 12, 1),
(100001350, 12, 2),
(100001485, 13, 3),
(100001580, 14, 1),
(100001700, 14, 2),
(100001864, 14, 1),
(100001901, 15, 2);