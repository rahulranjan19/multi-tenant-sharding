CREATE SCHEMA CC;

CREATE TABLE CC.DELIVERY (
  id INT PRIMARY KEY,
  delivery_number INT,
  facility_num VARCHAR(250) NOT NULL,
  scheduled_on DATE NOT NULL
);

CREATE SCHEMA SAMS;

CREATE TABLE SAMS.DELIVERY (
  id INT PRIMARY KEY,
  delivery_number INT,
  facility_num VARCHAR(250) NOT NULL,
  scheduled_on DATE NOT NULL
);