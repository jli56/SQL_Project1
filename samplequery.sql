







-- 1. Show the list of zones for each lot as tuple pairs (lot, zone).
SELECT * FROM t_zone;

-- 2. Get permit information for a given employee with UnivID: 1006020
SELECT * FROM t_permit WHERE univid = '1006020';

-- 3. Get vehicle information for a particular UnivID: 1006003
SELECT * FROM t_vehicle WHERE car_license = (SELECT l.car_license FROM t_license l, t_permit p WHERE l.permitid = p.permitid AND p.univid = '1006003');

-- 4. Find an available space# for Visitor for an electric vehicle in a specific parking lot: Justice Lot
SET SERVEROUTPUT ON
DECLARE
    V_ANUM NUMBER(5) := 0;
    V_END NUMBER(5) := 0;
    V_COUNT NUMBER(5) := 0;
    V_ISAVAILABLE NUMBER(5) := 0;

BEGIN
  SELECT VSPACE_START,VSPACE_END INTO V_ANUM,V_END
         FROM T_PARKINGLOT
         WHERE LOTNAME = 'Justice Lot';
         DBMS_OUTPUT.PUT_LINE ('Searching interval: '||V_ANUM||'----->'||V_END); 

         WHILE V_ANUM < V_END  LOOP
         SELECT VSPACE_START + V_COUNT INTO V_ANUM FROM T_PARKINGLOT WHERE LOTNAME='Justice Lot';
         
         SELECT COUNT(*) INTO V_ISAVAILABLE FROM T_SPACETYPE WHERE LOTNAME='Justice Lot' AND SPACE_TYPE='Electric' AND V_ANUM >= START_NUM AND V_ANUM <= END_NUM
         AND NOT EXISTS(SELECT 1 FROM T_PARKINGRECORD WHERE LOTNAME='Justice Lot' AND SPACE_NUMBER = V_ANUM);        
         V_COUNT := V_COUNT + 1;
         
         IF V_ISAVAILABLE = 1 THEN 
    DBMS_OUTPUT.PUT_LINE (V_ANUM);
  END IF;   
   END LOOP;

END;

-- 5. Find any cars that are currently in violation
SELECT * FROM t_citation WHERE status = 'Unpaid';

-- 6. How many employees have permits for parking zone D.
SELECT COUNT(*) FROM t_permit p, t_univuser u WHERE zid = 'D' AND u.univid = p.univid AND u.status = 'E';

