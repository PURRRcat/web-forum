package ru.forum.web.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.forum.service.UserService;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired private UserService userService;

    @GetMapping("/users")
    public String users(Model model, HttpSession session, HttpServletResponse response) {
        if (!"admin".equals(session.getAttribute("role"))) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return "error/403";
        }
        model.addAttribute("users", userService.findAll());
        return "admin/users";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id,
                             HttpSession session, HttpServletResponse response) {
        if (!"admin".equals(session.getAttribute("role"))) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return "error/403";
        }
        userService.delete(id);
        return "redirect:/admin/users";
    }
}
