package com.petshop.controller;

import com.petshop.dto.NewsletterRequest;
import com.petshop.model.NewsletterSubscription;
import com.petshop.repository.NewsletterRepository;
import com.petshop.service.NewsletterService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/newsletter")
public class NewsletterController {
    private final NewsletterService newsletterService;
    private final NewsletterRepository newsletterRepository;

    public NewsletterController(NewsletterService newsletterService, NewsletterRepository newsletterRepository) {
        this.newsletterService = newsletterService;
        this.newsletterRepository = newsletterRepository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NewsletterSubscription subscribe(@Valid @RequestBody NewsletterRequest request) {
        return newsletterService.subscribe(request.email());
    }

    @GetMapping
    public List<NewsletterSubscription> list() {
        return newsletterRepository.findAll();
    }
}
