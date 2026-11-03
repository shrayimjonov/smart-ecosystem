create function check_referenced_tables(table_name text, id_to_delete bigint) returns text[]
    language plpgsql
as
$$
DECLARE
    fk RECORD;
    referenced_tables TEXT[] := ARRAY[]::TEXT[];
    is_referenced BOOLEAN;
BEGIN
    FOR fk IN
        SELECT
            conrelid::regclass AS child_table,
            a.attname AS child_column
        FROM
            pg_constraint AS c
                JOIN
            pg_attribute AS a ON a.attnum = ANY(c.conkey) AND a.attrelid = c.conrelid
        WHERE
            c.confrelid = table_name::regclass
          AND c.contype = 'f'
        LOOP
            EXECUTE format(
                    'SELECT EXISTS (
                        SELECT 1 FROM %I WHERE %I = $1
                    )', fk.child_table, fk.child_column
                    ) INTO is_referenced USING id_to_delete;
            IF is_referenced THEN
                referenced_tables := array_append(referenced_tables, fk.child_table::TEXT);
            END IF;
        END LOOP;
    IF array_length(referenced_tables, 1) > 0 THEN
        RETURN referenced_tables;
    ELSE
        RETURN NULL;
    END IF;
END;
$$;

alter function check_referenced_tables(text, bigint) owner to cts;