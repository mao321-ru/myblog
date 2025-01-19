-- Посты блога
create table if not exists posts(
    post_id bigserial primary key,
    title varchar(256) not null,
    text varchar(256),
    like_count integer,
    create_time timestamp with time zone default current_timestamp not null
);