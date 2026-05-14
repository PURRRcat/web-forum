package ru.forum.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.forum.service.PostService;
import ru.forum.service.TopicService;

@Controller
public class SearchController {

    @Autowired private TopicService topicService;
    @Autowired private PostService postService;

    @GetMapping("/search")
    public String search(@RequestParam(defaultValue = "") String q, Model model) {
        model.addAttribute("q", q);
        if (!q.isBlank()) {
            model.addAttribute("topicResults", topicService.search(q));
            model.addAttribute("postResults",  postService.search(q));
        }
        return "search";
    }
}
