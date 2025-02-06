-- drop old tables and constraints
-- foreign keys
ALTER TABLE Abonament_Usluga
    DROP CONSTRAINT Abonament_Usluga_Abonament;

ALTER TABLE Abonament_Usluga
    DROP CONSTRAINT Abonament_Usluga_Usluga;

ALTER TABLE Slot
    DROP CONSTRAINT Dostepnosc_Uzytkownik;

ALTER TABLE Slot
    DROP CONSTRAINT Dostepnosc_Wizyta;

ALTER TABLE Lek_Recepta
    DROP CONSTRAINT Lek_Recepta_Lek;

ALTER TABLE Lek_Recepta
    DROP CONSTRAINT Lek_Recepta_Recepta;

ALTER TABLE Lekarz_Specjalizacja
    DROP CONSTRAINT Lekarz_Specjalizacja_Specjalizacja;

ALTER TABLE Lekarz_Specjalizacja
    DROP CONSTRAINT Lekarz_Specjalizacja_Uzytkownik;

ALTER TABLE Uzytkownik
    DROP CONSTRAINT Pacjent_Abonament;

ALTER TABLE Recepta
    DROP CONSTRAINT Recepta_Wizyta;

ALTER TABLE Specjalizacja_Usluga
    DROP CONSTRAINT Specjalizacja_Usluga_Specjalizacja;

ALTER TABLE Specjalizacja_Usluga
    DROP CONSTRAINT Specjalizacja_Usluga_Usluga;

ALTER TABLE Usluga_Dodatkowa
    DROP CONSTRAINT Usluga_Dodatkowa_Wykonawca;

ALTER TABLE Usluga_Dodatkowa
    DROP CONSTRAINT Usluga_Usluga_Dodatkowa;

ALTER TABLE Feedback
    DROP CONSTRAINT Wizyta_Feedback;

ALTER TABLE Wizyta
    DROP CONSTRAINT Wizyta_Lekarz;

ALTER TABLE Wizyta
    DROP CONSTRAINT Wizyta_Pacjent;

ALTER TABLE Wizyta
    DROP CONSTRAINT Wizyta_Placowka;

ALTER TABLE Powiadomienie
    DROP CONSTRAINT Wizyta_Powiadomienie;

ALTER TABLE Skierowanie
    DROP CONSTRAINT Wizyta_Skierowanie;

ALTER TABLE Wizyta
    DROP CONSTRAINT Wizyta_Usluga;

ALTER TABLE Usluga_Dodatkowa
    DROP CONSTRAINT Wizyta_Usluga_Dodatkowa;

ALTER TABLE Zalacznik
    DROP CONSTRAINT Zalacznik_Usluga_Dodatkowa;

ALTER TABLE Zalacznik
    DROP CONSTRAINT Zalacznik_Wizyta;

-- tables
DROP TABLE Abonament;

DROP TABLE Abonament_Usluga;

DROP TABLE Feedback;

DROP TABLE Lek;

DROP TABLE Lek_Recepta;

DROP TABLE Lekarz_Specjalizacja;

DROP TABLE Placowka;

DROP TABLE Powiadomienie;

DROP TABLE Recepta;

DROP TABLE Skierowanie;

DROP TABLE Slot;

DROP TABLE Specjalizacja;

DROP TABLE Specjalizacja_Usluga;

DROP TABLE Usluga;

DROP TABLE Usluga_Dodatkowa;

DROP TABLE Uzytkownik;

DROP TABLE Wizyta;

DROP TABLE Zalacznik;


-- tables
-- Table: Abonament
CREATE TABLE Abonament (
    id int  NOT NULL,
    nazwa varchar(255)  NOT NULL,
    cena decimal(6,2)  NOT NULL,
    CONSTRAINT Abonament_pk PRIMARY KEY (id)
);

-- Table: Abonament_Usluga
CREATE TABLE Abonament_Usluga (
    Usluga_id int  NOT NULL,
    Abonament_id int  NOT NULL,
    znizka int  NOT NULL,
    CONSTRAINT Abonament_Usluga_pk PRIMARY KEY (Usluga_id,Abonament_id)
);

-- Table: Feedback
CREATE TABLE Feedback (
    id int  NOT NULL,
    ocena int  NOT NULL,
    tresc text  NULL,
    wizyta_id int  NOT NULL,
    CONSTRAINT Feedback_pk PRIMARY KEY (id)
);

-- Table: Lek
CREATE TABLE Lek (
    id int  NOT NULL,
    nazwa varchar(255)  NOT NULL,
    nazwa_powszechna varchar(255)  NOT NULL,
    opakowanie varchar(255)  NOT NULL,
    CONSTRAINT Lek_pk PRIMARY KEY (id)
);

