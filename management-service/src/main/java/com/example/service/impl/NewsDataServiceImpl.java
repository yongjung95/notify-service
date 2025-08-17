package com.example.service.impl;

import com.example.domain.NewsData;
import com.example.dto.NewsDataRecord;
import com.example.exception.NotFoundNewsException;
import com.example.repository.NewsDataRepository;
import com.example.service.NewsDataService;
import com.example.service.RssSummaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsDataServiceImpl implements NewsDataService {

    private final NewsDataRepository newsDataRepository;

    private final RssSummaryService rssSummaryService;

    @Override
    public NewsDataRecord findNewsDataById(Long id) {
        NewsData newsData = newsDataRepository.findById(id).orElseThrow(NotFoundNewsException::new);
        return NewsDataRecord.fromNewsDataEntity(newsData);
    }

    @Override
    public Mono<NewsDataRecord> findNewsDataByKeywordAndToday(String keyword, LocalDate today) {
        return Mono.fromCallable(() -> newsDataRepository.findByKeywordAndToday(keyword, today))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(optionalNewsData -> optionalNewsData.map(newsData -> Mono.just(NewsDataRecord.fromNewsDataEntity(newsData)))
                        .orElseGet(() -> rssSummaryService.summarizeRssFeed(keyword)
                                .flatMap(result ->
                                        Mono.fromCallable(() -> newsDataRepository.save(NewsData.of(keyword, result)))
                                                .subscribeOn(Schedulers.boundedElastic())
                                )
                                .map(NewsDataRecord::fromNewsDataEntity)
                                .onErrorResume(e -> {
                                    // 에러 발생 시 처리
                                    log.error("Error processing News for keyword {}: {}", keyword, e.getMessage());
                                    return Mono.empty();
                                })));
    }
}
