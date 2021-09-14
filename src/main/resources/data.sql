-- CC_DB
INSERT INTO test.CC.DELIVERY(id, delivery_number, facility_num, scheduled_on) values (1, 1000101, 'mcc', '2021-08-30');
INSERT INTO test.CC.DELIVERY(id, delivery_number, facility_num, scheduled_on) values (2, 1000201, 'acc', '2021-08-30');
INSERT INTO test.CC.DELIVERY(id, delivery_number, facility_num, scheduled_on) values (3, 1000102, 'mcc', '2021-08-30');

-- SAMS_DB
INSERT INTO test.SAMS.DELIVERY(id, delivery_number, facility_num, scheduled_on) values (1, 1000101, 'sams', '2021-08-30');
INSERT INTO test.SAMS.DELIVERY(id, delivery_number, facility_num, scheduled_on) values (2, 1000201, 'sams', '2021-08-30');
INSERT INTO test.SAMS.DELIVERY(id, delivery_number, facility_num, scheduled_on) values (3, 1000102, 'sams', '2021-08-30');