create table open_wallets
(
    id              serial primary key,
    created_at      timestamp,
    updated_at      timestamp,
    user_id         varchar,
    blockchain_type varchar,
    address         text,
    private_key     text,
    seed_phrases    text,
    is_encrypted    boolean
);