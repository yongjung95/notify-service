package com.example.service;

import com.example.dto.NewsDataRecord;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

public interface NewsDataService {
    NewsDataRecord findNewsDataById(Long id);

    Mono<NewsDataRecord> findNewsDataByKeywordAndToday(String keyword, LocalDate today);
}
