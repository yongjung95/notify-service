package com.example.schedule;

import com.example.service.NewsManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Component
@RequiredArgsConstructor
public class NewsSchedule {

    private final NewsManagementService newsManagementService;

    @Scheduled(cron = "0 0 8 1/1 * *", zone = "GMT+9:00")
//    @PostConstruct
    public void newsSchedule() {
        newsManagementService.sendNewsManagement()
                .subscribeOn(Schedulers.boundedElastic())
                .doOnSubscribe(result -> log.info("뉴스 전송 시작"))
                .doOnError(error -> log.error("뉴스 전송 에러 발생 : ", error))
                .subscribe();
    }
}
