CREATE TYPE ASTARTES_CATEGORY AS ENUM ('AGGRESSOR', 'LIBRARIAN', 'HELIX', 'APOTHECARY');


CREATE TABLE COORDINATES
(
    ID                 SERIAL               PRIMARY KEY,
    X                  BIGINT               NOT NULL CHECK (X > -593),
    Y                  INTEGER              NOT NULL CHECK (Y > -148)
);

CREATE TABLE CHAPTER
(
    ID                 SERIAL               PRIMARY KEY,
    NAME               VARCHAR(256)         NOT NULL CHECK (CHAR_LENGTH(NAME) > 0),
    PARENT_LEGION      VARCHAR(256)
);

CREATE TABLE SPACE_MARINE
(
    ID                 SERIAL               PRIMARY KEY,
    NAME               VARCHAR(256)         NOT NULL CHECK (CHAR_LENGTH(NAME) > 0),
    COORDINATES        INTEGER              REFERENCES COORDINATES (ID) ON UPDATE CASCADE ON DELETE CASCADE NOT NULL,
    CREATION_DATE      DATE                 NOT NULL,
    HEALTH             DOUBLE PRECISION     NOT NULL CHECK (HEALTH > 0),
    HEART_COUNT        INTEGER              NOT NULL CHECK (HEART_COUNT >= 3),
    LOYAL              BOOLEAN              ,
    CATEGORY           ASTARTES_CATEGORY    NOT NULL,
    CHAPTER            INTEGER              REFERENCES CHAPTER (ID) ON UPDATE CASCADE ON DELETE CASCADE
);