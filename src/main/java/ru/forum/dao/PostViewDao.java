package ru.forum.dao;

import org.hibernate.Session;
import org.hibernate.query.Query;
import ru.forum.model.PostView;
import ru.forum.util.HibernateUtil;

import java.util.List;

public class PostViewDao extends GenericDao<PostView, ru.forum.model.PostViewId> {

    public PostViewDao() {
        super(PostView.class);
    }

    public List<PostView> findByUserId(Long userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<PostView> query = session.createQuery("from PostView where user.id = :userId", PostView.class);
            query.setParameter("userId", userId);
            return query.list();
        }
    }
}
