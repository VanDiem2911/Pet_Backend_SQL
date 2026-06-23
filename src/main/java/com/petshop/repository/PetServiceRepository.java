package com.petshop.repository;

import com.petshop.model.PetService;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetServiceRepository extends JpaRepository<PetService, String> {
    List<PetService> findByActiveTrueOrderBySortOrderAsc();
    Optional<PetService> findByCode(String code);
}
