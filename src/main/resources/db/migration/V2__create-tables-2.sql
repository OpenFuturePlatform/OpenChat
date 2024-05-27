create table if not exists message_attachment
(
    id serial primary key ,
    attachment_id int,
    message_id    int
);