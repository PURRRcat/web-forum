# Форум — Web-приложение на Spring MVC

## Быстрый старт

```bash
# Создать и наполнить БД
gradle cleanDb createDb initDb

# Запустить все тесты + покрытие
gradle test jacocoTestReport

# Собрать WAR
gradle war

# Запустить на встроенном Tomcat (http://localhost:8080/forum)
gradle appRun
```

Отчёты:
- `build/reports/tests/test/index.html` — результаты тестов
- `build/reports/jacoco/test/html/index.html` — покрытие кода


## Описание сценариев системного тестирования

Системные тесты реализованы в файле `test/SystemTest.java` с помощью Spring MockMvc.

### Просмотр главной страницы

**Сценарий 1. Успешное отображение списка разделов**

Предусловие: в БД существует хотя бы один раздел.  
Запрос: `GET /`  
Ожидаемый результат: HTTP 200, представление `home`, модель содержит атрибут `categories`.  
Метод теста: `testHomePageShowsCategories`

### Регистрация нового пользователя

**Сценарий 2. Успешная регистрация**

Предусловие: имя пользователя и e-mail не заняты.  
Запрос: `POST /user/register` с параметрами `username`, `email`, `password`, `confirmPassword` (совпадают).  
Ожидаемый результат: HTTP 302 → `/user/login`; запись создана в таблице `users`.  
Метод теста: `testRegisterSuccess`

**Сценарий 3. Имя пользователя уже занято**

Предусловие: пользователь с таким `username` уже существует.  
Запрос: `POST /user/register` с занятым именем.  
Ожидаемый результат: HTTP 302 → `/user/register` (форма открыта повторно с сообщением об ошибке).  
Метод теста: `testRegisterFailUsernameExists`

**Сценарий 4. E-mail уже зарегистрирован**

Предусловие: пользователь с таким `email` уже существует.  
Запрос: `POST /user/register` с занятым e-mail.  
Ожидаемый результат: HTTP 302 → `/user/register`.  
Метод теста: `testRegisterFailEmailExists`

**Сценарий 5. Пароли не совпадают**

Предусловие: —  
Запрос: `POST /user/register`, где `password` ≠ `confirmPassword`.  
Ожидаемый результат: HTTP 302 → `/user/register`.  
Метод теста: `testRegisterFailPasswordMismatch`

### Вход в систему

**Сценарий 6. Успешный вход**

Предусловие: пользователь с таким именем и паролем существует.  
Запрос: `POST /user/login` с корректными `username` и `password`.  
Ожидаемый результат: HTTP 302 → `/`; в сессии установлены `userId`, `username`, `role`.  
Метод теста: `testLoginSuccess`

**Сценарий 7. Неверный пароль**

Предусловие: пользователь существует.  
Запрос: `POST /user/login` с неверным паролем.  
Ожидаемый результат: HTTP 302 → `/user/login` (сообщение об ошибке во flash-атрибуте).  
Метод теста: `testLoginFailInvalidPassword`

**Сценарий 8. Пользователь не найден**

Предусловие: пользователя с таким именем нет в БД.  
Запрос: `POST /user/login` с несуществующим `username`.  
Ожидаемый результат: HTTP 302 → `/user/login`.  
Метод теста: `testLoginFailUserNotFound`

### Просмотр раздела форума

**Сценарий 9. Успешный просмотр раздела**

Предусловие: раздел и хотя бы одна тема существуют.  
Запрос: `GET /category/{id}`  
Ожидаемый результат: HTTP 200, представление `category`, модель содержит `category` и `topics`.  
Метод теста: `testViewCategory`

### Создание темы

**Сценарий 10. Успешное создание темы авторизованным пользователем**

Предусловие: пользователь аутентифицирован; раздел существует.  
Запрос: `POST /topic/new/{categoryId}` с параметром `title`; сессия с `userId`.  
Ожидаемый результат: HTTP 302 → `/topic/{id}`; тема сохранена в БД.  
Метод теста: `testCreateTopicSuccess`

**Сценарий 11. Попытка создания темы без авторизации**

Предусловие: пользователь не вошёл в систему.  
Запрос: `GET /topic/new/{categoryId}` без сессии.  
Ожидаемый результат: HTTP 302 → `/user/login` (перехват `AuthInterceptor`).  
Метод теста: `testCreateTopicRedirectsToLoginWhenNotAuthenticated`

### Просмотр темы

**Сценарий 12. Успешный просмотр темы**

Предусловие: тема существует.  
Запрос: `GET /topic/{id}`  
Ожидаемый результат: HTTP 200, представление `topic`, модель содержит `topic` и `posts`.  
Метод теста: `testViewTopic`

### Добавление сообщения

**Сценарий 13. Успешное добавление сообщения авторизованным пользователем**

Предусловие: пользователь аутентифицирован; тема существует.  
Запрос: `POST /post/new/{topicId}` с параметром `content`; сессия с `userId`.  
Ожидаемый результат: HTTP 302 → `/topic/{id}`; сообщение сохранено в БД.  
Метод теста: `testCreatePostSuccess`

**Сценарий 14. Попытка добавления сообщения без авторизации**

Предусловие: пользователь не вошёл в систему.  
Запрос: `POST /post/new/{topicId}` без сессии.  
Ожидаемый результат: HTTP 302 → `/user/login` (перехват `AuthInterceptor`).  
Метод теста: `testCreatePostRedirectsToLoginWhenNotAuthenticated`


### Панель администратора

**Сценарий 15. Администратор просматривает список пользователей**

Предусловие: пользователь вошёл с ролью `admin`.  
Запрос: `GET /admin/users` с сессией (`role = "admin"`).  
Ожидаемый результат: HTTP 200, представление `admin/users`, модель содержит `users`.  
Метод теста: `testAdminViewUsers`

**Сценарий 16. Обычный пользователь не имеет доступа к панели администратора**

Предусловие: пользователь вошёл с ролью `user`.  
Запрос: `GET /admin/users` с сессией (`role = "user"`).  
Ожидаемый результат: HTTP 403, представление `error/403`.  
Метод теста: `testAdminAccessForbiddenForRegularUser`

### Результаты выполнения тестов

```
SystemTest:              16 тестов, 0 ошибок, 0 пропущено
ForumDaoIntegrationTest:  8 тестов, 0 ошибок, 0 пропущено
Итого: 24 теста — все прошли успешно
```


## Hibernate

- Конфиг: `src/main/resources/hibernate.cfg.xml`
- Утилита: `src/main/java/ru/forum/util/HibernateUtil.java`
- Сущности: `src/main/java/ru/forum/model/`
- DAO: `src/main/java/ru/forum/dao/`
