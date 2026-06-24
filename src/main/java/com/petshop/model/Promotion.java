package com.petshop.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "promotions")
public class Promotion {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @NotBlank private String title;
    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String description;
    @Min(0) @Max(100) private Integer discountPercent = 0;
    @Min(0) private Integer discountAmount = 0;
    private String startDate;
    private String endDate;
    private String imageUrl;
    private String serviceCode;
    @NotNull private Boolean active = true;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "promotion_id")
    private List<PromotionTier> tiers = new ArrayList<>();
    private String promotionType = "PREBOOK";
    private Integer advanceHours = 24;
    private String startTime;
    private String endTime;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getDiscountPercent() { return discountPercent; }
    public void setDiscountPercent(Integer discountPercent) { this.discountPercent = discountPercent; }
    public Integer getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(Integer discountAmount) { this.discountAmount = discountAmount; }
    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getServiceCode() { return serviceCode; }
    public void setServiceCode(String serviceCode) { this.serviceCode = serviceCode; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
    public List<PromotionTier> getTiers() { return tiers; }
    public void setTiers(List<PromotionTier> tiers) { this.tiers = tiers == null ? new ArrayList<>() : tiers; }
    public String getPromotionType() { return promotionType; }
    public void setPromotionType(String promotionType) { this.promotionType = promotionType; }
    public Integer getAdvanceHours() { return advanceHours; }
    public void setAdvanceHours(Integer advanceHours) { this.advanceHours = advanceHours; }
    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
}
