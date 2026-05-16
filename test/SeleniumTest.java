import org.apache.catalina.startup.Tomcat;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.time.Duration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SeleniumTest {

    static final int PORT = 8081;
    static final String BASE = "http://localhost:" + PORT + "/forum";
    static final Duration WAIT_TIMEOUT = Duration.ofSeconds(5);
    static final List<String> CHROME_BINARIES = List.of(
            "/usr/bin/chromium",
            "/usr/bin/google-chrome",
            "/usr/bin/google-chrome-stable"
    );

    static Tomcat tomcat;

    static final String REG_USER = "ivan_" + System.nanoTime();
    static final String REG_EMAIL = REG_USER + "@test.com";
    static final String REG_PASS = "password";

    static final String CAT_TITLE = "Программирование_" + System.nanoTime();
    static final String TOPIC_TITLE = "Первая тема теста";
    static final String POST_TEXT = "Привет, это системный тест!";

    static Long categoryId;
    static Long topicId;

    WebDriver driver;
    WebDriverWait wait;

    @BeforeAll
    static void startServer() throws Exception {
        Logger.getLogger("org.apache").setLevel(Level.WARNING);
        configureChromeDriver();

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
        if (tomcat != null) {
            tomcat.stop();
        }
        System.out.println("[SeleniumTest] Сервер остановлен.");
    }

    @BeforeEach
    void setup() {
        ChromeOptions options = new ChromeOptions();
        String chromeBinary = resolveChromeBinary();
        if (chromeBinary != null) {
            options.setBinary(chromeBinary);
        }

        if (useHeadlessMode()) {
            options.addArguments("--headless=new");
        }

        options.addArguments("--window-size=1440,1100");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--no-sandbox");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, WAIT_TIMEOUT);
    }

    @AfterEach
    void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    private static void configureChromeDriver() {
        File chromeDriver = new File("/usr/bin/chromedriver");
        if (chromeDriver.canExecute()) {
            System.setProperty("webdriver.chrome.driver", chromeDriver.getAbsolutePath());
        }
    }

    private static String resolveChromeBinary() {
        for (String path : CHROME_BINARIES) {
            File binary = new File(path);
            if (binary.canExecute()) {
                return binary.getAbsolutePath();
            }
        }
        return null;
    }

    private static boolean useHeadlessMode() {
        String override = System.getProperty("ui.headless");
        if (override != null) {
            return Boolean.parseBoolean(override);
        }
        String display = System.getenv("DISPLAY");
        return display == null || display.isBlank();
    }

    private void open(String path) {
        driver.get(BASE + path);
        waitForPage();
    }

    private void waitForPage() {
        wait.until(driver -> "complete".equals(
                ((JavascriptExecutor) driver).executeScript("return document.readyState")));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
    }

    private String pageText() {
        return driver.findElement(By.tagName("body")).getText();
    }

    private void submitForm(String... nameValues) {
        WebElement form = findForm(nameValues);
        for (int i = 0; i < nameValues.length; i += 2) {
            WebElement field = form.findElement(By.name(nameValues[i]));
            field.clear();
            field.sendKeys(nameValues[i + 1]);
        }
        form.findElement(By.cssSelector("button[type='submit'],button:not([type])")).click();
        waitForPage();
    }

    private WebElement findForm(String... nameValues) {
        List<WebElement> forms = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.tagName("form")));
        for (WebElement form : forms) {
            boolean matches = true;
            for (int i = 0; i < nameValues.length; i += 2) {
                if (form.findElements(By.name(nameValues[i])).isEmpty()) {
                    matches = false;
                    break;
                }
            }
            if (matches) {
                return form;
            }
        }
        throw new IllegalStateException("Не найдена форма для полей: " + String.join(", ", nameValues));
    }

    private void loginAs(String username, String password) {
        open("/user/login");
        submitForm("username", username, "password", password);
    }

    @Test
    @Order(1)
    @DisplayName("Сц.1 — Гость открывает главную: заголовок «Форум», разделов нет")
    void testGuestViewsEmptyHomePage() {
        open("/");
        assertEquals("Форум", driver.getTitle());
        assertTrue(pageText().contains("Разделов пока нет"),
                "Ожидается сообщение об отсутствии разделов");
    }

    @Test
    @Order(2)
    @DisplayName("Сц.2 — Страница входа содержит форму с полями username и password")
    void testLoginPageAccessible() {
        open("/user/login");
        WebElement currentForm = findForm("username", "", "password", "");
        assertNotNull(currentForm.findElement(By.name("username")));
        assertNotNull(currentForm.findElement(By.name("password")));
    }

    @Test
    @Order(3)
    @DisplayName("Сц.3 — Регистрация: пароли не совпадают → форма с ошибкой")
    void testRegisterFailPasswordMismatch() {
        open("/user/register");
        submitForm(
                "username", REG_USER,
                "email", REG_EMAIL,
                "password", "abc123",
                "confirmPassword", "xyz999");
        assertTrue(driver.getCurrentUrl().contains("/user/register"),
                "Должен остаться на странице регистрации");
        assertTrue(pageText().contains("не совпадают"),
                "Должно быть сообщение о несовпадении паролей");
    }

    @Test
    @Order(4)
    @DisplayName("Сц.4 — Успешная регистрация → перенаправление на страницу входа")
    void testRegisterSuccess() {
        open("/user/register");
        submitForm(
                "username", REG_USER,
                "email", REG_EMAIL,
                "password", REG_PASS,
                "confirmPassword", REG_PASS);
        assertTrue(driver.getCurrentUrl().contains("/user/login"),
                "После регистрации должен быть редирект на /user/login");
        assertTrue(pageText().contains("Регистрация выполнена"),
                "Должно быть сообщение об успешной регистрации");
    }

    @Test
    @Order(5)
    @DisplayName("Сц.5 — Регистрация с занятым именем → ошибка «уже занято»")
    void testRegisterFailUsernameExists() {
        open("/user/register");
        submitForm(
                "username", REG_USER,
                "email", "other_" + REG_EMAIL,
                "password", REG_PASS,
                "confirmPassword", REG_PASS);
        assertTrue(driver.getCurrentUrl().contains("/user/register"),
                "Должен остаться на странице регистрации");
        assertTrue(pageText().contains("уже занято"),
                "Должно быть сообщение о занятом имени");
    }

    @Test
    @Order(6)
    @DisplayName("Сц.6 — Регистрация с занятым e-mail → ошибка «уже зарегистрирован»")
    void testRegisterFailEmailExists() {
        open("/user/register");
        submitForm(
                "username", "other_" + REG_USER,
                "email", REG_EMAIL,
                "password", REG_PASS,
                "confirmPassword", REG_PASS);
        assertTrue(driver.getCurrentUrl().contains("/user/register"),
                "Должен остаться на странице регистрации");
        assertTrue(pageText().contains("уже зарегистрирован"),
                "Должно быть сообщение о занятом e-mail");
    }

    @Test
    @Order(7)
    @DisplayName("Сц.7 — Вход с неверным паролем → сообщение об ошибке")
    void testLoginFailWrongPassword() {
        loginAs(REG_USER, "wrongpassword");
        assertTrue(driver.getCurrentUrl().contains("/user/login"),
                "Должен остаться на странице входа");
        assertTrue(pageText().contains("Неверное"),
                "Должно быть сообщение об ошибке");
    }

    @Test
    @Order(8)
    @DisplayName("Сц.8 — Успешный вход → главная страница с именем пользователя")
    void testLoginSuccess() {
        loginAs(REG_USER, REG_PASS);
        assertTrue(driver.getCurrentUrl().endsWith("/forum/")
                        || driver.getCurrentUrl().endsWith("/forum"),
                "После входа должна открыться главная страница");
        assertTrue(pageText().contains(REG_USER),
                "Имя пользователя должно быть видно в навбаре");
    }

    @Test
    @Order(9)
    @DisplayName("Сц.9 — Администратор создаёт раздел → он виден на главной странице")
    void testAdminCreatesCategory() {
        loginAs("admin", "admin");

        open("/category/new");
        submitForm(
                "title", CAT_TITLE,
                "description", "Тестовый раздел для системного теста");

        open("/");
        assertTrue(pageText().contains(CAT_TITLE),
                "Новый раздел должен быть виден на главной странице");

        WebElement catLink = driver.findElement(By.xpath("//a[contains(text(),'" + CAT_TITLE + "')]"));
        String href = catLink.getAttribute("href");
        assertNotNull(href, "Должна быть ссылка на раздел");
        categoryId = Long.parseLong(href.replaceAll(".*/category/(\\d+).*", "$1"));
    }

    @Test
    @Order(10)
    @DisplayName("Сц.10 — Гость пытается создать тему → редирект на страницу входа")
    void testUnauthenticatedCannotCreateTopic() {
        open("/topic/new/" + categoryId);
        assertTrue(driver.getCurrentUrl().contains("/user/login"),
                "Неаутентифицированный пользователь должен быть перенаправлен на вход");
    }

    @Test
    @Order(11)
    @DisplayName("Сц.11 — Авторизованный пользователь создаёт тему → открывается страница темы")
    void testCreateTopic() {
        loginAs(REG_USER, REG_PASS);

        open("/topic/new/" + categoryId);
        submitForm(
                "title", TOPIC_TITLE,
                "content", "Первое сообщение темы");

        assertTrue(pageText().contains(TOPIC_TITLE),
                "Заголовок темы должен быть виден на странице темы");

        String url = driver.getCurrentUrl();
        topicId = Long.parseLong(url.replaceAll(".*/topic/(\\d+).*", "$1"));
    }

    @Test
    @Order(12)
    @DisplayName("Сц.12 — Гость пытается отправить сообщение → редирект на вход")
    void testUnauthenticatedCannotPost() {
        open("/topic/" + topicId);
        assertFalse(pageText().contains("Добавить сообщение"),
                "Форма добавления сообщения не должна быть видна гостю");
        assertTrue(pageText().contains("Войдите"),
                "Должна быть подсказка «Войдите»");
    }

    @Test
    @Order(13)
    @DisplayName("Сц.13 — Авторизованный пользователь добавляет сообщение → оно появляется в теме")
    void testCreatePost() {
        loginAs(REG_USER, REG_PASS);

        open("/topic/" + topicId);
        submitForm("content", POST_TEXT);

        assertTrue(pageText().contains(POST_TEXT),
                "Текст нового сообщения должен быть виден на странице темы");
        assertTrue(pageText().contains(REG_USER),
                "Имя автора должно быть видно рядом с сообщением");
    }

    @Test
    @Order(14)
    @DisplayName("Сц.14 — Поиск по теме находит тему в результатах")
    void testSearch() {
        open("/search");
        submitForm("q", TOPIC_TITLE);

        assertTrue(pageText().contains(TOPIC_TITLE),
                "Тема должна появиться в результатах поиска");
    }

    @Test
    @Order(15)
    @DisplayName("Сц.15 — Администратор открывает панель управления → видит всех пользователей")
    void testAdminViewsUserList() {
        loginAs("admin", "admin");

        open("/admin/users");
        assertTrue(pageText().contains("admin"),
                "Список должен содержать admin");
        assertTrue(pageText().contains(REG_USER),
                "Список должен содержать зарегистрированного пользователя");
    }

    @Test
    @Order(16)
    @DisplayName("Сц.16 — Обычный пользователь открывает /admin/users → получает 403")
    void testRegularUserCannotAccessAdminPanel() {
        loginAs(REG_USER, REG_PASS);

        open("/admin/users");
        assertTrue(pageText().contains("403") || pageText().contains("запрещ"),
                "Должна отображаться страница ошибки доступа");
    }
}
