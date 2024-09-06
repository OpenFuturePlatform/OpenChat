create table open_wallets
(
    id              serial primary key,
    created_at      timestamp,
    updated_at      timestamp,
    user_id         varchar,
    blockchain_type varchar,
    address         text,
    balance         varchar
);

create table wallet_contracts
(
    id               serial primary key,
    created_at       timestamp,
    updated_at       timestamp,
    blockchain_type  varchar,
    address          text,
    contract_address text,
    amount           varchar,
    from_address     text
);