CREATE TYPE device_types AS ENUM ('HVAC', 'SOLAR', 'METER');

CREATE TABLE building (
    id           INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name         VARCHAR(255) NOT NULL UNIQUE,
    location     VARCHAR(500) NOT NULL
);

CREATE TABLE device(
    id           INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    type         DEVICE_TYPES NOT NULL,
    manufacturer VARCHAR(255) NOT NULL
);

CREATE TABLE measurement(
    id           INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    device_id    INTEGER,
    building_id  INTEGER,
    timestamp    TIMESTAMPTZ NOT NULL,
    energy_kwh   FLOAT NOT NULL,
    CONSTRAINT fk_measurement_device FOREIGN KEY (device_id) REFERENCES Device(id),
    CONSTRAINT fk_measurement_building FOREIGN KEY (building_id) REFERENCES building(id)
);

CREATE TABLE device_building(
    device_id INTEGER,
    building_id INTEGER,
    installed_since TIMESTAMPTZ NOT NULL,
    PRIMARY KEY (device_id, building_id), --prevents duplicates
    CONSTRAINT fk_devicebuilding_device FOREIGN KEY (device_id) REFERENCES Device(id),
    CONSTRAINT fk_devicebuilding_building FOREIGN KEY (building_id) REFERENCES building(id)
)


