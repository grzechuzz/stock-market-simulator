CREATE TABLE bank_stock (
    name TEXT PRIMARY KEY,
    quantity INTEGER NOT NULL CHECK (quantity >= 0)
);

CREATE TABLE wallet (
    id TEXT PRIMARY KEY
);

CREATE TABLE wallet_stock (
    wallet_id TEXT NOT NULL,
    stock_name TEXT NOT NULL,
    quantity INTEGER NOT NULL CHECK (quantity > 0),

    PRIMARY KEY (wallet_id, stock_name),
    FOREIGN KEY (wallet_id) REFERENCES wallet (id) ON DELETE CASCADE
);

CREATE TABLE audit_log (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    type VARCHAR(16) NOT NULL CHECK (type IN ('BUY', 'SELL')),
    wallet_id TEXT NOT NULL,
    stock_name TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
