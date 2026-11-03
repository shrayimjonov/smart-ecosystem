ALTER TABLE company_permissions
    ADD COLUMN IF NOT EXISTS id BIGSERIAL;

ALTER TABLE company_permissions
    ADD COLUMN IF NOT EXISTS start_date TIMESTAMP,
    ADD COLUMN IF NOT EXISTS unit_count INTEGER,
    ADD COLUMN IF NOT EXISTS amount NUMERIC(25, 2),
    ADD COLUMN IF NOT EXISTS agreement_id BIGINT,
    ADD COLUMN IF NOT EXISTS xizmat_id BIGINT,
    ADD COLUMN IF NOT EXISTS title VARCHAR(255),
    ADD COLUMN IF NOT EXISTS title_uz VARCHAR(255),
    ADD COLUMN IF NOT EXISTS deleted BOOLEAN DEFAULT FALSE NOT NULL,
    ADD COLUMN IF NOT EXISTS ximat_type VARCHAR(255);

ALTER TABLE company_permissions
    RENAME COLUMN permissions_key TO service_type;

ALTER TABLE company_permissions
    RENAME COLUMN expire_date TO end_date;

ALTER TABLE company_permissions
    DROP CONSTRAINT company_permissions_pkey;

ALTER TABLE company_permissions
    ADD CONSTRAINT company_permissions_pkey PRIMARY KEY (id);

ALTER TABLE company_permissions
    ADD CONSTRAINT uq_company_permissions_company_service
        UNIQUE (company_id, service_type);