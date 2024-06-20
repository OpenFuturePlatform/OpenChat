create table assistant_notes
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

create table assistant_todos
(
    id            serial primary key,
    author        varchar(255),
    chat_id       int null,
    group_chat_id int null,
    members       json,
    recipient     varchar(255),
    generated_at  timestamp,
    version       int,
    start_time    timestamp,
    end_time      timestamp,
    todos         json
);

create table assistant_reminders
(
    id            serial primary key,
    author        varchar(255),
    chat_id       int null,
    group_chat_id int null,
    members       json,
    recipient     varchar(255),
    generated_at  timestamp,
    version       int,
    start_time    timestamp,
    end_time      timestamp,
    reminders     json
);