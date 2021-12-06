INSERT INTO CHAPTER (NAME, PARENT_LEGION)
VALUES ('Paul Atreides', 'Atreides'),
       ('Chani Kynes', 'Fremen'),
       ('Lady Jessica', 'Bene Gesserit'),
       ('Leto Atreides', 'Atreides');

INSERT INTO COORDINATES (X, Y)
VALUES (0, 0),
       (100, 100),
       (200, 200),
       (300, 300),
       (400, 400),
       (500, 500),
       (600, 600),
       (700, 700),
       (800, 800),
       (900, 900);

INSERT INTO SPACE_MARINE (NAME, COORDINATES, CREATION_DATE, HEALTH, HEART_COUNT, LOYAL, CATEGORY, CHAPTER)
VALUES ('Heighliner', 5, '2003-01-01', 4, 4, true , 'APOTHECARY', 1),
       ('Arcadia', 1, '2003-01-01', 11, 5, true, 'HELIX', 2),
       ('Black Hawk', 1, '2003-01-01', 11, 5, true, 'LIBRARIAN', 3),
       ('Charlotte', 1, '2021-01-01', 11, 5, true, 'LIBRARIAN', 4),
       ('Shark', 6, '2004-01-01', 3, 3, true, 'AGGRESSOR', 1);
