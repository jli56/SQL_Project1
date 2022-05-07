set feedback off
set define off

prompt Loading T_PARKINGLOT...
insert into T_PARKINGLOT (lotname, address, space_start, space_end, vspace_start, vspace_end)
values ('Freedom Lot', '2105 Daniel Allen St, NC 27505', 1, 150, 0, 0);
insert into T_PARKINGLOT (lotname, address, space_start, space_end, vspace_start, vspace_end)
values ('Premiere Lot', '2108 McKent St, NC 27507', 1, 200, 199, 200);
insert into T_PARKINGLOT (lotname, address, space_start, space_end, vspace_start, vspace_end)
values ('Justice Lot', '2704 Ben Clark St, NC 26701', 1, 175, 150, 175);
commit;
prompt 3 records loaded

prompt Loading T_CITATION...
insert into T_CITATION (cnum, car_license, model, color, lotname, citation_date, payment_due, category, fee, status)
values ('10001', 'TRK1093', 'Rio', 'Blue', 'Justice Lot', to_date('14-08-2020 14:40:00', 'dd-mm-yyyy hh24:mi:ss'), to_date('13-09-2020 23:59:59', 'dd-mm-yyyy hh24:mi:ss'), 'Expired Permit', 25, 'Paid');
insert into T_CITATION (cnum, car_license, model, color, lotname, citation_date, payment_due, category, fee, status)
values ('10002', 'UGY9123', 'Maxima', 'Black', 'Justice Lot', to_date('17-08-2020 12:55:00', 'dd-mm-yyyy hh24:mi:ss'), to_date('16-09-2020 23:59:59', 'dd-mm-yyyy hh24:mi:ss'), 'Expired Permit', 25, 'Unpaid');
insert into T_CITATION (cnum, car_license, model, color, lotname, citation_date, payment_due, category, fee, status)
values ('10003', 'AKL1732', 'Model X', 'Silver', 'Justice Lot', to_date('17-08-2020 13:00:00', 'dd-mm-yyyy hh24:mi:ss'), to_date('16-09-2020 23:59:59', 'dd-mm-yyyy hh24:mi:ss'), 'Expired Permit', 25, 'Unpaid');
insert into T_CITATION (cnum, car_license, model, color, lotname, citation_date, payment_due, category, fee, status)
values ('10004', 'NEV9889', 'Elantra', 'Red', 'Justice Lot', to_date('10-09-2020 15:50:00', 'dd-mm-yyyy hh24:mi:ss'), to_date('09-10-2020 23:59:59', 'dd-mm-yyyy hh24:mi:ss'), 'Invalid Permit', 20, 'Unpaid');
insert into T_CITATION (cnum, car_license, model, color, lotname, citation_date, payment_due, category, fee, status)
values ('10005', 'PTL5642', 'Sentra', 'Black', 'Freedom Lot', to_date('14-09-2020 10:05:00', 'dd-mm-yyyy hh24:mi:ss'), to_date('13-10-2020 23:59:59', 'dd-mm-yyyy hh24:mi:ss'), 'Expired Permit', 40, 'Paid');
insert into T_CITATION (cnum, car_license, model, color, lotname, citation_date, payment_due, category, fee, status)
values ('10006', 'TRK1093', 'Rio', 'Blue', 'Premiere Lot', to_date('21-09-2020 14:00:00', 'dd-mm-yyyy hh24:mi:ss'), to_date('20-10-2020 23:59:59', 'dd-mm-yyyy hh24:mi:ss'), 'Expired Permit', 25, 'Unpaid');
commit;
prompt 6 records loaded

