select count(*)
from aggregation_history;---1945104

-- truncate table aggregation_history;

-- status DRAFT
INSERT INTO aggregation_history (id, created_by, created_date, last_modified_by, last_modified_date, deleted,
                                 aggregation_id, status, organization_id, login, product_id, quantity, unit, code,
                                 print_code)
select uuid_in(md5(random()::text || clock_timestamp()::text)::cstring),
       a.created_by,
       a.created_date,
       a.last_modified_by,
       a.last_modified_date,
       false,
       a.id,
       'DRAFT',
       a.organization_id,
       a.login,
       a.product_id,
       a.quantity,
       a.unit,
       a.code,
       a.print_code
from aggregation a
where deleted is not true;


-- status CREATED
INSERT INTO aggregation_history (id, created_by, created_date, last_modified_by, last_modified_date, deleted,
                                 aggregation_id, status, organization_id, login, product_id, quantity, unit, code,
                                 print_code)
select uuid_in(md5(random()::text || clock_timestamp()::text)::cstring),
       a.created_by,
       a.aggregation_date,
       a.last_modified_by,
       a.last_modified_date,
       false,
       a.id,
       'CREATED',
       a.organization_id,
       a.login,
       a.product_id,
       a.quantity,
       a.unit,
       a.code,
       a.print_code
from aggregation a
where deleted is not true
  and status  in ('CREATED','UTILIZED','RETURNED','SHIPPED');


-- status ERROR
INSERT INTO aggregation_history (id, created_by, created_date, last_modified_by, last_modified_date, deleted,
                                 aggregation_id, status, organization_id, login, product_id, quantity, unit, code,
                                 print_code)
select uuid_in(md5(random()::text || clock_timestamp()::text)::cstring),
       a.created_by,
       a.last_modified_date,
       a.last_modified_by,
       a.last_modified_date,
       false,
       a.id,
       'ERROR',
       a.organization_id,
       a.login,
       a.product_id,
       a.quantity,
       a.unit,
       a.code,
       a.print_code
from aggregation a
where deleted is not true
  and status = 'ERROR';


-- status UTILIZED
INSERT INTO aggregation_history (id, created_by, created_date, last_modified_by, last_modified_date, deleted,
                                 aggregation_id, status, organization_id, login, product_id, quantity, unit, code,
                                 print_code)
select uuid_in(md5(random()::text || clock_timestamp()::text)::cstring),
       a.created_by,
       a.last_modified_date,
       a.last_modified_by,
       a.last_modified_date,
       false,
       a.id,
       'UTILIZED',
       a.organization_id,
       a.login,
       a.product_id,
       a.quantity,
       a.unit,
       a.code,
       a.print_code
from aggregation a
where deleted is not true
  and status = 'UTILIZED';


-- status RETURNED
INSERT INTO aggregation_history (id, created_by, created_date, last_modified_by, last_modified_date, deleted,
                                 aggregation_id, status, organization_id, login, product_id, quantity, unit, code,
                                 print_code)
select uuid_in(md5(random()::text || clock_timestamp()::text)::cstring),
       a.created_by,
       a.last_modified_date,
       a.last_modified_date,
       a.last_modified_date,
       false,
       a.id,
       'SHIPPED',
       a.organization_id,
       a.login,
       a.product_id,
       a.quantity,
       a.unit,
       a.code,
       a.print_code
from aggregation a
where deleted is not true
  and status = 'RETURNED';
INSERT INTO aggregation_history (id, created_by, created_date, last_modified_by, last_modified_date, deleted,
                                 aggregation_id, status, organization_id, login, product_id, quantity, unit, code,
                                 print_code)
select uuid_in(md5(random()::text || clock_timestamp()::text)::cstring),
       a.created_by,
       a.last_modified_date,
       a.last_modified_date,
       a.last_modified_date,
       false,
       a.id,
       'RETURNED',
       a.organization_id,
       a.login,
       a.product_id,
       a.quantity,
       a.unit,
       a.code,
       a.print_code
from aggregation a
where deleted is not true
  and status = 'RETURNED';


-- status SHIPPED
INSERT INTO aggregation_history (id, created_by, created_date, last_modified_by, last_modified_date, deleted,
                                 aggregation_id, status, organization_id, login, product_id, quantity, unit, code,
                                 print_code)
select uuid_in(md5(random()::text || clock_timestamp()::text)::cstring),
       a.created_by,
       a.last_modified_date,
       a.last_modified_date,
       a.last_modified_date,
       false,
       a.id,
       'SHIPPED',
       a.organization_id,
       a.login,
       a.product_id,
       a.quantity,
       a.unit,
       a.code,
       a.print_code
from aggregation a
where deleted is not true
  and status = 'SHIPPED';
