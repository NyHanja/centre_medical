CREATE DATABASE centre_medical;
CREATE USER centre_app WITH PASSWORD'centre_app';
\c centre_medical
CREATE TABLE patient (
    id              SERIAL PRIMARY KEY,
    nom             VARCHAR(100) NOT NULL,
    prenom          VARCHAR(100) NOT NULL,
    date_naissance  DATE         NOT NULL,
    sexe            CHAR(1)      NOT NULL CHECK (sexe IN ('M', 'F')),
    telephone       VARCHAR(20),
    adresse         VARCHAR(255),
    groupe_sanguin  VARCHAR(3)   CHECK (groupe_sanguin IN ('A+','A-','B+','B-','AB+','AB-','O+','O-')),
    allergies       TEXT,
    qrcode          VARCHAR(255),
    date_creation   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE utilisateur (
    id            SERIAL PRIMARY KEY,
    login         VARCHAR(50)  NOT NULL UNIQUE,
    mot_de_passe  VARCHAR(255) NOT NULL,
    role          VARCHAR(10)  NOT NULL CHECK (role IN ('MEDECIN', 'PATIENT')),
    id_patient    INTEGER      REFERENCES patient(id) ON DELETE CASCADE,
    actif         BOOLEAN      NOT NULL DEFAULT TRUE,
    CONSTRAINT chk_patient_lie CHECK (
        (role = 'PATIENT' AND id_patient IS NOT NULL) OR
        (role = 'MEDECIN' AND id_patient IS NULL)
    )
);

CREATE UNIQUE INDEX uq_utilisateur_patient
    ON utilisateur(id_patient) WHERE id_patient IS NOT NULL;

GRANT CONNECT ON DATABASE centre_medical TO centre_app;
GRANT SELECT, INSERT, UPDATE, DELETE ON patient, utilisateur TO centre_app;
GRANT USAGE, SELECT ON SEQUENCE patient_id_seq, utilisateur_id_seq TO centre_app;