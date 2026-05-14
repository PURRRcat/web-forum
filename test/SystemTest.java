import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.forum.dao.*;
import ru.forum.model.*;
import ru.forum.service.UserService;
import ru.forum.util.HibernateUtil;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Системные тесты Web-интерфейса форума.
 *
 * Используется Spring MockMvc — стандартный инструмент тестирования
 * Spring MVC, аналогичный HTTPUnit: отправляет HTTP-запросы напрямую
 * DispatcherServlet'у без запуска реального сервера.
 *
 * Покрытые варианты использования и результаты:
 Главная страница
 Регистрация: успех / занятое имя / занятый e-mail / несовпадение паролей
 Вход: успех / неверный пароль / пользователь не найден
 Просмотр раздела
 Создание темы: успех / без авторизации
 Просмотр темы
 Добавление сообщения: успех / без авторизации
 Панель администратора: успех / доступ запрещён
 */
@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextHierarchy({
        @ContextConfiguration("classpath:applicationContext.xml"),
        @ContextConfiguration("classpath:spring-mvc.xml")
})
class SystemTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    // ── Инициализация ────────────────────────────────────────────────────────

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        clearDatabase();
    }

    /** Очищает все таблицы перед каждым тестом (в обратном порядке FK). */
    private void clearDatabase() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.createNativeMutationQuery("DELETE FROM post_views").executeUpdate();
            session.createNativeMutationQuery("DELETE FROM attachment").executeUpdate();
            session.createNativeMutationQuery("UPDATE posts SET parent_id = NULL").executeUpdate();
            session.createNativeMutationQuery("DELETE FROM posts").executeUpdate();
            session.createNativeMutationQuery("DELETE FROM topics").executeUpdate();
            session.createNativeMutationQuery("DELETE FROM categories").executeUpdate();
            session.createNativeMutationQuery("DELETE FROM users").executeUpdate();
            tx.commit();
        }
    }

    // ── Вспомогательные фабричные методы ────────────────────────────────────

    /**
     * SHA-256("password") — хэш тестового пароля «password».
     * Используется во всех тестах для создания пользователей.
     */
    private static final String PASSWORD_HASH =
            "5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8";

    private User createUser(String username, String email, String role) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(PASSWORD_HASH);
        user.setRole(role);
        return new UserDao().save(user);
    }

    private Category createCategory(String title, User moderator) {
        Category cat = new Category();
        cat.setTitle(title);
        cat.setDescription("Тестовый раздел");
        cat.setUser(moderator);
        return new CategoryDao().save(cat);
    }

    private Topic createTopic(String title, Category category, User author) {
        Topic topic = new Topic();
        topic.setTitle(title);
        topic.setCategory(category);
        topic.setUser(author);
        return new TopicDao().save(topic);
    }

    private MockHttpSession sessionFor(User user) {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userId", user.getId());
        session.setAttribute("username", user.getUsername());
        session.setAttribute("role", user.getRole());
        return session;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // Главная страница
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Главная страница отображает список разделов")
    void testHomePageShowsCategories() throws Exception {
        User admin = createUser("admin", "admin@test.com", "admin");
        createCategory("Тестовый раздел", admin);

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attributeExists("categories"));
    }

    // ═══════════════════════════════════════════════════════════════════════
    // Регистрация
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Успешная регистрация нового пользователя")
    void testRegisterSuccess() throws Exception {
        mockMvc.perform(post("/user/register")
                        .param("username", "newuser")
                        .param("email", "newuser@test.com")
                        .param("password", "password123")
                        .param("confirmPassword", "password123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/login"));

        Assertions.assertTrue(new UserDao().findByUsername("newuser").isPresent(),
                "Пользователь должен быть сохранён в БД");
    }

    @Test
    @DisplayName("Регистрация с уже занятым именем пользователя")
    void testRegisterFailUsernameExists() throws Exception {
        createUser("existinguser", "existing@test.com", "user");

        mockMvc.perform(post("/user/register")
                        .param("username", "existinguser")
                        .param("email", "other@test.com")
                        .param("password", "password123")
                        .param("confirmPassword", "password123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/register"));
    }

    @Test
    @DisplayName("Регистрация с уже зарегистрированным e-mail")
    void testRegisterFailEmailExists() throws Exception {
        createUser("user1", "taken@test.com", "user");

        mockMvc.perform(post("/user/register")
                        .param("username", "newuser2")
                        .param("email", "taken@test.com")
                        .param("password", "password123")
                        .param("confirmPassword", "password123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/register"));
    }

    @Test
    @DisplayName("Регистрация при несовпадении паролей")
    void testRegisterFailPasswordMismatch() throws Exception {
        mockMvc.perform(post("/user/register")
                        .param("username", "user3")
                        .param("email", "user3@test.com")
                        .param("password", "password123")
                        .param("confirmPassword", "different"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/register"));
    }

    // ═══════════════════════════════════════════════════════════════════════
    // Вход в систему
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Успешный вход с корректными учётными данными")
    void testLoginSuccess() throws Exception {
        createUser("testuser", "test@test.com", "user");

        mockMvc.perform(post("/user/login")
                        .param("username", "testuser")
                        .param("password", "password"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    @DisplayName("Вход с неверным паролем")
    void testLoginFailInvalidPassword() throws Exception {
        createUser("testuser2", "test2@test.com", "user");

        mockMvc.perform(post("/user/login")
                        .param("username", "testuser2")
                        .param("password", "wrongpassword"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/login"));
    }

    @Test
    @DisplayName("Вход с несуществующим именем пользователя")
    void testLoginFailUserNotFound() throws Exception {
        mockMvc.perform(post("/user/login")
                        .param("username", "nonexistent")
                        .param("password", "password"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/login"));
    }

    // ═══════════════════════════════════════════════════════════════════════
    // Просмотр раздела
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Просмотр раздела со списком тем")
    void testViewCategory() throws Exception {
        User admin = createUser("admin", "admin@test.com", "admin");
        Category cat = createCategory("Программирование", admin);
        createTopic("Первая тема", cat, admin);

        mockMvc.perform(get("/category/" + cat.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("category"))
                .andExpect(model().attributeExists("category", "topics"));
    }

    // ═══════════════════════════════════════════════════════════════════════
    // Создание темы
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Авторизованный пользователь создаёт тему")
    void testCreateTopicSuccess() throws Exception {
        User admin = createUser("admin", "admin@test.com", "admin");
        Category cat = createCategory("Раздел", admin);

        mockMvc.perform(post("/topic/new/" + cat.getId())
                        .param("title", "Новая тема от теста")
                        .session(sessionFor(admin)))
                .andExpect(status().is3xxRedirection());

        Assertions.assertFalse(new TopicDao().findByCategoryId(cat.getId()).isEmpty(),
                "Тема должна быть сохранена в БД");
    }

    @Test
    @DisplayName("Неаутентифицированный запрос перенаправляется на страницу входа")
    void testCreateTopicRedirectsToLoginWhenNotAuthenticated() throws Exception {
        User admin = createUser("admin", "admin@test.com", "admin");
        Category cat = createCategory("Раздел", admin);

        mockMvc.perform(get("/topic/new/" + cat.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/login"));
    }

    // ═══════════════════════════════════════════════════════════════════════
    // Просмотр темы
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Просмотр темы со списком сообщений")
    void testViewTopic() throws Exception {
        User admin = createUser("admin", "admin@test.com", "admin");
        Category cat = createCategory("Раздел", admin);
        Topic topic = createTopic("Тема для просмотра", cat, admin);

        mockMvc.perform(get("/topic/" + topic.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("topic"))
                .andExpect(model().attributeExists("topic", "posts"));
    }

    // ═══════════════════════════════════════════════════════════════════════
    // Добавление сообщения
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Авторизованный пользователь добавляет сообщение")
    void testCreatePostSuccess() throws Exception {
        User admin = createUser("admin", "admin@test.com", "admin");
        Category cat = createCategory("Раздел", admin);
        Topic topic = createTopic("Тема", cat, admin);

        mockMvc.perform(post("/post/new/" + topic.getId())
                        .param("content", "Текст тестового сообщения")
                        .session(sessionFor(admin)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/topic/" + topic.getId()));

        Assertions.assertFalse(new PostDao().findByTopicId(topic.getId()).isEmpty(),
                "Сообщение должно быть сохранено в БД");
    }

    @Test
    @DisplayName("Неаутентифицированный запрос перенаправляется на страницу входа")
    void testCreatePostRedirectsToLoginWhenNotAuthenticated() throws Exception {
        User admin = createUser("admin", "admin@test.com", "admin");
        Category cat = createCategory("Раздел", admin);
        Topic topic = createTopic("Тема", cat, admin);

        mockMvc.perform(post("/post/new/" + topic.getId())
                        .param("content", "Текст"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/login"));
    }

    // ═══════════════════════════════════════════════════════════════════════
    // Панель администратора
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Администратор видит список всех пользователей")
    void testAdminViewUsers() throws Exception {
        User admin = createUser("admin", "admin@test.com", "admin");
        createUser("user1", "user1@test.com", "user");

        mockMvc.perform(get("/admin/users").session(sessionFor(admin)))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/users"))
                .andExpect(model().attributeExists("users"));
    }

    @Test
    @DisplayName("Обычный пользователь получает отказ в доступе (403)")
    void testAdminAccessForbiddenForRegularUser() throws Exception {
        User regular = createUser("regular", "regular@test.com", "user");

        mockMvc.perform(get("/admin/users").session(sessionFor(regular)))
                .andExpect(status().isForbidden())
                .andExpect(view().name("error/403"));
    }
}
