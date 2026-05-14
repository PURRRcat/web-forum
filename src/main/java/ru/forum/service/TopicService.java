package ru.forum.service;

import org.springframework.stereotype.Service;
import ru.forum.dao.TopicDao;
import ru.forum.model.Category;
import ru.forum.model.Topic;
import ru.forum.model.User;
import ru.forum.util.HibernateUtil;

import org.hibernate.Session;
import java.util.List;
import java.util.Optional;

@Service
public class TopicService {

    private final TopicDao topicDao = new TopicDao();

    public List<Topic> findByCategoryId(Long categoryId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "SELECT t FROM Topic t JOIN FETCH t.user WHERE t.category.id = :id ORDER BY t.createdAt DESC",
                    Topic.class)
                    .setParameter("id", categoryId)
                    .list();
        }
    }

    public Optional<Topic> findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "SELECT t FROM Topic t JOIN FETCH t.user JOIN FETCH t.category WHERE t.id = :id",
                    Topic.class)
                    .setParameter("id", id)
                    .uniqueResultOptional();
        }
    }

    public List<Topic> findByUserId(Long userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "SELECT t FROM Topic t JOIN FETCH t.category WHERE t.user.id = :id ORDER BY t.createdAt DESC",
                    Topic.class)
                    .setParameter("id", userId)
                    .list();
        }
    }

    public List<Topic> search(String keyword) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "SELECT DISTINCT t FROM Topic t JOIN FETCH t.user JOIN FETCH t.category " +
                    "WHERE LOWER(t.title) LIKE :kw ORDER BY t.createdAt DESC",
                    Topic.class)
                    .setParameter("kw", "%" + keyword.toLowerCase() + "%")
                    .list();
        }
    }

    public List<Topic> findUnread(Long userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "SELECT DISTINCT t FROM Topic t JOIN FETCH t.user JOIN FETCH t.category " +
                    "JOIN t.posts p WHERE p.id NOT IN " +
                    "  (SELECT pv.post.id FROM PostView pv WHERE pv.user.id = :uid) " +
                    "ORDER BY t.createdAt DESC",
                    Topic.class)
                    .setParameter("uid", userId)
                    .list();
        }
    }

    public Topic create(String title, Category category, User author) {
        Topic topic = new Topic();
        topic.setTitle(title);
        topic.setCategory(category);
        topic.setUser(author);
        return topicDao.save(topic);
    }

    public void delete(Long id) {
        Topic t = topicDao.findById(id);
        if (t != null) topicDao.delete(t);
    }
}