prompt Loading T_PERMIT...
insert into T_PERMIT (permitid, zid, start_date, expire_date, space_type, univid, phone, space_num, lotname, status)
values ('20V0001A', 'V', to_date('12-08-2020 14:00:00', 'dd-mm-yyyy hh24:mi:ss'), to_date('12-08-2020 16:00:00', 'dd-mm-yyyy hh24:mi:ss'), 'Regular', null, null, 200, 'Premiere Lot', 'valid');
insert into T_PERMIT (permitid, zid, start_date, expire_date, space_type, univid, phone, space_num, lotname, status)
values ('20V0012B', 'V', to_date('14-08-2020 11:00:00', 'dd-mm-yyyy hh24:mi:ss'), to_date('14-08-2020 14:00:00', 'dd-mm-yyyy hh24:mi:ss'), 'Regular', null, null, 160, 'Justice Lot', 'valid');
insert into T_PERMIT (permitid, zid, start_date, expire_date, space_type, univid, phone, space_num, lotname, status)
values ('20V0015J', 'V', to_date('17-08-2020 10:10:00', 'dd-mm-yyyy hh24:mi:ss'), to_date('17-08-2020 12:10:00', 'dd-mm-yyyy hh24:mi:ss'), 'Handicapped', null, null, 151, 'Justice Lot', 'valid');
insert into T_PERMIT (permitid, zid, start_date, expire_date, space_type, univid, phone, space_num, lotname, status)
values ('20V0021L', 'V', to_date('17-08-2020 11:45:00', 'dd-mm-yyyy hh24:mi:ss'), to_date('17-08-2020 12:45:00', 'dd-mm-yyyy hh24:mi:ss'), 'Electric', null, null, 173, 'Justice Lot', 'valid');
insert into T_PERMIT (permitid, zid, start_date, expire_date, space_type, univid, phone, space_num, lotname, status)
values ('20V0026P', 'V', to_date('19-08-2020 14:50:00', 'dd-mm-yyyy hh24:mi:ss'), to_date('19-08-2020 16:50:00', 'dd-mm-yyyy hh24:mi:ss'), 'Handicapped', null, null, 153, 'Justice Lot', 'valid');
insert into T_PERMIT (permitid, zid, start_date, expire_date, space_type, univid, phone, space_num, lotname, status)
values ('20V0025B', 'V', to_date('21-08-2020 09:30:00', 'dd-mm-yyyy hh24:mi:ss'), to_date('21-08-2020 13:30:00', 'dd-mm-yyyy hh24:mi:ss'), 'Regular', null, null, 200, 'Premiere Lot', 'valid');
insert into T_PERMIT (permitid, zid, start_date, expire_date, space_type, univid, phone, space_num, lotname, status)
values ('20B0001B', 'B', to_date('10-08-2020 12:00:00', 'dd-mm-yyyy hh24:mi:ss'), to_date('09-08-2021 23:59:00', 'dd-mm-yyyy hh24:mi:ss'), 'Electric', '1007999', null, null, null, 'valid');
insert into T_PERMIT (permitid, zid, start_date, expire_date, space_type, univid, phone, space_num, lotname, status)
values ('20CS001C', 'CS', to_date('15-08-2020 12:00:00', 'dd-mm-yyyy hh24:mi:ss'), to_date('14-12-2020 23:59:00', 'dd-mm-yyyy hh24:mi:ss'), 'Handicapped', '1006003', null, null, null, 'valid');
insert into T_PERMIT (permitid, zid, start_date, expire_date, space_type, univid, phone, space_num, lotname, status)
values ('20D0021D', 'D', to_date('10-07-2020 12:00:00', 'dd-mm-yyyy hh24:mi:ss'), to_date('09-07-2021 23:59:00', 'dd-mm-yyyy hh24:mi:ss'), 'Regular', '1006020', null, null, null, 'valid');
insert into T_PERMIT (permitid, zid, start_date, expire_date, space_type, univid, phone, space_num, lotname, status)
values ('20AS016S', 'AS', to_date('01-09-2020 12:00:00', 'dd-mm-yyyy hh24:mi:ss'), to_date('31-12-2020 23:59:00', 'dd-mm-yyyy hh24:mi:ss'), 'Regular', '1006135', null, null, null, 'valid');
insert into T_PERMIT (permitid, zid, start_date, expire_date, space_type, univid, phone, space_num, lotname, status)
values ('20A0052A', 'A', to_date('29-07-2020 12:00:00', 'dd-mm-yyyy hh24:mi:ss'), to_date('28-07-2021 23:59:00', 'dd-mm-yyyy hh24:mi:ss'), 'Regular', '1006022', null, null, null, 'valid');
commit;
prompt 11 records loaded

prompt Loading T_LICENSE...
insert into T_LICENSE (permitid, car_license)
values ('20A0052A', 'KTP2003');
insert into T_LICENSE (permitid, car_license)
values ('20AS016S', 'NEV9889');
insert into T_LICENSE (permitid, car_license)
values ('20B0001B', 'VTZ87543');
insert into T_LICENSE (permitid, car_license)
values ('20CS001C', 'UGB9020');
insert into T_LICENSE (permitid, car_license)
values ('20D0021D', 'RPU1824');
insert into T_LICENSE (permitid, car_license)
values ('20D0021D', 'TIR3487');
insert into T_LICENSE (permitid, car_license)
values ('20V0001A', 'CDF5731');
insert into T_LICENSE (permitid, car_license)
values ('20V0012B', 'TRK1093');
insert into T_LICENSE (permitid, car_license)
values ('20V0015J', 'UGY9123');
insert into T_LICENSE (permitid, car_license)
values ('20V0021L', 'AKL1732');
insert into T_LICENSE (permitid, car_license)
values ('20V0025B', 'TRK1093');
insert into T_LICENSE (permitid, car_license)
values ('20V0026P', 'UWA1118');
commit;
prompt 12 records loaded

prompt Loading T_NOTIFICATION...
prompt Table is empty
	
prompt Loading T_PARKINGRECORD...
prompt Table is empty
	
prompt Loading T_SPACETYPE...
insert into T_SPACETYPE (lotname, space_type, start_num, end_num)
values ('Freedom Lot', 'Regular', 1, 150);
insert into T_SPACETYPE (lotname, space_type, start_num, end_num)
values ('Premiere Lot', 'Regular', 1, 200);
insert into T_SPACETYPE (lotname, space_type, start_num, end_num)
values ('Justice Lot', 'Regular', 1, 150);
insert into T_SPACETYPE (lotname, space_type, start_num, end_num)
values ('Justice Lot', 'Regular', 156, 171);
insert into T_SPACETYPE (lotname, space_type, start_num, end_num)
values ('Justice Lot', 'Handicapped', 151, 155);
insert into T_SPACETYPE (lotname, space_type, start_num, end_num)
values ('Justice Lot', 'Handicapped', 172, 175);
commit;
prompt 6 records loaded

