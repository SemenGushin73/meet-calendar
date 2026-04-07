create table if not exists rooms
(
    id   bigserial primary key,
    name varchar(100) not null unique
);

insert into rooms (name)
values ('Балкон 3 этаж'),
       ('Переговорка на 3 этаже 3 этаж'),
       ('Север Подвал'),
       ('Юг Подвал'),
       ('Восток Подвал'),
       ('Запад Подвал'),
       ('Под винтовой лестницей 2 этаж'),
       ('Мой календарь');