create table users
(
    id             bigserial primary key,
    username       varchar(100) not null unique,
    email          varchar(255) not null unique,
    password_hash  varchar(255) not null,
    first_name     varchar(100) not null,
    last_name      varchar(100) not null,
    enabled        boolean      not null default true,
    account_locked boolean      not null default false,
    created_at     timestamptz  not null default now(),
    updated_at     timestamptz  not null default now(),
    last_login_at  timestamptz,
    timezone       varchar(50)  not null default 'UTC',
    locale         varchar(20)  not null default 'ru'
);

create table if not exists rooms
(
    id   bigserial primary key,
    name varchar(100) not null unique
);

create table if not exists bookings
(
    id       bigserial primary key,
    room_id  bigint       not null references rooms (id),
    user_id  bigint       not null references users (id),

    title    varchar(200) not null,

    start_at timestamptz  not null,
    end_at   timestamptz  not null,

    constraint chk_time_order check (end_at > start_at)
);

create table if not exists roles
(
    id   bigserial primary key,
    name varchar(100) not null unique
);

create table if not exists user_roles
(
    user_id bigint not null references users (id),
    role_id bigint not null references roles (id),
    primary key (user_id, role_id)
);

insert into users (username, email, password_hash, first_name, last_name, enabled, account_locked,
                   created_at, updated_at, last_login_at, timezone, locale)
values ('AliceInWonderland', 'alice@mail.com', '$2a$10$67FXi.dIvWhzkY7YDJAsjO8AAUV2Wqh131/4NWf1gnuOn5Ej.facy',
        'Alice', 'Madness', true, false, now(), now(), null, 'UTC', 'ru'),
       ('BobInWonderland', 'bob@mail.com', '$2a$10$/WbwBlPL1T9uqN01ha7xTOUs8.mWceLJVTqYR1coYg6YYj3Xr/.L6',
        'Bob', 'Marley', true, false, now(), now(), null, 'UTC', 'ru');

insert into rooms (name)
values ('Балкон 3 этаж'),
       ('Переговорка на 3 этаже 3 этаж'),
       ('Север Подвал'),
       ('Юг Подвал'),
       ('Восток Подвал'),
       ('Запад Подвал'),
       ('Под винтовой лестницей 2 этаж'),
       ('Мой календарь');

insert into roles (name)
values ('ADMIN'),
       ('USER');

insert into user_roles(user_id, role_id)
values ((select id from users where username = 'AliceInWonderland'),
        (select id from roles where name = 'USER'));

insert into user_roles(user_id, role_id)
values ((select id from users where username = 'AliceInWonderland'),
        (select id from roles where name = 'ADMIN'));


insert into user_roles(user_id, role_id)
values ((select id from users where username = 'BobInWonderland'),
        (select id from roles where name = 'USER'));

create index if not exists checkingRooms on bookings (room_id, start_at);

create index if not exists checkingRents on bookings (user_id, start_at);