package com.petshop.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.LinkedHashSet;
import jakarta.persistence.*;

@Entity
@Table(name = "services")
public class PetService {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @NotBlank private String code;
    @NotBlank private String title;
    private BigDecimal price;
    private String priceLabel;
    private String unit;
    @Column(columnDefinition = "TEXT")
    private String description;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "service_bullets", joinColumns = @JoinColumn(name = "service_id"))
    @Column(name = "bullet_point", length = 500, nullable = false)
    private Set<String> bulletPoints = new LinkedHashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "service_images", joinColumns = @JoinColumn(name = "service_id"))
    @Column(name = "image_url", length = 500, nullable = false)
    private Set<String> imageUrls = new LinkedHashSet<>();
    @NotNull private Boolean active = true;
    private Integer sortOrder = 0;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public String getPriceLabel() { return priceLabel; }
    public void setPriceLabel(String priceLabel) { this.priceLabel = priceLabel; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<String> getBulletPoints() { return new ArrayList<>(bulletPoints); }
    public void setBulletPoints(List<String> bulletPoints) { this.bulletPoints = bulletPoints == null ? new LinkedHashSet<>() : new LinkedHashSet<>(bulletPoints); }
    public List<String> getImageUrls() { return new ArrayList<>(imageUrls); }
    public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls == null ? new LinkedHashSet<>() : new LinkedHashSet<>(imageUrls); }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
}
