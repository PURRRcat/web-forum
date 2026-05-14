package ru.forum.util;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
    private static volatile SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            Configuration configuration = new Configuration();
            configuration.configure("hibernate.cfg.xml");

            String urlOverride = System.getProperty("hibernate.connection.url");
            if (urlOverride != null) {
                configuration.setProperty("hibernate.connection.url", urlOverride);
            }

            configuration.addAnnotatedClass(ru.forum.model.User.class);
            configuration.addAnnotatedClass(ru.forum.model.Category.class);
            configuration.addAnnotatedClass(ru.forum.model.Topic.class);
            configuration.addAnnotatedClass(ru.forum.model.Post.class);
            configuration.addAnnotatedClass(ru.forum.model.PostView.class);
            configuration.addAnnotatedClass(ru.forum.model.Attachment.class);

            StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties());
            return configuration.buildSessionFactory(builder.build());
        } catch (Throwable ex) {
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null || sessionFactory.isClosed()) {
            synchronized (HibernateUtil.class) {
                if (sessionFactory == null || sessionFactory.isClosed()) {
                    sessionFactory = buildSessionFactory();
                }
            }
        }
        return sessionFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
        }
    }
}
