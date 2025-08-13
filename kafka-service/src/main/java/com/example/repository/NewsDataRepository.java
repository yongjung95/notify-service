package com.example.repository;

import com.example.domain.NewsData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsDataRepository extends JpaRepository<NewsData, Long> {
}
