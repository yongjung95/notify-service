package com.example.dto;

import com.example.domain.NewsData;
import lombok.Builder;

@Builder
public record NewsDataRecord(
        Long id,
        String keyword,
        String data
) {
    public static NewsDataRecord fromNewsDataEntity(NewsData newsData) {
        return NewsDataRecord.builder()
                .id(newsData.getId())
                .keyword(newsData.getKeyword())
                .data(newsData.getData())
                .build();
    }
}
