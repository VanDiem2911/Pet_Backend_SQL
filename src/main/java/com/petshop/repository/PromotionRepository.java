package com.petshop.repository;

import com.petshop.model.Promotion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PromotionRepository extends JpaRepository<Promotion, String> {
    List<Promotion> findByActiveTrue();
    boolean existsByServiceCode(String serviceCode);
}
