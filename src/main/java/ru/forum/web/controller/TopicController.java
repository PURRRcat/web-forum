package ru.forum.web.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.forum.model.Category;
import ru.forum.model.Topic;
import ru.forum.model.User;
import ru.forum.service.CategoryService;
import ru.forum.service.PostService;
import ru.forum.service.TopicService;
import ru.forum.service.UserService;

@Controller
@RequestMapping("/topic")
public class TopicController {

    @Autowired private TopicService topicService;
    @Autowired private CategoryService categoryService;
    @Autowired private PostService postService;
    @Autowired private UserService userService;

    @GetMapping("/{id}")
    public String view(@PathVariable Long id, Model model) {
        model.addAttribute("topic", topicService.findById(id).orElseThrow());
        model.addAttribute("posts", postService.findByTopicId(id));
        return "topic";
    }

    @GetMapping("/new/{categoryId}")
    public String newForm(@PathVariable Long categoryId, Model model) {
        model.addAttribute("category", categoryService.findById(categoryId).orElseThrow());
        return "topic/new";
    }

    @PostMapping("/new/{categoryId}")
    public String create(@PathVariable Long categoryId,
                         @RequestParam String title,
                         @RequestParam(defaultValue = "") String content,
                         HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        User author  = userService.findById(userId).orElseThrow();
        Category cat = categoryService.findById(categoryId).orElseThrow();
        Topic topic  = topicService.create(title, cat, author);
        if (!content.isBlank()) {
            postService.create(content, topic, author);
        }
        return "redirect:/topic/" + topic.getId();
    }
}
