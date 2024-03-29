create database open_chat;
use open_chat;

drop table message;
create table message
(
    id          bigint auto_increment primary key,
    body        text,
    sent_at     timestamp,
    received_at timestamp,
    sender      varchar(255),
    recipient   varchar(255)
);

create table attachment
(
    id   int auto_increment primary key,
    name varchar(255),
    url  varchar(255)
);

create table user
(
    id            int auto_increment primary key,
    email         varchar(255),
    phone_number  varchar(255),
    registered_at timestamp,
    last_login    timestamp,
    first_name    varchar(255),
    last_name     varchar(255),
    avatar        varchar(255),
    active        bool default true
);
