package ru.forum.service;

import org.springframework.stereotype.Service;
import ru.forum.dao.PostDao;
import ru.forum.model.Post;
import ru.forum.model.Topic;
import ru.forum.model.User;
import ru.forum.util.HibernateUtil;

import org.hibernate.Session;
import org.hibernate.query.Query;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    private final PostDao postDao = new PostDao();

    /** Загружает сообщения вместе с автором (JOIN FETCH) для отображения в JSP. */
    public List<Post> findByTopicId(Long topicId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Post> q = session.createQuery(
                    "SELECT p FROM Post p JOIN FETCH p.user WHERE p.topic.id = :id ORDER BY p.createdAt",
                    Post.class);
            q.setParameter("id", topicId);
            return q.list();
        }
    }

    /** Загружает сообщение вместе с автором и темой. */
    public Optional<Post> findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Post> q = session.createQuery(
                    "SELECT p FROM Post p JOIN FETCH p.user JOIN FETCH p.topic WHERE p.id = :id",
                    Post.class);
            q.setParameter("id", id);
            return q.uniqueResultOptional();
        }
    }

    public Post create(String content, Topic topic, User author) {
        Post post = new Post();
        post.setContent(content);
        post.setTopic(topic);
        post.setUser(author);
        return postDao.save(post);
    }

    public Post update(Long id, String content) {
        Post post = postDao.findById(id);
        if (post == null) {
            throw new IllegalArgumentException("Post not found: " + id);
        }
        post.setContent(content);
        post.setUpdatedAt(LocalDateTime.now());
        return postDao.update(post);
    }

    public void delete(Long id) {
        Post p = postDao.findById(id);
        if (p != null) {
            postDao.delete(p);
        }
    }
}
