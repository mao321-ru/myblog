-- Удаляет временные данные, созданные при выполнении тестов (с id > 1000)
delete from post_tags where post_id > 1000 or tag_id > 1000;
delete from post_images where post_id > 1000;
delete from posts where post_id > 1000;
delete from tags where tag_id > 1000;

-- id для временных данных (создавемые в процессе тестов) начинаются с 1001
alter sequence posts_post_id_seq restart with 1001;
alter sequence tags_tag_id_seq restart with 1001;
