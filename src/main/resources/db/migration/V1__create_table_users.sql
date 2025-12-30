create table if not exists users
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

insert into users (username, email, password_hash, first_name, last_name, enabled, account_locked,
                   created_at, updated_at, last_login_at, timezone, locale)
values ('AliceInWonderland', 'alice@mail.com', '$2a$10$67FXi.dIvWhzkY7YDJAsjO8AAUV2Wqh131/4NWf1gnuOn5Ej.facy',
        'Alice', 'Madness', true, false, now(), now(), null, 'UTC', 'ru'),
       ('BobInWonderland', 'bob@mail.com', '$2a$10$/WbwBlPL1T9uqN01ha7xTOUs8.mWceLJVTqYR1coYg6YYj3Xr/.L6',
        'Bob', 'Marley', true, false, now(), now(), null, 'UTC', 'ru');