-- Table: Lek_Recepta
CREATE TABLE Lek_Recepta (
    lek_id int  NOT NULL,
    recepta_id int  NOT NULL,
    ilosc int  NOT NULL,
    CONSTRAINT Lek_Recepta_pk PRIMARY KEY (lek_id,recepta_id)
);

-- Table: Lekarz_Specjalizacja
CREATE TABLE Lekarz_Specjalizacja (
    lekarz_id int  NOT NULL,
    specjalizacja_id int  NOT NULL,
    CONSTRAINT Lekarz_Specjalizacja_pk PRIMARY KEY (lekarz_id,specjalizacja_id)
);

-- Table: Placowka
CREATE TABLE Placowka (
    id int  NOT NULL,
    adres varchar(255)  NOT NULL,
    miasto varchar(255)  NOT NULL,
    CONSTRAINT Placowka_pk PRIMARY KEY (id)
);

-- Table: Powiadomienie
CREATE TABLE Powiadomienie (
    id int  NOT NULL,
    wizyta_id int  NOT NULL,
    status varchar(255)  NOT NULL,
    CONSTRAINT Powiadomienie_pk PRIMARY KEY (id)
);

-- Table: Recepta
CREATE TABLE Recepta (
    id int  NOT NULL,
    kod_dostepu int  NOT NULL,
    wizyta_id int  NOT NULL,
    opis varchar(255)  NOT NULL,
    data date  NOT NULL,
    pesel int  NULL,
    nr_paszportu varchar(10)  NULL,
    CONSTRAINT Recepta_pk PRIMARY KEY (id)
);

-- Table: Skierowanie
CREATE TABLE Skierowanie (
    id int  NOT NULL,
    wizyta_id int  NOT NULL,
    rozpoznanie text  NOT NULL,
    typ varchar(255)  NULL,
    data_waznosci date  NOT NULL,
    CONSTRAINT Skierowanie_pk PRIMARY KEY (id)
);

-- Table: Slot
CREATE TABLE Slot (
    id int  NOT NULL,
    uzytkownik_id int  NOT NULL,
    wizyta_id int  NULL,
    poczatek_slotu time  NOT NULL,
    koniec_slotu time  NOT NULL,
    data date  NOT NULL,
    czy_zajety boolean  NOT NULL,
    CONSTRAINT Slot_pk PRIMARY KEY (id)
);

-- Table: Specjalizacja
CREATE TABLE Specjalizacja (
    id int  NOT NULL,
    nazwa varchar(255)  NOT NULL,
    CONSTRAINT Specjalizacja_pk PRIMARY KEY (id)
);

-- Table: Specjalizacja_Usluga
CREATE TABLE Specjalizacja_Usluga (
    Usluga_id int  NOT NULL,
    Specjalizacja_id int  NOT NULL,
    CONSTRAINT Specjalizacja_Usluga_pk PRIMARY KEY (Usluga_id,Specjalizacja_id)
);

-- Table: Usluga
CREATE TABLE Usluga (
    id int  NOT NULL,
    nazwa varchar(255)  NOT NULL,
    cena decimal(6,2)  NOT NULL,
    typ varchar(255)  NOT NULL,
    czas_trwania int  NOT NULL,
    CONSTRAINT Usluga_pk PRIMARY KEY (id)
);

-- Table: Usluga_Dodatkowa
CREATE TABLE Usluga_Dodatkowa (
    id int  NOT NULL,
    opis text  NOT NULL,
    data date  NOT NULL,
    wykonawca_id int  NOT NULL,
    usluga_id int  NOT NULL,
    wizyta_id int  NOT NULL,
    status varchar(255)  NOT NULL,
    CONSTRAINT Usluga_Dodatkowa_pk PRIMARY KEY (id)
);

-- Table: Uzytkownik
CREATE TABLE Uzytkownik (
    id int  NOT NULL,
    imie varchar(255)  NOT NULL,
    nazwisko varchar(255)  NOT NULL,
    pesel int  NULL,
    nr_paszportu varchar(255)  NOT NULL,
    email varchar(255)  NOT NULL,
    password varchar(255)  NOT NULL,
    nr_telefonu varchar(255)  NULL,
    status varchar(255)  NOT NULL,
    data_urodzenia date  NOT NULL,
    pwz varchar(255)  NULL,
    rola varchar(255)  NOT NULL,
    abonament_id int  NULL,
    CONSTRAINT Uzytkownik_pk PRIMARY KEY (id)
);

