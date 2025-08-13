package com.example.service;

import com.example.dto.NewsManagementRecord;

import java.util.List;

public interface NewsManagementService {
    List<NewsManagementRecord> findNewsManagementByMemberUUID(String memberUUID);
    Long save(String keyword, String memberUUID);
    void delete(String keyword, String memberUUID);
}
