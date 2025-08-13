package com.example.schedule;

import com.example.service.StocksManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Component
@RequiredArgsConstructor
public class StocksManagementSchedule {

    private final StocksManagementService stocksManagementService;

    @Scheduled(cron = "0 0 9 1/1 * MON-FRI", zone = "GMT+9:00")
//    @Scheduled(cron = "*/3 * * * * *") // 테스트용
//    @PostConstruct
    public void openingKoreaStocks() {
        stocksManagementService.sendKoreaStockManagement(true)
                .subscribeOn(Schedulers.boundedElastic())
                .doOnSubscribe(result -> log.info("국내 주식 개장 가격 전송 시작"))
                .doOnError(error -> log.error("국내주식 개장 가격 전송 에러 발생 : ", error))
                .subscribe();
    }

    @Scheduled(cron = "0 30 15 1/1 * MON-FRI", zone = "GMT+9:00")
//    @PostConstruct
    public void closingKoreaStocks() {
        stocksManagementService.sendKoreaStockManagement(false)
                .subscribeOn(Schedulers.boundedElastic())
                .doOnSubscribe(result -> log.info("국내 주식 폐장 가격 전송 시작"))
                .doOnError(error -> log.error("국내 주식 폐장 가격 전송 에러 발생 : ", error))
                .subscribe();
    }

    @Scheduled(cron = "0 30 9 1/1 * MON-FRI", zone = "America/New_York")
//    @PostConstruct
    public void openingAmericaStocks() {
        stocksManagementService.sendAmericaStockManagement(true)
                .subscribeOn(Schedulers.boundedElastic())
                .doOnSubscribe(result -> log.info("미국 주식 개장 가격 전송 시작"))
                .doOnError(error -> log.error("미국 주식 개장 가격 전송 에러 발생 : ", error))
                .subscribe();
    }

    @Scheduled(cron = "0 0 16 1/1 * MON-FRI", zone = "America/New_York")
    public void closingAmericaStocks() {
        stocksManagementService.sendAmericaStockManagement(false)
                .subscribeOn(Schedulers.boundedElastic())
                .doOnSubscribe(result -> log.info("미국 주식 폐장 가격 전송 시작"))
                .doOnError(error -> log.error("미국 주식 폐장 가격 전송 에러 발생 : ", error))
                .subscribe();
    }
}
