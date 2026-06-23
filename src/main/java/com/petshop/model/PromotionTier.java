package com.petshop.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@Entity
@Table(name = "promotion_tiers")
public class PromotionTier {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Min(1) private Integer minDays;
    private Integer maxDays;
    @Min(0) @Max(100) private Integer discountPercent;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public Integer getMinDays() { return minDays; }
    public void setMinDays(Integer minDays) { this.minDays = minDays; }
    public Integer getMaxDays() { return maxDays; }
    public void setMaxDays(Integer maxDays) { this.maxDays = maxDays; }
    public Integer getDiscountPercent() { return discountPercent; }
    public void setDiscountPercent(Integer discountPercent) { this.discountPercent = discountPercent; }
}
