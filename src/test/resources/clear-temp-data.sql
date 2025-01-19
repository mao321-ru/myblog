-- Удаляет временные данные, созданные при выполнении тестов (с post_id > 1000)
delete from posts where post_id > 1000;