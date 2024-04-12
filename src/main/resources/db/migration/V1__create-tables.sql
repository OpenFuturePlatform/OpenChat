create table message
(
    id              serial primary key,
    private_chat_id integer,
    body            text,
    sent_at         timestamp,
    received_at     timestamp,
    sender          varchar(255),
    recipient       varchar(255),
    content_type    varchar(20)
);

create table private_chat
(
    id serial primary key
);

create table chat_participant
(
    id      serial primary key,
    chat_id integer,
    "user"  varchar(255)
);

create table attachment
(
    id         serial primary key,
    name       varchar(255),
    url        varchar(255),
    message_id integer,
    created_at timestamp
);

create table "user"
(
    id            serial primary key,
    email         varchar(255),
    phone_number  varchar(255),
    registered_at timestamp,
    last_login    timestamp,
    first_name    varchar(255),
    last_name     varchar(255),
    avatar        varchar(255),
    active        bool default true
);

create table "group"
(
    id          serial primary key,
    creator     int,
    created_at  timestamp,
    name        varchar(255),
    archived    bool default false,
    archived_at timestamp
);

create table group_message
(
    id           serial primary key,
    body         text,
    sender       varchar(255),
    content_type varchar(20),
    "group_id"   int,
    sent_at      timestamp
);

create table group_participant
(
    participant int,
    group_id    int,
    deleted     bool
)