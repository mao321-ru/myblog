# myblog: Приложение-блог с использованием Spring Boot

## Установка приложения в Docker

В случае доступности Docker Compose можно установить и запустить приложение командой:

```cmd
  docker compose up --build --detach
```
После установки приложение будет доступно по URL:

[http://localhost:9000](http://localhost:9000)

Команда для остановки и удаление приложения:

```cmd
  docker compose down
```

Указанные выше команды успешно выполнялись с помощью:
- Docker version 27.5.1 build 9f9e405
- Docker Compose version v2.32.4-desktop.1 (вместо "docker compose" можно использовать "docker-compose")

## Сборка приложения

Предварительные требования:
- Java 21 (например, Eclipse Temurin OpenJDK 21.0.5+11)

Для сборки используется Gradle, команда:

```cmd
   ./gradlew clean bootJar
```

## Установка приложения

Предварительные требования:
- PostgreSQL 17.x (например PostgreSQL 17.2)

Порядок установки:

- создать пользователя в PostgreSQL

Пример создания пользователя myblogdev с паролем myblogdev скриптом src/main/db/init/10_myblogdev.sql с помощью утилиты командой строки psql:

```cmd
  psql postgresql://postgres@localhost:5432/postgres -f src/main/db/init/10_myblogdev.sql
```

- создать БД в PostgreSQL

Пример создания БД myblogdb, принадлежащей пользователю myblogdev скриптом src/main/db/init/20_myblogdb.sql:

```cmd
  psql postgresql://myblogdev@localhost:5432/postgres -f src/main/db/init/20_myblogdb.sql
```

- запустить приложение в консоли командой (прервать выполнение можно по Ctrl-C)

```cmd
  java -jar build/libs/myblog-0.0.2-SNAPSHOT.jar
```

После запуска приложение будет доступно по URL:

[http://localhost:9090](http://localhost:9090)


## Запуск тестов

Предварительно нужно:

- создать пользователя myblogdev в PostgreSQL (если он не был создан при [Установка приложения](#Установка-приложения), пример команды там же);

- создать тестовую БД в PostgreSQL для использования в тестах

Пример создания БД myblogdb_test для пользователя myblogdev скриптом src/integrationTest/db/myblogdb_test.sql:

```cmd
  psql postgresql://myblogdev@localhost:5432/postgres -f src/integrationTest/db/myblogdb_test.sql
```

Тесты запускаются командой:

```cmd
  ./gradlew cleanTest check
```
