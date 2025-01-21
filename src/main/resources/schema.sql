-- Посты блога
create table if not exists posts(
    post_id bigserial primary key,
    title varchar(256) not null,
    tags_str varchar(1000),
    text varchar(4000),
    like_count integer,
    create_time timestamp with time zone default current_timestamp not null
);

-- Картинки постов
create table if not exists post_images(
    post_id bigint not null unique,
    orig_filename varchar(256) not null,
    content_type varchar(256) not null,
    file_data bytea not null
);

-- Теги (справочник)
create table if not exists tags(
    tag_id bigserial primary key,
    tag_name varchar(256) not null unique,
    create_time timestamp with time zone default current_timestamp not null
);

-- Теги постов
create table if not exists post_tags(
    post_id bigint not null,
    tag_id bigint not null,
    constraint post_tags_pk primary key ( post_id, tag_id)
);
