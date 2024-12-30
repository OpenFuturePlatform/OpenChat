create table blockchain_contracts
(
    id               serial primary key,
    created_at       timestamp,
    updated_at       timestamp,
    blockchain       varchar,
    is_test          boolean default false,
    contract_name    text,
    contract_address text,
    decimal          int
);

insert into blockchain_contracts
(created_at, updated_at, blockchain, is_test, contract_name, contract_address, decimal)
values (now(), now(), 'ETH', false, 'Tether USDT', '0xdac17f958d2ee523a2206206994597c13d831ec7', 6),
       (now(), now(), 'ETH', false, 'USD Coin', '0xA0b86991c6218b36c1d19D4a2e9Eb0cE3606eB48', 6),
       (now(), now(), 'ETH', true, 'Tether USDT', '0xaA8E23Fb1079EA71e0a56F48a2aA51851D8433D0', 6),
       (now(), now(), 'ETH', true, 'USD Coin', '0x1c7D4B196Cb0C7B01d743Fbc6116a902379C7238', 6),
       (now(), now(), 'TRX', false, 'Tether USDT', 'TR7NHqjeKQxGTCi8q8ZY4pL8otSzgjLj6t', 6),
       (now(), now(), 'TRX', true, 'Tether USDT', 'TG3XXyExBkPp9nzdajDZsozEu4BkaSJozs', 6),
       (now(), now(), 'BNB', false, 'USDT Token', '0x55d398326f99059ff775485246999027b3197955', 18),
       (now(), now(), 'BNB', true, 'USDT Token', '0x337610d27c682E347C9cD60BD4b3b107C9d34dDd', 18);