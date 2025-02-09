# myblog: Приложение-блог с использованием Spring Boot

## Установка приложения в Docker

В случае доступности Docker Compose можно установить и запустить приложение командой:

```cmd
  ./gradlew clean assemble
  docker compose up --build --detach
```
После установки приложение будет доступно по URL:

http://localhost:9000/

Команда для остановки и удаление приложения:

```cmd
  docker compose down
```

Указанные выше команды успешно выполнялись с помощью:
- Docker version 27.5.1 build 9f9e405
- Docker Compose version v2.32.4-desktop.1 (вместо "docker compose" можно использовать "docker-compose")

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
- PostgreSQL 17.x (например PostgreSQL 17.2)
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

- создать тестовую БД в PostgreSQL для использования в автотестах при сборке приложения

Пример создания БД myblogdb_test для пользователя myblogdev скриптом src/test/db/myblogdb_test.sql:

```cmd
  psql postgresql://myblogdev@localhost:5432/postgres -f src/test/db/myblogdb_test.sql
```

- собрать war-файл приложения

Параметры подключения к основной БД указаны в файле src/main/resources/application.properties, к тестовой БД в файле src/test/resources/test-application.properties, при необходимости их можно изменить перед сборкой.

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

