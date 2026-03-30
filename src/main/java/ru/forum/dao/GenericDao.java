package ru.forum.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import ru.forum.util.HibernateUtil;

import java.io.Serializable;
import java.util.List;
import java.util.function.Function;

public class GenericDao<T, ID extends Serializable> {

    private final Class<T> type;

    public GenericDao(Class<T> type) {
        this.type = type;
    }

    public T save(T entity) {
        return runInTransaction(session -> session.merge(entity));
    }

    public T update(T entity) {
        return runInTransaction(session -> session.merge(entity));
    }

    public void delete(T entity) {
        runInTransaction(session -> {
            session.remove(entity);
            return null;
        });
    }

    public T findById(ID id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(type, id);
        }
    }

    public List<T> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            var cb = session.getCriteriaBuilder();
            var query = cb.createQuery(type);
            query.from(type);
            return session.createQuery(query).getResultList();
        }
    }

    protected <R> R runInTransaction(Function<Session, R> work) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            R result = work.apply(session);
            tx.commit();
            return result;
        } catch (RuntimeException ex) {
            if (tx != null) tx.rollback();
            throw ex;
        }
    }
}
