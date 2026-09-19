-- RP Gate In/Out Management System
-- SQLite schema only. Java REST integration is intentionally not included.
PRAGMA foreign_keys = ON;

BEGIN;

CREATE TABLE personnel (
    personnel_id INTEGER PRIMARY KEY,
    army_no TEXT,
    card_no TEXT,
    rank TEXT,
    name TEXT NOT NULL,
    mobile_no TEXT,
    unit TEXT,
    status TEXT NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'INACTIVE')),
    created_at TEXT NOT NULL DEFAULT (strftime('%Y-%m-%dT%H:%M:%fZ', 'now')),
    updated_at TEXT NOT NULL DEFAULT (strftime('%Y-%m-%dT%H:%M:%fZ', 'now')),
    CHECK (length(trim(name)) > 0),
    CHECK (army_no IS NOT NULL OR card_no IS NOT NULL OR length(trim(name)) > 0)
);

CREATE UNIQUE INDEX uq_personnel_army_no
    ON personnel (lower(trim(army_no)))
    WHERE army_no IS NOT NULL AND length(trim(army_no)) > 0;
CREATE UNIQUE INDEX uq_personnel_card_no
    ON personnel (lower(trim(card_no)))
    WHERE card_no IS NOT NULL AND length(trim(card_no)) > 0;
CREATE INDEX ix_personnel_name ON personnel (lower(name));
CREATE INDEX ix_personnel_unit ON personnel (lower(unit));
CREATE INDEX ix_personnel_status ON personnel (status);

CREATE TABLE movement_records (
    movement_id INTEGER PRIMARY KEY,
    personnel_id INTEGER REFERENCES personnel(personnel_id) ON DELETE SET NULL,
    category TEXT NOT NULL CHECK (category IN ('Leave', 'TD / Posting', 'Vehicle In / Out', 'Outpass', 'Night Pass')),
    movement_type TEXT,
    movement_at TEXT NOT NULL,
    created_at TEXT NOT NULL DEFAULT (strftime('%Y-%m-%dT%H:%M:%fZ', 'now')),
    updated_at TEXT NOT NULL DEFAULT (strftime('%Y-%m-%dT%H:%M:%fZ', 'now')),
    status TEXT NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'CANCELLED', 'COMPLETED')),
    historical_army_no TEXT,
    historical_card_no TEXT,
    historical_rank TEXT,
    historical_name TEXT NOT NULL,
    historical_mobile_no TEXT,
    historical_unit TEXT,
    remarks TEXT,
    CHECK (length(trim(historical_name)) > 0),
    CHECK (length(trim(movement_at)) > 0),
    CHECK (movement_type IS NULL OR length(trim(movement_type)) > 0)
);

CREATE INDEX ix_movements_personnel_date ON movement_records (personnel_id, movement_at DESC);
CREATE INDEX ix_movements_category_date ON movement_records (category, movement_at DESC);
CREATE INDEX ix_movements_type_date ON movement_records (movement_type, movement_at DESC);
CREATE INDEX ix_movements_status_date ON movement_records (status, movement_at DESC);
CREATE INDEX ix_movements_date ON movement_records (movement_at DESC);
CREATE INDEX ix_movements_historical_identity
    ON movement_records (lower(historical_army_no), lower(historical_card_no), lower(historical_name));
CREATE INDEX ix_movements_historical_army ON movement_records (lower(historical_army_no));
CREATE INDEX ix_movements_historical_card ON movement_records (lower(historical_card_no));
CREATE INDEX ix_movements_historical_name ON movement_records (lower(historical_name));

CREATE TABLE leave_records (
    movement_id INTEGER PRIMARY KEY REFERENCES movement_records(movement_id) ON DELETE CASCADE,
    leave_type TEXT NOT NULL DEFAULT 'Annual Leave',
    destination_from TEXT,
    mobile_no TEXT,
    CHECK (length(trim(leave_type)) > 0)
);
CREATE INDEX ix_leave_type ON leave_records (leave_type);

CREATE TABLE td_posting_records (
    movement_id INTEGER PRIMARY KEY REFERENCES movement_records(movement_id) ON DELETE CASCADE,
    movement_subtype TEXT NOT NULL,
    unit_formation TEXT,
    authority_order_no TEXT,
    destination TEXT,
    CHECK (movement_subtype IN ('TD OUT', 'TD IN', 'POSTING IN', 'POSTING OUT'))
);
CREATE INDEX ix_td_posting_subtype ON td_posting_records (movement_subtype);
CREATE INDEX ix_td_posting_order ON td_posting_records (authority_order_no);

CREATE TABLE vehicle_records (
    movement_id INTEGER PRIMARY KEY REFERENCES movement_records(movement_id) ON DELETE CASCADE,
    vehicle_no TEXT NOT NULL,
    vehicle_type TEXT NOT NULL DEFAULT 'Motor Cycle',
    driver_personnel_id INTEGER REFERENCES personnel(personnel_id) ON DELETE SET NULL,
    purpose TEXT,
    CHECK (length(trim(vehicle_no)) > 0),
    CHECK (length(trim(vehicle_type)) > 0)
);
CREATE INDEX ix_vehicle_no ON vehicle_records (lower(vehicle_no));
CREATE INDEX ix_vehicle_driver ON vehicle_records (driver_personnel_id);
CREATE INDEX ix_vehicle_type ON vehicle_records (vehicle_type);

CREATE TABLE outpass_records (
    movement_id INTEGER PRIMARY KEY REFERENCES movement_records(movement_id) ON DELETE CASCADE,
    pass_no TEXT,
    time_out TEXT NOT NULL,
    expected_return TEXT,
    purpose_destination TEXT,
    CHECK (length(trim(time_out)) > 0),
    CHECK (expected_return IS NULL OR expected_return >= time_out)
);
CREATE INDEX ix_outpass_time_out ON outpass_records (time_out DESC);
CREATE INDEX ix_outpass_return ON outpass_records (expected_return);

CREATE TABLE night_pass_records (
    movement_id INTEGER PRIMARY KEY REFERENCES movement_records(movement_id) ON DELETE CASCADE,
    pass_no TEXT,
    time_out TEXT NOT NULL,
    return_time TEXT,
    destination TEXT,
    CHECK (length(trim(time_out)) > 0),
    CHECK (return_time IS NULL OR return_time >= time_out)
);
CREATE INDEX ix_night_pass_time_out ON night_pass_records (time_out DESC);
CREATE INDEX ix_night_pass_return ON night_pass_records (return_time);

CREATE UNIQUE INDEX uq_outpass_pass_no
    ON outpass_records (lower(trim(pass_no)))
    WHERE pass_no IS NOT NULL AND length(trim(pass_no)) > 0;
CREATE UNIQUE INDEX uq_night_pass_pass_no
    ON night_pass_records (lower(trim(pass_no)))
    WHERE pass_no IS NOT NULL AND length(trim(pass_no)) > 0;

COMMIT;
