create table if not exists user_roles
(
    user_id bigint not null references users (id),
    role_id bigint not null references roles (id),
    primary key (user_id, role_id)
    );

insert into user_roles(user_id, role_id)
values ((select id from users where username = 'AliceInWonderland'),
        (select id from roles where name = 'USER'));

insert into user_roles(user_id, role_id)
values ((select id from users where username = 'AliceInWonderland'),
        (select id from roles where name = 'ADMIN'));


insert into user_roles(user_id, role_id)
values ((select id from users where username = 'BobInWonderland'),
        (select id from roles where name = 'USER'));