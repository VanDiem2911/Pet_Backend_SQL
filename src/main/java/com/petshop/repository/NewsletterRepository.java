package com.petshop.repository;

import com.petshop.model.NewsletterSubscription;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsletterRepository extends JpaRepository<NewsletterSubscription, String> {
    Optional<NewsletterSubscription> findByEmailIgnoreCase(String email);
}
