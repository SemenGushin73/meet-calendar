create table if not exists roles
(
    id   bigserial primary key,
    name varchar(100) not null unique
);

insert into roles (name)
values ('ADMIN'),
       ('USER');