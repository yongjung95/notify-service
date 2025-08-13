package com.example.repository;

import com.example.domain.NewsManagement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NewsManagementRepository extends JpaRepository<NewsManagement, Long> {
    Optional<NewsManagement> findNewsManagementByKeywordAndMemberUUID(String keyword, String memberUUID);
    List<NewsManagement> findNewsManagementByMemberUUID(String memberUUID);
}
