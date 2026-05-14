package ru.forum.service;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import ru.forum.dao.UserDao;
import ru.forum.model.User;

import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class DataSeeder implements ApplicationListener<ContextRefreshedEvent> {

    private static final AtomicBoolean ran = new AtomicBoolean(false);

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (!ran.compareAndSet(false, true)) return;

        UserDao userDao = new UserDao();
        if (userDao.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@forum.local");
            admin.setPasswordHash(UserService.hashPassword("admin"));
            admin.setRole("admin");
            userDao.save(admin);
        }
    }
}
