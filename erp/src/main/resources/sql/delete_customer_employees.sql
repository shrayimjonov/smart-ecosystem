-- DISABLE constraints
DO
$$
    DECLARE
        stmt TEXT;
    BEGIN
        FOR stmt IN
            SELECT distinct 'ALTER TABLE ' || quote_ident(nsp.nspname) || '.' || quote_ident(cls.relname) ||
                            ' DISABLE TRIGGER ALL;'
            FROM pg_constraint con
                     JOIN pg_class cls ON cls.oid = con.conrelid
                     JOIN pg_namespace nsp ON nsp.oid = cls.relnamespace
            WHERE con.confrelid = 'jhi_user'::regclass
            LOOP
                EXECUTE stmt;
            END LOOP;
    END
$$;

-- ENABLE constraints
DO
$$
    DECLARE
        stmt TEXT;
    BEGIN
        FOR stmt IN
            SELECT distinct 'ALTER TABLE ' || quote_ident(nsp.nspname) || '.' || quote_ident(cls.relname) ||
                            ' ENABLE TRIGGER ALL;'
            FROM pg_constraint con
                     JOIN pg_class cls ON cls.oid = con.conrelid
                     JOIN pg_namespace nsp ON nsp.oid = cls.relnamespace
            WHERE con.confrelid = 'jhi_user'::regclass
            LOOP
                EXECUTE stmt;
            END LOOP;
    END
$$;

-- DELETE CUSTOMER EMPLOYEES
delete from jhi_user where customer_id is not null;

-- DELETE customer_id COLUMN FROM jhi_users
alter table jhi_user drop column customer_id;

-- UPDATE cases TABLE (assigned_id = 12345 comes from FRONT)
update cases set assignee_id = null where assignee_id = 12345;