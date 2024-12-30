create table meeting_notes
(
    id            serial primary key,
    author        varchar(255),
    chat_id       int null,
    group_chat_id int null,
    members       text,
    recipient     varchar(255),
    generated_at  timestamp,
    version       int,
    start_time    timestamp,
    end_time      timestamp,
    notes         text
);
