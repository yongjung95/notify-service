package com.example.repository;

import com.example.domain.ApiInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface StockApiInfoRepository extends JpaRepository<ApiInfo, Long> {

    @Query("select s.token from ApiInfo s where s.issueDate = :issueDate")
    Optional<String> findTokenByIssueDate(String issueDate);
}
