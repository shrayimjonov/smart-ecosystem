ALTER TABLE company
    ADD COLUMN IF NOT EXISTS parent_id BIGINT;

ALTER TABLE company
    ADD COLUMN IF NOT EXISTS patent_date TIMESTAMP;

DO $$
    BEGIN
        IF NOT EXISTS (
            SELECT 1
            FROM pg_constraint
            WHERE conname = 'fk_company_parent'
        ) THEN
            ALTER TABLE company
                ADD CONSTRAINT fk_company_parent
                    FOREIGN KEY (parent_id)
                        REFERENCES company (id);
        END IF;
    END$$;