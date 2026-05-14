package ru.forum.web.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.forum.model.User;
import ru.forum.service.PostService;
import ru.forum.service.TopicService;
import ru.forum.service.UserService;

import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired private UserService userService;
    @Autowired private TopicService topicService;
    @Autowired private PostService postService;

    @GetMapping("/login")
    public String loginForm() { return "user/login"; }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session, RedirectAttributes ra) {
        Optional<User> user = userService.login(username, password);
        if (user.isPresent()) {
            session.setAttribute("userId",   user.get().getId());
            session.setAttribute("username", user.get().getUsername());
            session.setAttribute("role",     user.get().getRole());
            return "redirect:/";
        }
        ra.addFlashAttribute("error", "Неверное имя пользователя или пароль.");
        return "redirect:/user/login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/register")
    public String registerForm() { return "user/register"; }

    @PostMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String email,
                           @RequestParam String password,
                           @RequestParam String confirmPassword,
                           RedirectAttributes ra) {
        try {
            userService.register(username, email, password, confirmPassword);
            ra.addFlashAttribute("success", "Регистрация выполнена. Войдите в систему.");
            return "redirect:/user/login";
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("error", translateError(e.getMessage()));
            ra.addFlashAttribute("username", username);
            ra.addFlashAttribute("email", email);
            return "redirect:/user/register";
        }
    }

    @GetMapping("/{id}")
    public String profile(@PathVariable Long id, Model model) {
        User user = userService.findById(id).orElseThrow();
        model.addAttribute("profileUser", user);
        model.addAttribute("userTopics",  topicService.findByUserId(id));
        model.addAttribute("postCount",   postService.countByUserId(id));
        return "user/profile";
    }

    @GetMapping("/cabinet")
    public String cabinetForm(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        model.addAttribute("profileUser", userService.findById(userId).orElseThrow());
        return "user/cabinet";
    }

    @PostMapping("/cabinet")
    public String cabinetSave(@RequestParam(defaultValue = "") String about,
                              @RequestParam(defaultValue = "") String avatarPath,
                              HttpSession session, RedirectAttributes ra) {
        Long userId = (Long) session.getAttribute("userId");
        userService.updateProfile(userId, about, avatarPath);
        ra.addFlashAttribute("success", "Профиль обновлён.");
        return "redirect:/user/cabinet";
    }

    @PostMapping("/cabinet/password")
    public String changePassword(@RequestParam String oldPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmNew,
                                 HttpSession session, RedirectAttributes ra) {
        Long userId = (Long) session.getAttribute("userId");
        String error = userService.changePassword(userId, oldPassword, newPassword, confirmNew);
        if (error != null) {
            ra.addFlashAttribute("pwError", translateError(error));
        } else {
            ra.addFlashAttribute("pwSuccess", "Пароль изменён.");
        }
        return "redirect:/user/cabinet";
    }

    @GetMapping("/unread")
    public String unread(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/user/login";
        model.addAttribute("topics", topicService.findUnread(userId));
        return "user/unread";
    }

    private String translateError(String code) {
        return switch (code) {
            case "username_exists"    -> "Имя пользователя уже занято.";
            case "email_exists"       -> "Этот e-mail уже зарегистрирован.";
            case "passwords_mismatch" -> "Пароли не совпадают.";
            case "wrong_password"     -> "Неверный текущий пароль.";
            default                   -> "Ошибка.";
        };
    }
}
