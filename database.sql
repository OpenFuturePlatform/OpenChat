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