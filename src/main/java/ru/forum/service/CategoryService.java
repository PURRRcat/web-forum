package ru.forum.service;

import org.springframework.stereotype.Service;
import ru.forum.dao.CategoryDao;
import ru.forum.model.Category;
import ru.forum.model.User;
import ru.forum.util.HibernateUtil;

import org.hibernate.Session;
import org.hibernate.query.Query;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    private final CategoryDao categoryDao = new CategoryDao();

    public List<Category> findAll() {
        return categoryDao.findAll();
    }

    /** Загружает категорию вместе с модератором (избегает LazyInitializationException в JSP). */
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
        if (c != null) {
            categoryDao.delete(c);
        }
    }
}
