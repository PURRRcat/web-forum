package ru.forum.dao;

import org.hibernate.Session;
import org.hibernate.query.Query;
import ru.forum.model.Post;
import ru.forum.util.HibernateUtil;

import java.util.List;

public class PostDao extends GenericDao<Post, Long> {

    public PostDao() {
        super(Post.class);
    }

    public List<Post> findByTopicId(Long topicId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Post> query = session.createQuery("from Post where topic.id = :topicId", Post.class);
            query.setParameter("topicId", topicId);
            return query.list();
        }
    }
}
