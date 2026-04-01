# Форум

## Структура
- `build.gradle` — сборка, зависимости, задачи для HSQLDB
- `create.sql`, `init.sql` — схемы и начальные данные
- `src/main/java` — сущности, DAO
- `test/` — тесты

## Быстрый старт
Cоздать и наполнить базу:
```bash 
gradle cleanDb createDb initDb 
``` 
Выполнить тесты:
```bash 
gradle test jacocoTestReport --no-daemon
``` 
Отчеты можно смотреть по путям: 
- build/reports/jacoco/test/html/index.html
- build/reports/tests/test/index.html

## Hibernate
- Конфиг: `src/main/resources/hibernate.cfg.xml`
- Утилита: `src/main/java/ru/forum/util/HibernateUtil.java`
- Сущности: `src/main/java/ru/forum/model/*`
- DAO: `src/main/java/ru/forum/dao/*`

## Тесты
- `test/ForumDaoIntegrationTest.java`
