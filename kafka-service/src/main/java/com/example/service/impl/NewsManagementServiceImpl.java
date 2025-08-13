package com.example.service.impl;

import com.example.domain.NewsData;
import com.example.dto.SendNotifyNewsTopic;
import com.example.repository.NewsDataRepository;
import com.example.repository.NewsManagementQueryRepository;
import com.example.service.KafkaProducerService;
import com.example.service.NewsManagementService;
import com.example.service.RssSummaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsManagementServiceImpl implements NewsManagementService {

    private final RssSummaryService rssSummaryService;
    private final KafkaProducerService kafkaProducerService;
    private final NewsManagementQueryRepository newsManagementQueryRepository;
    private final NewsDataRepository newsDataRepository;

    private final String newsTopicName = "news-management-topic";

    @Override
    @Transactional
    public Mono<Void> sendNewsManagement() {
        // 1. DB 조회(findNewsManagementNotify)를 Mono.fromCallable로 감싸서 논블로킹으로 시작
        return Mono.fromCallable(newsManagementQueryRepository::findNewsManagementNotify)
                .publishOn(Schedulers.boundedElastic()) // DB 조회 작업을 별도 스레드에서 실행
                .flatMapMany(Flux::fromIterable) // 조회된 리스트를 Flux로 변환
                .flatMap(newsManagementNotify ->
                        // 2. RSS 요약 작업은 Mono로 실행
                        rssSummaryService.summarizeRssFeed(newsManagementNotify.keyword())
                                .flatMap(result ->
                                        // 3. DB 저장(save)도 Mono.fromCallable로 감싸기
                                        Mono.fromCallable(() -> newsDataRepository.save(NewsData.of(newsManagementNotify.keyword(), result)))
                                                .publishOn(Schedulers.boundedElastic()) // DB 저장 작업을 별도 스레드에서 실행
                                )
                                .flatMap(newsData -> {
                                    // 4. Kafka 발행(sendTopicMessage)도 Mono.fromCallable로 감싸기
                                    return Mono.fromCallable(() -> {
                                                kafkaProducerService.sendTopicMessage(newsTopicName, newsManagementNotify.memberUUID(),
                                                        SendNotifyNewsTopic.of(
                                                                newsManagementNotify.keyword(),
                                                                newsManagementNotify.memberUUID(),
                                                                newsManagementNotify.nickname(),
                                                                newsManagementNotify.fcmToken(),
                                                                newsData.getId()));

                                                log.info("{} Kafka 발행: {} - {}", newsTopicName, newsManagementNotify.keyword(), newsData.getId());
                                                return true;
                                            }).publishOn(Schedulers.boundedElastic()) // Kafka 전송 작업을 별도 스레드에서 실행
                                            .onErrorResume(e -> {
                                                log.error("{} Kafka 발행 에러 발생 : {}", newsTopicName, e.getMessage());
                                                return Mono.error(new RuntimeException("Kafka send failed for " + newsManagementNotify.keyword(), e));
                                            });
                                })
                                .onErrorResume(e -> {
                                    log.error("Error processing News for keyword {}: {}", newsManagementNotify.keyword(), e.getMessage());
                                    return Mono.empty(); // 특정 키워드 처리 실패 시 다음 키워드로 넘어감
                                })
                )
                .then(); // 최종적으로 Mono<Void> 반환
    }
}
