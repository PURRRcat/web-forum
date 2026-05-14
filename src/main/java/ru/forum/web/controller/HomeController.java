package ru.forum.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.forum.service.CategoryService;

@Controller
public class HomeController {

    @Autowired private CategoryService categoryService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("stats", categoryService.findAllWithStats());
        return "home";
    }
}
