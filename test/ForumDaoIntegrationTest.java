import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.forum.dao.*;
import ru.forum.model.*;
import ru.forum.util.HibernateUtil;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ForumDaoIntegrationTest {

    private final UserDao userDao = new UserDao();
    private final CategoryDao categoryDao = new CategoryDao();
    private final TopicDao topicDao = new TopicDao();
    private final PostDao postDao = new PostDao();
    private final PostViewDao postViewDao = new PostViewDao();
    private final AttachmentDao attachmentDao = new AttachmentDao();

    @BeforeEach
    public void cleanUp() {
        try (org.hibernate.Session session = HibernateUtil.getSessionFactory().openSession()) {
            var tx = session.beginTransaction();
            session.createNativeQuery("DELETE FROM post_views").executeUpdate();
            session.createNativeQuery("DELETE FROM attachment").executeUpdate();
            session.createNativeQuery("DELETE FROM posts").executeUpdate();
            session.createNativeQuery("DELETE FROM topics").executeUpdate();
            session.createNativeQuery("DELETE FROM categories").executeUpdate();
            session.createNativeQuery("DELETE FROM users").executeUpdate();
            tx.commit();
        }
    }

    @AfterAll
    public static void tearDown() {
        HibernateUtil.shutdown();
    }

    @Test
    public void testUserCrudAndLookup() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("testuser@mail.com");
        user.setPasswordHash("hashpwd");
        user.setRole("user");

        userDao.save(user);
        assertNotNull(user.getId());

        User loaded = userDao.findById(user.getId());
        assertEquals("testuser", loaded.getUsername());

        assertTrue(userDao.findByUsername("testuser").isPresent());
        assertTrue(userDao.findByEmail("testuser@mail.com").isPresent());
    }

    @Test
    public void testCategoryTopicPostFlow() {
        User user = new User();
        user.setUsername("ivan");
        user.setEmail("ivan@mail.com");
        user.setPasswordHash("hash");
        user.setRole("user");
        userDao.save(user);

        Category category = new Category();
        category.setTitle("Программирование");
        category.setDescription("Вопросы по разработке");
        category.setUser(user);
        categoryDao.save(category);

        Topic topic = new Topic();
        topic.setTitle("Вопрос по SQL");
        topic.setCategory(category);
        topic.setUser(user);
        topic.setStatus("normal");
        topicDao.save(topic);

        Post rootPost = new Post();
        rootPost.setTopic(topic);
        rootPost.setUser(user);
        rootPost.setContent("Как работает JOIN?");
        postDao.save(rootPost);

        Post reply = new Post();
        reply.setTopic(topic);
        reply.setUser(user);
        reply.setParent(rootPost);
        reply.setContent("Нужно использовать ON условие");
        postDao.save(reply);

        List<Topic> topics = topicDao.findByCategoryId(category.getId());
        assertEquals(1, topics.size());

        List<Post> posts = postDao.findByTopicId(topic.getId());
        assertEquals(2, posts.size());
    }

    @Test
    public void testPostViewAndAttachment() {
        User user = new User();
        user.setUsername("anna");
        user.setEmail("anna@mail.com");
        user.setPasswordHash("hash");
        user.setRole("user");
        userDao.save(user);

        Category category = new Category();
        category.setTitle("Общий раздел");
        category.setUser(user);
        categoryDao.save(category);

        Topic topic = new Topic();
        topic.setTitle("Общая тема");
        topic.setUser(user);
        topic.setCategory(category);
        topic.setStatus("normal");
        topicDao.save(topic);

        Post post = new Post();
        post.setTopic(topic);
        post.setUser(user);
        post.setContent("Hello");
        postDao.save(post);

        Attachment attachment = new Attachment();
        attachment.setPost(post);
        attachment.setPathToFile("/files/image1.png");
        attachment.setType("image");
        attachmentDao.save(attachment);

        PostView view = new PostView();
        view.setPost(post);
        view.setUser(user);
        view.setId(new PostViewId(post.getId(), user.getId()));
        postViewDao.save(view);

        assertEquals(1, attachmentDao.findByPostId(post.getId()).size());
        assertEquals(1, postViewDao.findByUserId(user.getId()).size());
    }
}
