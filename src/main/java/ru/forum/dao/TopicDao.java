package ru.forum.dao;

import org.hibernate.Session;
import org.hibernate.query.Query;
import ru.forum.model.Topic;
import ru.forum.util.HibernateUtil;

import java.util.List;

public class TopicDao extends GenericDao<Topic, Long> {

    public TopicDao() {
        super(Topic.class);
    }

    public List<Topic> findByCategoryId(Long categoryId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Topic> query = session.createQuery("from Topic where category.id = :categoryId", Topic.class);
            query.setParameter("categoryId", categoryId);
            return query.list();
        }
    }
}
