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

create index if not exists checkingRooms on bookings (room_id, start_at);

create index if not exists checkingRents on bookings (user_id, start_at);