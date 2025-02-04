-- Посты блога
create table if not exists posts(
    post_id bigserial primary key,
    title varchar(256) not null check( trim( title) != ''),
    tags_str varchar(1000),
    text varchar(32000),
    likes_count integer check (likes_count >= 0),
    create_time timestamp with time zone default current_timestamp not null
);

-- Картинки постов
create table if not exists post_images(
    post_id bigint not null unique references posts,
    orig_filename varchar(256) not null check( trim( orig_filename) != ''),
    content_type varchar(256) not null check( trim( content_type) != ''),
    file_data bytea not null
);

-- Теги (справочник)
create table if not exists tags(
    tag_id bigserial primary key,
    tag_name varchar(256) not null check( trim( tag_name) != '') unique,
    create_time timestamp with time zone default current_timestamp not null
);

-- Теги постов
create table if not exists post_tags(
    post_id bigint not null references posts,
    tag_id bigint not null references tags,
    constraint post_tags_pk primary key ( post_id, tag_id)
);

-- индекс для FK
create index if not exists
    post_tags_ix_tag_id
on
    post_tags( tag_id)
;

-- Комментарии постов
create table if not exists post_comments(
    comment_id bigserial primary key,
    post_id bigint not null references posts,
    comment_text varchar(4000) not null check(trim( comment_text) != ''),
    create_time timestamp with time zone default current_timestamp not null
);

-- индекс для FK
create index if not exists
    post_comments_ix_post_id
on
    post_comments( post_id)
;
