package com.petshop.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import org.springframework.data.annotation.CreatedDate;
import jakarta.persistence.*;

@Entity
@Table(name = "newsletter_subscriptions")
@EntityListeners(org.springframework.data.jpa.domain.support.AuditingEntityListener.class)
public class NewsletterSubscription {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Email
    @NotBlank
    @Column(unique = true)
    private String email;

    @CreatedDate
    private Instant createdAt;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
