CREATE TABLE epam.users
(
    id         BIGSERIAL PRIMARY KEY,
    first_name VARCHAR NOT NULL,
    last_name  VARCHAR NOT NULL,
    username   VARCHAR NOT NULL UNIQUE,
    password   VARCHAR NOT NULL,
    is_active  BOOLEAN NOT NULL
);

CREATE TABLE epam.training_type
(
    id                 BIGSERIAL PRIMARY KEY,
    training_type_name VARCHAR NOT NULL
);

CREATE TABLE epam.trainee
(
    id            BIGSERIAL PRIMARY KEY,
    date_of_birth DATE,
    address       VARCHAR,
    user_id       BIGINT NOT NULL REFERENCES epam.users (id)
);

CREATE TABLE epam.trainer
(
    id             BIGSERIAL PRIMARY KEY,
    user_id        BIGINT NOT NULL REFERENCES epam.users (id),
    specialization BIGINT NOT NULL REFERENCES epam.training_type (id)
);

CREATE TABLE epam.training
(
    id            BIGSERIAL PRIMARY KEY,
    trainee_id    BIGINT  NOT NULL REFERENCES epam.trainee (id),
    trainer_id    BIGINT  NOT NULL REFERENCES epam.trainer (id),
    name          VARCHAR NOT NULL,
    training_type BIGINT  NOT NULL REFERENCES epam.training_type (id),
    date          DATE    NOT NULL,
    duration_in_minutes        INTEGER NOT NULL
);

CREATE TABLE epam.trainee_trainer
(
    trainee_id BIGINT NOT NULL REFERENCES epam.trainee (id),
    trainer_id BIGINT NOT NULL REFERENCES epam.trainer (id),
    PRIMARY KEY (trainee_id, trainer_id)
);

INSERT INTO epam.training_type (training_type_name)
VALUES ('CARDIO'),
       ('STRENGTH'),
       ('FLEXIBILITY'),
       ('BALANCE'),
       ('OTHER');