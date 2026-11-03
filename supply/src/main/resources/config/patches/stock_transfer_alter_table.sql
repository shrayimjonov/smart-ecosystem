ALTER TABLE stock_transfer
    ADD COLUMN IF NOT EXISTS trip_id BIGINT;

ALTER TABLE stock_transfer
    ADD CONSTRAINT fk_stock_transfer_trip
        FOREIGN KEY (trip_id)
            REFERENCES trip (id);

-- CREATE INDEX IF NOT EXISTS idx_stock_transfer_trip_id
--     ON stock_transfer (trip_id);