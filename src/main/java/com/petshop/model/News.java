package com.petshop.model;

import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import org.springframework.data.annotation.CreatedDate;
import jakarta.persistence.*;

@Entity
@Table(name = "news")
@EntityListeners(org.springframework.data.jpa.domain.support.AuditingEntityListener.class)
public class News {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotBlank
    private String title;

    @NotBlank
    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String summary;

    @NotBlank
    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String content;

    private String imageUrl;

    private String author = "Pet Home Spa";

    @CreatedDate
    private Instant createdAt;

    // --- Getters & Setters ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
