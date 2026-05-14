package ru.forum.service;

import org.springframework.stereotype.Service;
import ru.forum.dao.PostDao;
import ru.forum.model.Post;
import ru.forum.model.Topic;
import ru.forum.model.User;
import ru.forum.util.HibernateUtil;

import org.hibernate.Session;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    private final PostDao postDao = new PostDao();

    public List<Post> findByTopicId(Long topicId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "SELECT p FROM Post p JOIN FETCH p.user WHERE p.topic.id = :id ORDER BY p.createdAt",
                    Post.class)
                    .setParameter("id", topicId)
                    .list();
        }
    }

    public Optional<Post> findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "SELECT p FROM Post p JOIN FETCH p.user JOIN FETCH p.topic WHERE p.id = :id",
                    Post.class)
                    .setParameter("id", id)
                    .uniqueResultOptional();
        }
    }

    public List<Post> search(String keyword) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "SELECT p FROM Post p JOIN FETCH p.user JOIN FETCH p.topic " +
                    "WHERE LOWER(p.content) LIKE :kw ORDER BY p.createdAt DESC",
                    Post.class)
                    .setParameter("kw", "%" + keyword.toLowerCase() + "%")
                    .list();
        }
    }

    public long countByUserId(Long userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Long count = session.createQuery(
                    "SELECT COUNT(p) FROM Post p WHERE p.user.id = :id", Long.class)
                    .setParameter("id", userId)
                    .uniqueResult();
            return count != null ? count : 0;
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
        if (post == null) throw new IllegalArgumentException("Post not found: " + id);
        post.setContent(content);
        post.setUpdatedAt(LocalDateTime.now());
        return postDao.update(post);
    }

    public void delete(Long id) {
        Post p = postDao.findById(id);
        if (p != null) postDao.delete(p);
    }
}
