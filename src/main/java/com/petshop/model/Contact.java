package com.petshop.model;

import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import org.springframework.data.annotation.CreatedDate;
import jakarta.persistence.*;

@Entity
@Table(name = "contacts")
@EntityListeners(org.springframework.data.jpa.domain.support.AuditingEntityListener.class)
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotBlank
    private String name;

    @NotBlank
    private String email;

    @NotBlank
    private String subject; // "Tư vấn" | "Hợp tác" | "Khác"

    @NotBlank
    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String message;

    private String status = "PENDING"; // "PENDING" | "RESOLVED"

    @CreatedDate
    private Instant createdAt;

    // --- Getters & Setters ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
