package com.example.repository;

import com.example.domain.NewsData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.Optional;

public interface NewsDataRepository extends JpaRepository<NewsData, Long> {

    @Query("SELECT n FROM NewsData n WHERE n.keyword = :keyword AND FUNCTION('DATE', n.createdDate) = :today")
    Optional<NewsData> findByKeywordAndToday(String keyword, LocalDate today);
}
