create table user_firebase_tokens
(
    id              serial primary key,
    created_at      timestamp,
    updated_at      timestamp,
    user_id         varchar,
    firebase_token   text
);