import org.htmlunit.BrowserVersion;
import org.htmlunit.WebClient;
import org.htmlunit.html.*;
import org.apache.catalina.startup.Tomcat;
import org.junit.jupiter.api.*;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SeleniumTest {

    static final int    PORT = 8081;
    static final String BASE = "http://localhost:" + PORT + "/forum";

    static Tomcat tomcat;

    static final String REG_USER  = "ivan_" + System.nanoTime();
    static final String REG_EMAIL = REG_USER + "@test.com";
    static final String REG_PASS  = "password";

    static final String CAT_TITLE   = "Программирование_" + System.nanoTime();
    static final String TOPIC_TITLE = "Первая тема теста";
    static final String POST_TEXT   = "Привет, это системный тест!";

    static Long categoryId;
    static Long topicId;

    WebClient client;

    @BeforeAll
    static void startServer() throws Exception {
        Logger.getLogger("org.apache").setLevel(Level.WARNING);
        Logger.getLogger("org.htmlunit").setLevel(Level.OFF);

        System.setProperty("hibernate.connection.url", "jdbc:hsqldb:mem:testforum");

        String warPath = System.getProperty("war.path", "build/libs/forum.war");

        tomcat = new Tomcat();
        tomcat.setPort(PORT);
        tomcat.setBaseDir("build/tomcat-sysTest");
        tomcat.getConnector();
        tomcat.addWebapp("/forum", new File(warPath).getAbsolutePath());
        tomcat.start();

        System.out.println("[SeleniumTest] Сервер запущен: " + BASE);
    }

    @AfterAll
    static void stopServer() throws Exception {
        if (tomcat != null) tomcat.stop();
        System.out.println("[SeleniumTest] Сервер остановлен.");
    }

    @BeforeEach
    void setup() {
        client = new WebClient(BrowserVersion.BEST_SUPPORTED);
        client.getOptions().setJavaScriptEnabled(false);
        client.getOptions().setCssEnabled(false);
        client.getOptions().setThrowExceptionOnScriptError(false);
        client.getOptions().setPrintContentOnFailingStatusCode(false);
        client.getOptions().setThrowExceptionOnFailingStatusCode(false);
    }

    @AfterEach
    void teardown() {
        client.close();
    }

    private HtmlPage get(String path) throws Exception {
        return client.getPage(BASE + path);
    }

    private HtmlPage submitForm(HtmlPage page, String... nameValues) throws Exception {
        HtmlForm form = page.getForms().get(0);
        for (int i = 0; i < nameValues.length; i += 2) {
            HtmlElement el = form.getFirstByXPath("//*[@name='" + nameValues[i] + "']");
            if (el instanceof HtmlInput)    ((HtmlInput)    el).setValue(nameValues[i + 1]);
            if (el instanceof HtmlTextArea) ((HtmlTextArea) el).setText(nameValues[i + 1]);
        }
        return form.<HtmlElement>querySelector("button[type='submit'],button:not([type])").click();
    }

    private HtmlPage loginAs(String username, String password) throws Exception {
        HtmlPage page = get("/user/login");
        return submitForm(page, "username", username, "password", password);
    }

    @Test @Order(1)
    @DisplayName("Сц.1 — Гость открывает главную: заголовок «Форум», разделов нет")
    void testGuestViewsEmptyHomePage() throws Exception {
        HtmlPage page = get("/");
        assertEquals("Форум", page.getTitleText());
        assertTrue(page.asNormalizedText().contains("Разделов пока нет"),
                "Ожидается сообщение об отсутствии разделов");
    }

    @Test @Order(2)
    @DisplayName("Сц.2 — Страница входа содержит форму с полями username и password")
    void testLoginPageAccessible() throws Exception {
        HtmlPage page = get("/user/login");
        assertNotNull(page.getForms().get(0).getInputByName("username"));
        assertNotNull(page.getForms().get(0).getInputByName("password"));
    }

    @Test @Order(3)
    @DisplayName("Сц.3 — Регистрация: пароли не совпадают → форма с ошибкой")
    void testRegisterFailPasswordMismatch() throws Exception {
        HtmlPage page = get("/user/register");
        HtmlPage result = submitForm(page,
                "username", REG_USER,
                "email",    REG_EMAIL,
                "password", "abc123",
                "confirmPassword", "xyz999");
        assertTrue(result.getUrl().toString().contains("/user/register"),
                "Должен остаться на странице регистрации");
        assertTrue(result.asNormalizedText().contains("не совпадают"),
                "Должно быть сообщение о несовпадении паролей");
    }

    @Test @Order(4)
    @DisplayName("Сц.4 — Успешная регистрация → перенаправление на страницу входа")
    void testRegisterSuccess() throws Exception {
        HtmlPage page = get("/user/register");
        HtmlPage result = submitForm(page,
                "username", REG_USER,
                "email",    REG_EMAIL,
                "password", REG_PASS,
                "confirmPassword", REG_PASS);
        assertTrue(result.getUrl().toString().contains("/user/login"),
                "После регистрации должен быть редирект на /user/login");
        assertTrue(result.asNormalizedText().contains("Регистрация выполнена"),
                "Должно быть сообщение об успешной регистрации");
    }

    @Test @Order(5)
    @DisplayName("Сц.5 — Регистрация с занятым именем → ошибка «уже занято»")
    void testRegisterFailUsernameExists() throws Exception {
        HtmlPage page = get("/user/register");
        HtmlPage result = submitForm(page,
                "username", REG_USER,
                "email",    "other_" + REG_EMAIL,
                "password", REG_PASS,
                "confirmPassword", REG_PASS);
        assertTrue(result.getUrl().toString().contains("/user/register"),
                "Должен остаться на странице регистрации");
        assertTrue(result.asNormalizedText().contains("уже занято"),
                "Должно быть сообщение о занятом имени");
    }

    @Test @Order(6)
    @DisplayName("Сц.6 — Регистрация с занятым e-mail → ошибка «уже зарегистрирован»")
    void testRegisterFailEmailExists() throws Exception {
        HtmlPage page = get("/user/register");
        HtmlPage result = submitForm(page,
                "username", "other_" + REG_USER,
                "email",    REG_EMAIL,
                "password", REG_PASS,
                "confirmPassword", REG_PASS);
        assertTrue(result.getUrl().toString().contains("/user/register"),
                "Должен остаться на странице регистрации");
        assertTrue(result.asNormalizedText().contains("уже зарегистрирован"),
                "Должно быть сообщение о занятом e-mail");
    }

    @Test @Order(7)
    @DisplayName("Сц.7 — Вход с неверным паролем → сообщение об ошибке")
    void testLoginFailWrongPassword() throws Exception {
        HtmlPage result = loginAs(REG_USER, "wrongpassword");
        assertTrue(result.getUrl().toString().contains("/user/login"),
                "Должен остаться на странице входа");
        assertTrue(result.asNormalizedText().contains("Неверное"),
                "Должно быть сообщение об ошибке");
    }

    @Test @Order(8)
    @DisplayName("Сц.8 — Успешный вход → главная страница с именем пользователя")
    void testLoginSuccess() throws Exception {
        HtmlPage result = loginAs(REG_USER, REG_PASS);
        assertTrue(result.getUrl().toString().endsWith("/forum/")
                        || result.getUrl().toString().endsWith("/forum"),
                "После входа должна открыться главная страница");
        assertTrue(result.asNormalizedText().contains(REG_USER),
                "Имя пользователя должно быть видно в навбаре");
    }

    @Test @Order(9)
    @DisplayName("Сц.9 — Администратор создаёт раздел → он виден на главной странице")
    void testAdminCreatesCategory() throws Exception {
        loginAs("admin", "admin");

        HtmlPage formPage = get("/category/new");
        HtmlPage result = submitForm(formPage,
                "title", CAT_TITLE,
                "description", "Тестовый раздел для системного теста");

        HtmlPage home = get("/");
        assertTrue(home.asNormalizedText().contains(CAT_TITLE),
                "Новый раздел должен быть виден на главной странице");

        HtmlAnchor catLink = home.getFirstByXPath("//a[contains(text(),'" + CAT_TITLE + "')]");
        assertNotNull(catLink, "Должна быть ссылка на раздел");
        String href = catLink.getHrefAttribute();
        categoryId = Long.parseLong(href.replaceAll(".*/category/(\\d+).*", "$1"));
    }

    @Test @Order(10)
    @DisplayName("Сц.10 — Гость пытается создать тему → редирект на страницу входа")
    void testUnauthenticatedCannotCreateTopic() throws Exception {
        HtmlPage result = get("/topic/new/" + categoryId);
        assertTrue(result.getUrl().toString().contains("/user/login"),
                "Неаутентифицированный пользователь должен быть перенаправлен на вход");
    }

    @Test @Order(11)
    @DisplayName("Сц.11 — Авторизованный пользователь создаёт тему → открывается страница темы")
    void testCreateTopic() throws Exception {
        loginAs(REG_USER, REG_PASS);

        HtmlPage formPage = get("/topic/new/" + categoryId);
        HtmlPage topicPage = submitForm(formPage,
                "title",   TOPIC_TITLE,
                "content", "Первое сообщение темы");

        assertTrue(topicPage.asNormalizedText().contains(TOPIC_TITLE),
                "Заголовок темы должен быть виден на странице темы");

        String url = topicPage.getUrl().toString();
        topicId = Long.parseLong(url.replaceAll(".*/topic/(\\d+).*", "$1"));
    }

    @Test @Order(12)
    @DisplayName("Сц.12 — Гость пытается отправить сообщение → редирект на вход")
    void testUnauthenticatedCannotPost() throws Exception {
        HtmlPage topicPage = get("/topic/" + topicId);
        assertFalse(topicPage.asNormalizedText().contains("Добавить сообщение"),
                "Форма добавления сообщения не должна быть видна гостю");
        assertTrue(topicPage.asNormalizedText().contains("Войдите"),
                "Должна быть подсказка «Войдите»");
    }

    @Test @Order(13)
    @DisplayName("Сц.13 — Авторизованный пользователь добавляет сообщение → оно появляется в теме")
    void testCreatePost() throws Exception {
        loginAs(REG_USER, REG_PASS);

        HtmlPage topicPage = get("/topic/" + topicId);
        HtmlPage result = submitForm(topicPage, "content", POST_TEXT);

        assertTrue(result.asNormalizedText().contains(POST_TEXT),
                "Текст нового сообщения должен быть виден на странице темы");
        assertTrue(result.asNormalizedText().contains(REG_USER),
                "Имя автора должно быть видно рядом с сообщением");
    }

    @Test @Order(14)
    @DisplayName("Сц.14 — Поиск по теме находит тему в результатах")
    void testSearch() throws Exception {
        HtmlPage searchPage = get("/search");
        HtmlPage result = submitForm(searchPage, "q", TOPIC_TITLE);

        assertTrue(result.asNormalizedText().contains(TOPIC_TITLE),
                "Тема должна появиться в результатах поиска");
    }

    @Test @Order(15)
    @DisplayName("Сц.15 — Администратор открывает панель управления → видит всех пользователей")
    void testAdminViewsUserList() throws Exception {
        loginAs("admin", "admin");

        HtmlPage page = get("/admin/users");
        assertEquals(200, page.getWebResponse().getStatusCode(),
                "Страница должна вернуть HTTP 200");
        assertTrue(page.asNormalizedText().contains("admin"),
                "Список должен содержать admin");
        assertTrue(page.asNormalizedText().contains(REG_USER),
                "Список должен содержать зарегистрированного пользователя");
    }

    @Test @Order(16)
    @DisplayName("Сц.16 — Обычный пользователь открывает /admin/users → получает 403")
    void testRegularUserCannotAccessAdminPanel() throws Exception {
        loginAs(REG_USER, REG_PASS);

        HtmlPage page = get("/admin/users");
        assertEquals(403, page.getWebResponse().getStatusCode(),
                "Обычный пользователь должен получить HTTP 403");
        assertTrue(page.asNormalizedText().contains("403") ||
                   page.asNormalizedText().contains("запрещён"),
                "Должна отображаться страница ошибки доступа");
    }
}
