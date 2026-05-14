package ru.forum.service;

import org.springframework.stereotype.Service;
import ru.forum.dao.TopicDao;
import ru.forum.model.Category;
import ru.forum.model.Topic;
import ru.forum.model.User;
import ru.forum.util.HibernateUtil;

import org.hibernate.Session;
import org.hibernate.query.Query;
import java.util.List;
import java.util.Optional;

@Service
public class TopicService {

    private final TopicDao topicDao = new TopicDao();

    /** Загружает темы вместе с автором (JOIN FETCH) для отображения в JSP. */
    public List<Topic> findByCategoryId(Long categoryId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Topic> q = session.createQuery(
                    "SELECT t FROM Topic t JOIN FETCH t.user WHERE t.category.id = :id ORDER BY t.createdAt DESC",
                    Topic.class);
            q.setParameter("id", categoryId);
            return q.list();
        }
    }

    /** Загружает тему вместе с автором и категорией. */
    public Optional<Topic> findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Topic> q = session.createQuery(
                    "SELECT t FROM Topic t JOIN FETCH t.user JOIN FETCH t.category WHERE t.id = :id",
                    Topic.class);
            q.setParameter("id", id);
            return q.uniqueResultOptional();
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
        if (t != null) {
            topicDao.delete(t);
        }
    }
}