prompt Loading T_UNIVUSER...
insert into T_UNIVUSER (univid, status, passwd)
values ('admin', 'A', 'password');
insert into T_UNIVUSER (univid, status, passwd)
values ('1007999', 'E', 'password');
insert into T_UNIVUSER (univid, status, passwd)
values ('1006003', 'E', 'password');
insert into T_UNIVUSER (univid, status, passwd)
values ('1006020', 'E', 'password');
insert into T_UNIVUSER (univid, status, passwd)
values ('1006135', 'S', 'password');
insert into T_UNIVUSER (univid, status, passwd)
values ('1006022', 'S', 'password');
commit;
prompt 6 records loaded

prompt Loading T_VEHICLE...
insert into T_VEHICLE (car_license, manufacturer, model, year, color)
values ('CDF5731', 'Toyota', 'Camry', 2018, 'Red');
insert into T_VEHICLE (car_license, manufacturer, model, year, color)
values ('AKL1732', 'Tesla', 'Model X', 2019, 'Silver');
insert into T_VEHICLE (car_license, manufacturer, model, year, color)
values ('UGY9123', 'Nissan', 'Maxima', 2015, 'Black');
insert into T_VEHICLE (car_license, manufacturer, model, year, color)
values ('TRK1093', 'Kia', 'Rio', 2017, 'Blue');
insert into T_VEHICLE (car_license, manufacturer, model, year, color)
values ('UWA1118', 'Audi', 'Q3', 2016, 'White');
insert into T_VEHICLE (car_license, manufacturer, model, year, color)
values ('UGB9020', 'Chevrolet', 'Cruze', 2015, 'Black');
insert into T_VEHICLE (car_license, manufacturer, model, year, color)
values ('VTZ87543', 'Nissan', 'LEAF', 2018, 'Black');
insert into T_VEHICLE (car_license, manufacturer, model, year, color)
values ('TIR3487', 'BMW', 'X5', 2017, 'White');
insert into T_VEHICLE (car_license, manufacturer, model, year, color)
values ('RPU1824', 'Honda', 'Odyssey', 2016, 'Blue');
insert into T_VEHICLE (car_license, manufacturer, model, year, color)
values ('NEV9889', 'Hyundai', 'Elantra', 2011, 'Red');
insert into T_VEHICLE (car_license, manufacturer, model, year, color)
values ('KTP2003', 'Acura', 'RDX', 2009, 'Black');
commit;
prompt 11 records loaded

prompt Loading T_ZONE...
insert into T_ZONE (lotname, zid)
values ('Freedom Lot', 'A');
insert into T_ZONE (lotname, zid)
values ('Freedom Lot', 'B');
insert into T_ZONE (lotname, zid)
values ('Freedom Lot', 'C');
insert into T_ZONE (lotname, zid)
values ('Freedom Lot', 'D');
insert into T_ZONE (lotname, zid)
values ('Justice Lot', 'AS');
insert into T_ZONE (lotname, zid)
values ('Justice Lot', 'BS');
insert into T_ZONE (lotname, zid)
values ('Justice Lot', 'CS');
insert into T_ZONE (lotname, zid)
values ('Justice Lot', 'DS');
insert into T_ZONE (lotname, zid)
values ('Justice Lot', 'V');
insert into T_ZONE (lotname, zid)
values ('Premiere Lot', 'A');
insert into T_ZONE (lotname, zid)
values ('Premiere Lot', 'AS');
insert into T_ZONE (lotname, zid)
values ('Premiere Lot', 'B');
insert into T_ZONE (lotname, zid)
values ('Premiere Lot', 'BS');
insert into T_ZONE (lotname, zid)
values ('Premiere Lot', 'C');
insert into T_ZONE (lotname, zid)
values ('Premiere Lot', 'CS');
insert into T_ZONE (lotname, zid)
values ('Premiere Lot', 'D');
insert into T_ZONE (lotname, zid)
values ('Premiere Lot', 'DS');
insert into T_ZONE (lotname, zid)
values ('Premiere Lot', 'V');
commit;
prompt 18 records loaded

prompt Loading T_UNIVUSER...
insert into T_UNIVUSER (univid, status, passwd)
values ('admin', 'A', 'password');
insert into T_UNIVUSER (univid, status, passwd)
values ('1007999', 'E', 'password');
insert into T_UNIVUSER (univid, status, passwd)
values ('1006003', 'E', 'password');
insert into T_UNIVUSER (univid, status, passwd)
values ('1006020', 'E', 'password');
insert into T_UNIVUSER (univid, status, passwd)
values ('1006135', 'S', 'password');
insert into T_UNIVUSER (univid, status, passwd)
values ('1006022', 'S', 'password');
commit;
prompt 6 records loaded

set feedback on
set define on
prompt Done
