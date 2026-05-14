package ru.forum.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import ru.forum.dao.UserDao;
import ru.forum.model.User;

/**
 * Создаёт учётную запись администратора при первом старте,
 * если в базе ещё нет пользователей.
 *
 * Логин: admin  Пароль: admin
 */
@Component
public class DataSeeder implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private UserService userService;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
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
