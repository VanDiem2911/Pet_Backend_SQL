package com.petshop.repository;

import com.petshop.model.News;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsRepository extends JpaRepository<News, String> {
    List<News> findAllByOrderByCreatedAtDesc();
}
