package com.petshop.model;

import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import org.springframework.data.annotation.CreatedDate;
import jakarta.persistence.*;

@Entity
@Table(name = "appointments")
@EntityListeners(org.springframework.data.jpa.domain.support.AuditingEntityListener.class)
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotBlank
    private String serviceType; // "grooming" | "boarding"

    @NotBlank
    private String customerName;

    @NotBlank
    private String phone;

    @NotBlank
    private String date; // "YYYY-MM-DD"

    private String checkoutDate; // boarding only
    private String timeSlot;    // grooming only
    @Column(columnDefinition = "TEXT")
    private String notes;
    private String status = "PENDING";
    private String appliedPromotionId;
    private String appliedPromotionTitle;
    private Integer discountPercent = 0;
    private Integer discountAmount = 0;

    @CreatedDate
    private Instant createdAt;

    // --- Getters & Setters ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getServiceType() { return serviceType; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getCheckoutDate() { return checkoutDate; }
    public void setCheckoutDate(String checkoutDate) { this.checkoutDate = checkoutDate; }

    public String getTimeSlot() { return timeSlot; }
    public void setTimeSlot(String timeSlot) { this.timeSlot = timeSlot; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getAppliedPromotionId() { return appliedPromotionId; }
    public void setAppliedPromotionId(String appliedPromotionId) { this.appliedPromotionId = appliedPromotionId; }
    public String getAppliedPromotionTitle() { return appliedPromotionTitle; }
    public void setAppliedPromotionTitle(String appliedPromotionTitle) { this.appliedPromotionTitle = appliedPromotionTitle; }
    public Integer getDiscountPercent() { return discountPercent; }
    public void setDiscountPercent(Integer discountPercent) { this.discountPercent = discountPercent; }
    public Integer getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(Integer discountAmount) { this.discountAmount = discountAmount; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
