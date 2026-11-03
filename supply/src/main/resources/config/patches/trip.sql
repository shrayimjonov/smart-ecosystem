CREATE TABLE IF NOT EXISTS trip (
                                     id BIGSERIAL PRIMARY KEY,

                                     expeditor_id BIGINT NOT NULL,
                                     trip_number VARCHAR(255) NOT NULL,
                                     status VARCHAR(50) NOT NULL,
                                     trip_date DATE,

                                     creator_id BIGINT,
                                     updater_id BIGINT,
                                     created_date TIMESTAMP,
                                     updated_date TIMESTAMP,

                                     deleted BOOLEAN DEFAULT FALSE NOT NULL
);


ALTER TABLE trip
    ADD CONSTRAINT fk_trips_expeditor_id
        FOREIGN KEY (expeditor_id)
            REFERENCES users (id);