package com.petshop.controller;

import com.petshop.model.PetService;
import com.petshop.model.Promotion;
import com.petshop.repository.PetServiceRepository;
import com.petshop.repository.PromotionRepository;
import java.util.List;
import java.time.LocalDate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api")
public class ServiceCatalogController {
    private final PetServiceRepository serviceRepository;
    private final PromotionRepository promotionRepository;

    public ServiceCatalogController(PetServiceRepository serviceRepository, PromotionRepository promotionRepository) {
        this.serviceRepository = serviceRepository;
        this.promotionRepository = promotionRepository;
    }

    @GetMapping("/services")
    public List<PetService> services() {
        return serviceRepository.findByActiveTrueOrderBySortOrderAsc();
    }

    @GetMapping("/promotions")
    public List<Promotion> promotions(@RequestParam(defaultValue = "false") boolean forBooking) {
        List<Promotion> active = promotionRepository.findByActiveTrue();
        if (forBooking) return active;
        String today = LocalDate.now(java.time.ZoneId.of("Asia/Ho_Chi_Minh")).toString();
        return active.stream()
                .filter(promotion -> {
                    if (promotion.getEndDate() == null || promotion.getEndDate().isBlank()) return true;
                    try {
                        LocalDate.parse(promotion.getEndDate());
                        return promotion.getEndDate().compareTo(today) >= 0;
                    } catch (Exception e) {
                        return false; // Skip if date is malformed
                    }
                })
                .toList();
    }
}
