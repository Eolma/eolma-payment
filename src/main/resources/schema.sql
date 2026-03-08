CREATE TABLE IF NOT EXISTS payment (
    id              VARCHAR(36) PRIMARY KEY,
    auction_id      BIGINT NOT NULL UNIQUE,
    product_id      BIGINT NOT NULL,
    buyer_id        VARCHAR(36) NOT NULL,
    seller_id       VARCHAR(36) NOT NULL,
    amount          BIGINT NOT NULL,
    status          VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    toss_payment_key VARCHAR(200),
    toss_order_id   VARCHAR(100) NOT NULL UNIQUE,
    payment_method  VARCHAR(30),
    confirmed_at    TIMESTAMPTZ,
    cancelled_at    TIMESTAMPTZ,
    failure_reason  VARCHAR(500),
    deadline_at     TIMESTAMPTZ NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_payment_auction ON payment(auction_id);
CREATE INDEX IF NOT EXISTS idx_payment_buyer ON payment(buyer_id);
CREATE INDEX IF NOT EXISTS idx_payment_seller ON payment(seller_id);
CREATE INDEX IF NOT EXISTS idx_payment_status ON payment(status);

CREATE TABLE IF NOT EXISTS settlement (
    id              VARCHAR(36) PRIMARY KEY,
    payment_id      VARCHAR(36) NOT NULL UNIQUE,
    seller_id       VARCHAR(36) NOT NULL,
    total_amount    BIGINT NOT NULL,
    fee_rate        DECIMAL(5,4) NOT NULL,
    fee_amount      BIGINT NOT NULL,
    settle_amount   BIGINT NOT NULL,
    status          VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    settled_at      TIMESTAMPTZ,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_settlement_seller ON settlement(seller_id);
CREATE INDEX IF NOT EXISTS idx_settlement_status ON settlement(status);

CREATE TABLE IF NOT EXISTS processed_event (
    event_id     VARCHAR(36) PRIMARY KEY,
    processed_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
