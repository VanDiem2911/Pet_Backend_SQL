package com.petshop.controller;

import com.petshop.model.News;
import com.petshop.repository.NewsRepository;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/news")
public class NewsController {
    private final NewsRepository newsRepository;

    public NewsController(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    @GetMapping
    public List<News> listNews() {
        return newsRepository.findAllByOrderByCreatedAtDesc();
    }
}
