alter sequence posts_post_id_seq restart with 1;
alter sequence tags_tag_id_seq restart with 1;

-- Вставка начальных тестовых данных при отсутствии (проверка по title)
insert into
    posts
(
    title,
    tags_str,
    text,
    like_count
)
select
    s.title,
    s.tags_str,
    s.text,
    s.like_count
from
    (
    select
        'Байкал' as title,
        'lake nice' as tags_str,
        'Это замечательное озеро' as text,
        1 as like_count
    union all
    select
        'Амур',
        null,
        null,
        null
    union all
    select
        'Волга',
        'nice river saratov',
        'Это прекрасная река',
        0
    union all
    select
        'Саратов',
        'nice city saratov',
        'Это прекрасный город',
        3
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

insert into
    tags
(
    tag_name
)
select
    tag_name
from
    posts p
    cross join lateral string_to_table( p.tags_str, ' ') as tag_name
group by
    tag_name
having
    tag_name not in
        (
        select
            t.tag_name
        from
            tags t
        )
order by
    1
;

insert into
    post_tags
(
    post_id,
    tag_id
)
select
    a.post_id,
    t.tag_id
from
    (
    select
        p.post_id,
        tag_name
    from
        posts p
        cross join lateral string_to_table( p.tags_str, ' ') as tag_name
    group by
        p.post_id,
        tag_name
    ) a
    join tags t
        on t.tag_name = a.tag_name
except
select
    pt.post_id,
    pt.tag_id
from
    post_tags pt
;

-- id для временных данных (создавемые в процессе тестов) начинаются с 1001
alter sequence posts_post_id_seq restart with 1001;
alter sequence tags_tag_id_seq restart with 1001;
