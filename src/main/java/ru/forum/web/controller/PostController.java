package ru.forum.web.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.forum.model.Post;
import ru.forum.model.Topic;
import ru.forum.model.User;
import ru.forum.service.PostService;
import ru.forum.service.TopicService;
import ru.forum.service.UserService;

@Controller
@RequestMapping("/post")
public class PostController {

    @Autowired private PostService postService;
    @Autowired private TopicService topicService;
    @Autowired private UserService userService;

    @PostMapping("/new/{topicId}")
    public String create(@PathVariable Long topicId,
                         @RequestParam String content,
                         HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        User author = userService.findById(userId).orElseThrow();
        Topic topic = topicService.findById(topicId).orElseThrow();
        postService.create(content, topic, author);
        return "redirect:/topic/" + topicId;
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model,
                           HttpSession session, HttpServletResponse response) {
        Post post = postService.findById(id).orElseThrow();
        if (!canModify(session, post)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return "error/403";
        }
        model.addAttribute("post", post);
        return "post/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable Long id,
                       @RequestParam String content,
                       HttpSession session, HttpServletResponse response) {
        Post post = postService.findById(id).orElseThrow();
        if (!canModify(session, post)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return "error/403";
        }
        postService.update(id, content);
        return "redirect:/topic/" + post.getTopic().getId();
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id,
                         HttpSession session, HttpServletResponse response) {
        Post post = postService.findById(id).orElseThrow();
        if (!canModify(session, post)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return "error/403";
        }
        Long topicId = post.getTopic().getId();
        postService.delete(id);
        return "redirect:/topic/" + topicId;
    }

    private boolean canModify(HttpSession session, Post post) {
        Long userId = (Long) session.getAttribute("userId");
        String role = (String) session.getAttribute("role");
        return userId != null
                && (post.getUser().getId().equals(userId) || "admin".equals(role));
    }
}
