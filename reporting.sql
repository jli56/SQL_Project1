-- 1. For each lot, generate the total number of citations given in all zones in the lot for a three
-- month period (07/01/2020 - 09/30/2020).
SELECT COUNT(*) AS numCitation , lotname
FROM t_citation
WHERE citation_date >= to_date('2020-07-01','YYYY-MM-DD') AND citation_date <= to_date('2020-09-30','YYYY-MM-DD')
GROUP BY lotname;


--2. For Justice Lot, generate the number of visitor permits in a date range: 08/12/2020 -
-- 08/20/2020, grouped by permit type e.g. regular, electric, handicapped.
SELECT COUNT(*) AS Citation , p.space_type
FROM t_citation c ,t_permit p, t_license l
WHERE c.citation_date >= to_date('2020-08-12','YYYY-MM-DD') AND c.citation_date <= to_date('2020-08-20','YYYY-MM-DD') AND c.lotname = 'Justice Lot' AND c.car_license = l.car_license AND l.permitid = p.permitid AND p.zid = 'V'
GROUP BY p.space_type
;


-- 3. For each visitor’s parking zone, show the total amount of revenue generated (including
-- pending citation fines) for each day in August 2020.

SELECT SUM(fee), to_char(citation_date, 'YYYY-MM-DD')
FROM (select c.citation_date,c.fee,l.permitid from t_citation c left join t_license l on c.car_license=l.car_license where substr(l.permitid,3,1)='V')
WHERE to_char(citation_date, 'YYYY-MM-DD') >= '2020-08-01' AND to_char(citation_date, 'YYYY-MM-DD') < '2020-09-1'
GROUP BY to_char(citation_date, 'YYYY-MM-DD');