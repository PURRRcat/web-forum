package ru.forum.web.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.forum.model.User;
import ru.forum.service.CategoryService;
import ru.forum.service.TopicService;
import ru.forum.service.UserService;

@Controller
@RequestMapping("/category")
public class CategoryController {

    @Autowired private CategoryService categoryService;
    @Autowired private TopicService topicService;
    @Autowired private UserService userService;

    @GetMapping("/{id}")
    public String view(@PathVariable Long id, Model model) {
        model.addAttribute("category", categoryService.findById(id).orElseThrow());
        model.addAttribute("topics", topicService.findByCategoryId(id));
        return "category";
    }

    @GetMapping("/new")
    public String newForm(HttpSession session, HttpServletResponse response) {
        if (!canManage(session)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return "error/403";
        }
        return "category/new";
    }

    @PostMapping("/new")
    public String create(@RequestParam String title,
                         @RequestParam(defaultValue = "") String description,
                         HttpSession session, HttpServletResponse response,
                         RedirectAttributes ra) {
        if (!canManage(session)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return "error/403";
        }
        Long userId = (Long) session.getAttribute("userId");
        User moderator = userService.findById(userId).orElseThrow();
        categoryService.create(title, description, moderator);
        ra.addFlashAttribute("success", "Раздел «" + title + "» создан.");
        return "redirect:/";
    }

    private boolean canManage(HttpSession session) {
        String role = (String) session.getAttribute("role");
        return "admin".equals(role) || "moderator".equals(role);
    }
}
