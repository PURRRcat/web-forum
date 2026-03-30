package ru.forum.dao;

import org.hibernate.Session;
import org.hibernate.query.Query;
import ru.forum.model.Category;
import ru.forum.util.HibernateUtil;

import java.util.List;

public class CategoryDao extends GenericDao<Category, Long> {

    public CategoryDao() {
        super(Category.class);
    }

    public List<Category> findByUserId(Long userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Category> query = session.createQuery("from Category where user.id = :userId", Category.class);
            query.setParameter("userId", userId);
            return query.list();
        }
    }
}
