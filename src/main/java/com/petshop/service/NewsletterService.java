package com.petshop.service;

import com.petshop.model.NewsletterSubscription;
import com.petshop.repository.NewsletterRepository;
import org.springframework.stereotype.Service;

@Service
public class NewsletterService {
    private final NewsletterRepository newsletterRepository;

    public NewsletterService(NewsletterRepository newsletterRepository) {
        this.newsletterRepository = newsletterRepository;
    }

    public NewsletterSubscription subscribe(String email) {
        return newsletterRepository.findByEmailIgnoreCase(email)
                .orElseGet(() -> {
                    NewsletterSubscription subscription = new NewsletterSubscription();
                    subscription.setEmail(email.trim().toLowerCase());
                    return newsletterRepository.save(subscription);
                });
    }
}