-- Table: Wizyta
CREATE TABLE Wizyta (
    id int  NOT NULL,
    status varchar(255)  NOT NULL,
    wywiad text  NULL,
    diagnoza text  NULL,
    zalecenia text  NULL,
    data date  NOT NULL,
    godzina_rozpoczecia time  NOT NULL,
    koszt decimal(6,2)  NOT NULL,
    placowka_id int  NOT NULL,
    usluga int  NOT NULL,
    info_od_pacjenta varchar(255)  NULL,
    lekarz_id int  NOT NULL,
    pacjent_id int  NOT NULL,
    CONSTRAINT Wizyta_pk PRIMARY KEY (id)
);

-- Table: Zalacznik
CREATE TABLE Zalacznik (
    id int  NOT NULL,
    wizyta_id int  NOT NULL,
    url varchar(255)  NOT NULL,
    usluga_Dodatkowa_id int  NOT NULL,
    CONSTRAINT Zalacznik_pk PRIMARY KEY (id)
);

-- foreign keys
-- Reference: Abonament_Usluga_Abonament (table: Abonament_Usluga)
ALTER TABLE Abonament_Usluga ADD CONSTRAINT Abonament_Usluga_Abonament
    FOREIGN KEY (Abonament_id)
    REFERENCES Abonament (id)  
    NOT DEFERRABLE 
    INITIALLY IMMEDIATE
;

-- Reference: Abonament_Usluga_Usluga (table: Abonament_Usluga)
ALTER TABLE Abonament_Usluga ADD CONSTRAINT Abonament_Usluga_Usluga
    FOREIGN KEY (Usluga_id)
    REFERENCES Usluga (id)  
    NOT DEFERRABLE 
    INITIALLY IMMEDIATE
;

-- Reference: Dostepnosc_Uzytkownik (table: Slot)
ALTER TABLE Slot ADD CONSTRAINT Dostepnosc_Uzytkownik
    FOREIGN KEY (uzytkownik_id)
    REFERENCES Uzytkownik (id)  
    NOT DEFERRABLE 
    INITIALLY IMMEDIATE
;

-- Reference: Dostepnosc_Wizyta (table: Slot)
ALTER TABLE Slot ADD CONSTRAINT Dostepnosc_Wizyta
    FOREIGN KEY (wizyta_id)
    REFERENCES Wizyta (id)  
    NOT DEFERRABLE 
    INITIALLY IMMEDIATE
;

-- Reference: Lek_Recepta_Lek (table: Lek_Recepta)
ALTER TABLE Lek_Recepta ADD CONSTRAINT Lek_Recepta_Lek
    FOREIGN KEY (lek_id)
    REFERENCES Lek (id)  
    NOT DEFERRABLE 
    INITIALLY IMMEDIATE
;

-- Reference: Lek_Recepta_Recepta (table: Lek_Recepta)
ALTER TABLE Lek_Recepta ADD CONSTRAINT Lek_Recepta_Recepta
    FOREIGN KEY (recepta_id)
    REFERENCES Recepta (id)  
    NOT DEFERRABLE 
    INITIALLY IMMEDIATE
;

-- Reference: Lekarz_Specjalizacja_Specjalizacja (table: Lekarz_Specjalizacja)
ALTER TABLE Lekarz_Specjalizacja ADD CONSTRAINT Lekarz_Specjalizacja_Specjalizacja
    FOREIGN KEY (specjalizacja_id)
    REFERENCES Specjalizacja (id)  
    NOT DEFERRABLE 
    INITIALLY IMMEDIATE
;

-- Reference: Lekarz_Specjalizacja_Uzytkownik (table: Lekarz_Specjalizacja)
ALTER TABLE Lekarz_Specjalizacja ADD CONSTRAINT Lekarz_Specjalizacja_Uzytkownik
    FOREIGN KEY (lekarz_id)
    REFERENCES Uzytkownik (id)  
    NOT DEFERRABLE 
    INITIALLY IMMEDIATE
;

-- Reference: Pacjent_Abonament (table: Uzytkownik)
ALTER TABLE Uzytkownik ADD CONSTRAINT Pacjent_Abonament
    FOREIGN KEY (abonament_id)
    REFERENCES Abonament (id)  
    NOT DEFERRABLE 
    INITIALLY IMMEDIATE
;

-- Reference: Recepta_Wizyta (table: Recepta)
ALTER TABLE Recepta ADD CONSTRAINT Recepta_Wizyta
    FOREIGN KEY (wizyta_id)
    REFERENCES Wizyta (id)  
    NOT DEFERRABLE 
    INITIALLY IMMEDIATE
;

-- Reference: Specjalizacja_Usluga_Specjalizacja (table: Specjalizacja_Usluga)
ALTER TABLE Specjalizacja_Usluga ADD CONSTRAINT Specjalizacja_Usluga_Specjalizacja
    FOREIGN KEY (Specjalizacja_id)
    REFERENCES Specjalizacja (id)  
    NOT DEFERRABLE 
    INITIALLY IMMEDIATE
