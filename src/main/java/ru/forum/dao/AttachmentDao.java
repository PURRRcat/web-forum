package ru.forum.dao;

import org.hibernate.Session;
import org.hibernate.query.Query;
import ru.forum.model.Attachment;
import ru.forum.util.HibernateUtil;

import java.util.List;

public class AttachmentDao extends GenericDao<Attachment, Long> {

    public AttachmentDao() {
        super(Attachment.class);
    }

    public List<Attachment> findByPostId(Long postId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Attachment> query = session.createQuery("from Attachment where post.id = :postId", Attachment.class);
            query.setParameter("postId", postId);
            return query.list();
        }
    }
}
