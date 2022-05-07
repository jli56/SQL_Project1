create table t_univuser(
univid varchar(20) NOT NULL PRIMARY KEY,
status varchar(2), /*A-admin,E-employee,S-student*/
passwd varchar(20)
);


create table t_parkinglot(
lotname varchar(50) NOT NULL PRIMARY KEY,
address varchar(100),
space_start integer,
space_end integer,
vspace_start integer, /*NULL if there is no V zone*/
vspace_end integer /*NULL if there is no V zone*/
);

create table t_zone(
lotname varchar(50),
zid varchar(2) NOT NULL, /*A,B,C,D,AS,BS,CS,DS,V*/
primary key(lotname, zid),
CONSTRAINT fk_zonelot FOREIGN KEY (lotname) REFERENCES t_parkinglot (lotname)
);

create table t_spacetype(
lotname varchar(50) ,
space_type varchar(20) DEFAULT 'Regular' NOT NULL,
start_num integer,
end_num integer,
primary key(lotname, start_num),
CONSTRAINT fk_spacetypelot FOREIGN KEY (lotname) REFERENCES t_parkinglot (lotname)
);



create table t_permit(
permitid varchar(8) NOT NULL PRIMARY KEY,
zid varchar(2),/*A,B,C,D,AS,BS,CS,DS,V*/
start_date date,/*including visitor start time*/
expire_date date,/*including visitor expire time*/
space_type varchar(20) DEFAULT 'Regular' NOT NULL,
univid varchar(20),        /*non-visitor*/
phone varchar(20),        /*visitor*/
space_num integer, /*visitor,like a serial number*/
lotname varchar(50) , /*visitor*/
status varchar(20), /*visitor:invalid,valid; non-visitor:used*/
CONSTRAINT fk_permitlot FOREIGN KEY (lotname) REFERENCES t_parkinglot (lotname)
);


create table t_license(
permitid varchar(8) ,
car_license varchar(50),
primary key(permitid, car_license),
CONSTRAINT fk_license_permitid FOREIGN KEY (permitid) REFERENCES t_permit (permitid)
);

create table t_vehicle(
car_license varchar(50) NOT NULL PRIMARY KEY,
manufacturer varchar(20),
model varchar(20),
year integer,
color varchar(20)
);


create table t_parkingrecord(
car_license varchar(50) NOT NULL PRIMARY KEY,
lotname varchar(50) ,
enterdate date,
leavedate date,
space_number integer,
zone varchar(2),
CONSTRAINT fk_parkingrecordlot FOREIGN KEY (lotname) REFERENCES t_parkinglot (lotname)
);


create table t_citation(
cnum varchar(50) NOT NULL PRIMARY KEY,
car_license varchar(50) NOT NULL,
model varchar(20),
color varchar(20),
lotname varchar(50),
citation_date date,
payment_due date,
category varchar(20),
fee number(9,2),
status varchar(10) DEFAULT 'Unpaid' NOT NULL,
CONSTRAINT fk_citation_lot FOREIGN KEY (lotname) REFERENCES t_parkinglot (lotname)
);

create table t_notification(
cnum varchar(50) NOT NULL PRIMARY KEY,
univid varchar(20),/*non-visitor*/
phone varchar(20)/*visitor*/
);

--add sequence
create sequence permit_seq increment by 1 start with 1 maxvalue 99999 nocache;
create sequence citation_seq increment by 1 start with 1 maxvalue 99999 nocache;


/*some useful query for coding
--select sysdate from dual;
--select to_char((sysdate+1/1440),'yyyymmdd-hh24miss') as added,to_char(add_months(sysdate,4),'yyyymmdd-hh24miss') as origin from dual;
*/











