package ru.forum.service;

import org.springframework.stereotype.Service;
import ru.forum.dao.UserDao;
import ru.forum.model.User;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserDao userDao = new UserDao();

    public Optional<User> login(String username, String password) {
        String hash = hashPassword(password);
        return userDao.findByUsername(username)
                .filter(u -> u.getPasswordHash().equals(hash));
    }

    public User register(String username, String email, String password, String confirmPassword) {
        if (!password.equals(confirmPassword))        throw new IllegalArgumentException("passwords_mismatch");
        if (userDao.findByUsername(username).isPresent()) throw new IllegalArgumentException("username_exists");
        if (userDao.findByEmail(email).isPresent())   throw new IllegalArgumentException("email_exists");
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(hashPassword(password));
        return userDao.save(user);
    }

    public void updateProfile(Long id, String about, String avatarPath) {
        User user = userDao.findById(id);
        if (user == null) return;
        user.setAbout(about);
        user.setAvatarPath(avatarPath);
        userDao.update(user);
    }

    public String changePassword(Long id, String oldPassword, String newPassword, String confirmNew) {
        User user = userDao.findById(id);
        if (user == null) return "user_not_found";
        if (!user.getPasswordHash().equals(hashPassword(oldPassword))) return "wrong_password";
        if (!newPassword.equals(confirmNew)) return "passwords_mismatch";
        user.setPasswordHash(hashPassword(newPassword));
        userDao.update(user);
        return null;
    }

    public List<User> findAll() {
        return userDao.findAll();
    }

    public Optional<User> findById(Long id) {
        return Optional.ofNullable(userDao.findById(id));
    }

    public void delete(Long id) {
        User u = userDao.findById(id);
        if (u != null) userDao.delete(u);
    }

    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
