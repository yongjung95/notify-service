package com.example.dto;

import com.example.domain.NewsManagement;
import lombok.Builder;

@Builder
public record NewsManagementRecord(
        Long id,
        String keyword
) {
    public static NewsManagementRecord fromNewsManagement(NewsManagement newsManagement) {
        return NewsManagementRecord.builder()
                .id(newsManagement.getId())
                .keyword(newsManagement.getKeyword())
                .build();
    }
}
