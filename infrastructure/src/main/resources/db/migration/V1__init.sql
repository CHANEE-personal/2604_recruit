CREATE TABLE IF NOT EXISTS member
(
    id
    BIGINT
    NOT
    NULL
    AUTO_INCREMENT,
    phone_number
    VARCHAR
(
    20
) NOT NULL,
    subscription_status VARCHAR
(
    20
) NOT NULL,
    PRIMARY KEY
(
    id
),
    UNIQUE KEY uk_member_phone_number
(
    phone_number
)
    );

CREATE TABLE IF NOT EXISTS channel
(
    id
    BIGINT
    NOT
    NULL
    AUTO_INCREMENT,
    name
    VARCHAR
(
    100
) NOT NULL,
    channel_type VARCHAR
(
    30
) NOT NULL,
    PRIMARY KEY
(
    id
)
    );

CREATE TABLE IF NOT EXISTS subscription_history
(
    id
    BIGINT
    NOT
    NULL
    AUTO_INCREMENT,
    member_id
    BIGINT
    NOT
    NULL,
    phone_number
    VARCHAR
(
    20
) NOT NULL,
    channel_id BIGINT NOT NULL,
    channel_name VARCHAR
(
    100
) NOT NULL,
    previous_status VARCHAR
(
    20
) NOT NULL,
    new_status VARCHAR
(
    20
) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY
(
    id
),
    INDEX idx_subscription_history_phone_number
(
    phone_number
),
    INDEX idx_subscription_history_member_id
(
    member_id
)
    );
