# myblog: Приложение-блог с использованием Spring Framework

## Сборка приложения

Для сборки используется Maven (например, Maven 3.9.9), команда:

```cmd
   mvn clean package
```

## Запуск тестов

Тесты выполняются при сборке, можно выполнить отдельно:

```cmd
  mvn test
```

## Установка приложения в сервлет-контейнер

Предварительно нужно установить:
- PostgreSQL (например PostgreSQL 17.2)
- Apache Tomcat 10.x (например, Apache Tomcat 10.1.34)

Порядок установки:

- создать пользователя в PostgreSQL

Пример создания пользователя myblogdev с паролем myblogdev скриптом src/main/db/myblogdev.sql с помощью утилиты командой строки psql:

```cmd
  psql postgresql://postgres@localhost:5432/postgres -f src/main/db/myblogdev.sql
```

- создать БД в PostgreSQL

Пример создания БД myblogdb, принадлежащей пользователю myblogdev скриптом src/main/db/myblogdb.sql:

```cmd
  psql postgresql://myblogdev@localhost:5432/postgres -f src/main/db/myblogdb.sql
```

- собрать war-файл приложения

Параметры подключения к БД указаны в файле src/main/resources/application.properties, при необходимости их можно изменить перед сборкой.
Сборка выполняется командой:

```cmd
   mvn clean package
```

- установить приложение в Tomcat

Нужно скопировать war-файл target/myblog.war в подкаталог webapps установки Tomcat.


## Запуск и использование

После установки приложение будет доступно по URL:

http://localhost:8080/myblog/

(в случае локальной установки Apache Tomcat и запуска по портом по умолчанию 8080)