;

-- Reference: Specjalizacja_Usluga_Usluga (table: Specjalizacja_Usluga)
ALTER TABLE Specjalizacja_Usluga ADD CONSTRAINT Specjalizacja_Usluga_Usluga
    FOREIGN KEY (Usluga_id)
    REFERENCES Usluga (id)  
    NOT DEFERRABLE 
    INITIALLY IMMEDIATE
;

-- Reference: Usluga_Dodatkowa_Wykonawca (table: Usluga_Dodatkowa)
ALTER TABLE Usluga_Dodatkowa ADD CONSTRAINT Usluga_Dodatkowa_Wykonawca
    FOREIGN KEY (wykonawca_id)
    REFERENCES Uzytkownik (id)  
    NOT DEFERRABLE 
    INITIALLY IMMEDIATE
;

-- Reference: Usluga_Usluga_Dodatkowa (table: Usluga_Dodatkowa)
ALTER TABLE Usluga_Dodatkowa ADD CONSTRAINT Usluga_Usluga_Dodatkowa
    FOREIGN KEY (usluga_id)
    REFERENCES Usluga (id)  
    NOT DEFERRABLE 
    INITIALLY IMMEDIATE
;

-- Reference: Wizyta_Feedback (table: Feedback)
ALTER TABLE Feedback ADD CONSTRAINT Wizyta_Feedback
    FOREIGN KEY (wizyta_id)
    REFERENCES Wizyta (id)  
    NOT DEFERRABLE 
    INITIALLY IMMEDIATE
;

-- Reference: Wizyta_Lekarz (table: Wizyta)
ALTER TABLE Wizyta ADD CONSTRAINT Wizyta_Lekarz
    FOREIGN KEY (lekarz_id)
    REFERENCES Uzytkownik (id)  
    NOT DEFERRABLE 
    INITIALLY IMMEDIATE
;

-- Reference: Wizyta_Pacjent (table: Wizyta)
ALTER TABLE Wizyta ADD CONSTRAINT Wizyta_Pacjent
    FOREIGN KEY (pacjent_id)
    REFERENCES Uzytkownik (id)  
    NOT DEFERRABLE 
    INITIALLY IMMEDIATE
;

-- Reference: Wizyta_Placowka (table: Wizyta)
ALTER TABLE Wizyta ADD CONSTRAINT Wizyta_Placowka
    FOREIGN KEY (placowka_id)
    REFERENCES Placowka (id)  
    NOT DEFERRABLE 
    INITIALLY IMMEDIATE
;

-- Reference: Wizyta_Powiadomienie (table: Powiadomienie)
ALTER TABLE Powiadomienie ADD CONSTRAINT Wizyta_Powiadomienie
    FOREIGN KEY (wizyta_id)
    REFERENCES Wizyta (id)  
    NOT DEFERRABLE 
    INITIALLY IMMEDIATE
;

-- Reference: Wizyta_Skierowanie (table: Skierowanie)
ALTER TABLE Skierowanie ADD CONSTRAINT Wizyta_Skierowanie
    FOREIGN KEY (wizyta_id)
    REFERENCES Wizyta (id)  
    NOT DEFERRABLE 
    INITIALLY IMMEDIATE
;

-- Reference: Wizyta_Usluga (table: Wizyta)
ALTER TABLE Wizyta ADD CONSTRAINT Wizyta_Usluga
    FOREIGN KEY (usluga)
    REFERENCES Usluga (id)  
    NOT DEFERRABLE 
    INITIALLY IMMEDIATE
;

-- Reference: Wizyta_Usluga_Dodatkowa (table: Usluga_Dodatkowa)
ALTER TABLE Usluga_Dodatkowa ADD CONSTRAINT Wizyta_Usluga_Dodatkowa
    FOREIGN KEY (wizyta_id)
    REFERENCES Wizyta (id)  
    NOT DEFERRABLE 
    INITIALLY IMMEDIATE
;

-- Reference: Zalacznik_Usluga_Dodatkowa (table: Zalacznik)
ALTER TABLE Zalacznik ADD CONSTRAINT Zalacznik_Usluga_Dodatkowa
    FOREIGN KEY (usluga_Dodatkowa_id)
    REFERENCES Usluga_Dodatkowa (id)  
    NOT DEFERRABLE 
    INITIALLY IMMEDIATE
;

-- Reference: Zalacznik_Wizyta (table: Zalacznik)
ALTER TABLE Zalacznik ADD CONSTRAINT Zalacznik_Wizyta
    FOREIGN KEY (wizyta_id)
    REFERENCES Wizyta (id)  
    NOT DEFERRABLE 
    INITIALLY IMMEDIATE
;

-- End of file.

