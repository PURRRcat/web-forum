package ru.forum.service;

import org.springframework.stereotype.Service;
import ru.forum.dao.CategoryDao;
import ru.forum.model.Category;
import ru.forum.model.User;
import ru.forum.service.dto.CategoryStats;
import ru.forum.util.HibernateUtil;

import org.hibernate.Session;
import org.hibernate.query.Query;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    private final CategoryDao categoryDao = new CategoryDao();

    public List<Category> findAll() {
        return categoryDao.findAll();
    }

    public List<CategoryStats> findAllWithStats() {
        List<Category> categories = categoryDao.findAll();
        List<CategoryStats> result = new ArrayList<>();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            for (Category cat : categories) {
                Long topicCount = session.createQuery(
                        "SELECT COUNT(t) FROM Topic t WHERE t.category.id = :id", Long.class)
                        .setParameter("id", cat.getId())
                        .uniqueResult();

                Long postCount = session.createQuery(
                        "SELECT COUNT(p) FROM Post p WHERE p.topic.category.id = :id", Long.class)
                        .setParameter("id", cat.getId())
                        .uniqueResult();

                Object[] last = session.createQuery(
                        "SELECT p.user.username, p.createdAt FROM Post p " +
                        "WHERE p.topic.category.id = :id ORDER BY p.createdAt DESC",
                        Object[].class)
                        .setParameter("id", cat.getId())
                        .setMaxResults(1)
                        .uniqueResult();

                String lastAuthor = last != null ? (String) last[0] : null;
                LocalDateTime lastPostAt = last != null ? (LocalDateTime) last[1] : null;

                result.add(new CategoryStats(cat,
                        topicCount != null ? topicCount : 0,
                        postCount  != null ? postCount  : 0,
                        lastAuthor, lastPostAt));
            }
        }
        return result;
    }

    public Optional<Category> findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Category> q = session.createQuery(
                    "SELECT c FROM Category c JOIN FETCH c.user WHERE c.id = :id", Category.class);
            q.setParameter("id", id);
            return q.uniqueResultOptional();
        }
    }

    public Category create(String title, String description, User moderator) {
        Category cat = new Category();
        cat.setTitle(title);
        cat.setDescription(description);
        cat.setUser(moderator);
        return categoryDao.save(cat);
    }

    public void delete(Long id) {
        Category c = categoryDao.findById(id);
        if (c != null) categoryDao.delete(c);
    }
}
