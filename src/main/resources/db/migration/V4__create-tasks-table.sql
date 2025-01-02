create table open_tasks
(
    id               serial primary key,
    created_at       timestamp,
    updated_at       timestamp,
    assignor         varchar(255),
    assignee         varchar(255),
    task_title       varchar(255),
    task_description text,
    task_date        date
);