-- Посты блога
create table if not exists posts(
    id bigserial primary key,
    title varchar(256) not null,
    text varchar(256),
    like_count integer,
    create_time timestamp with time zone default current_timestamp not null
);

-- Вставка начальных тестовых данных при отсутствии (проверка по title)
insert into
    posts
(
    title,
    text,
    like_count
)
select
    s.title,
    s.text,
    s.like_count
from
    (
    select
        'Байкал' as title,
        'Это замечательное озеро' as text,
        1 as like_count
    union all
    select
        'Амур',
        null,
        null
    ) s
where
    not exists
        (
        select
            null
        from
            posts t
        where
            t.title = s.title
        )
;
