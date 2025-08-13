package com.example.service.impl;

import com.example.dto.ApiInfo;
import com.example.dto.SendNotifyStocksTopic;
import com.example.dto.StocksManagementNotify;
import com.example.repository.ApiInfoQueryRepository;
import com.example.repository.StocksManagementQueryRepository;
import com.example.service.KafkaProducerService;
import com.example.service.StocksManagementService;
import com.example.service.StocksPriceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StocksManagementServiceImpl implements StocksManagementService {

    private final StocksManagementQueryRepository stocksManagementQueryRepository;
    private final ApiInfoQueryRepository apiInfoQueryRepository;
    private final StocksPriceService stocksPriceService;
    private final KafkaProducerService kafkaProducerService;

    private final String koreaTopicName = "korea-management-topic";
    private final String americaTopicName = "america-management-topic";

    @Override
    public Mono<Void> sendKoreaStockManagement(Boolean isOpening) {
        List<StocksManagementNotify> stocksManagementNotifyList =
                stocksManagementQueryRepository.findStocksManagementNotifyForKoreaStock();

        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        ApiInfo apiInfo = apiInfoQueryRepository.findApiInfo(today);

        if (apiInfo.opendYn()) {
            return Flux.fromIterable(stocksManagementNotifyList)
                    .flatMap(stocksManagementNotify -> stocksPriceService.getKoreaStockPrice(stocksManagementNotify.ticker(), apiInfo.token())
                            .flatMap(price -> {
                                log.info("price : {}", price);
                                try {
                                    SendNotifyStocksTopic sendNotifyStocksTopic =
                                            SendNotifyStocksTopic.of(stocksManagementNotify.ticker(),
                                                    stocksManagementNotify.name(),
                                                    price,
                                                    stocksManagementNotify.memberUUID(),
                                                    stocksManagementNotify.nickname(),
                                                    stocksManagementNotify.fcmToken(),
                                                    isOpening);

                                    kafkaProducerService.sendTopicMessage(koreaTopicName, stocksManagementNotify.memberUUID(), sendNotifyStocksTopic);
                                    log.info("{} Kafka 발행: {} - {}", koreaTopicName, stocksManagementNotify.memberUUID(), sendNotifyStocksTopic);

                                    return Mono.empty();
                                } catch (Exception e) {
                                    log.error("{} Kafka 발행 에러 발생 (ticker: {}): {}", koreaTopicName, stocksManagementNotify.ticker(), e.getMessage());
                                    return Mono.error(new RuntimeException("Kafka send failed for " + stocksManagementNotify.memberUUID(), e));
                                }
                            })
                            .onErrorResume(e -> {
                                log.error("Error fetching price for ticker {}: {}", stocksManagementNotify.ticker(), e.getMessage());
                                return Mono.empty();
                            }))
                    .then();
        } else {
            return Mono.empty();
        }
    }

    @Override
    public Mono<Void> sendAmericaStockManagement(Boolean isOpening) {
        List<StocksManagementNotify> stocksManagementNotifyList =
                stocksManagementQueryRepository.findStocksManagementNotifyForAmericaStock();

        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        ApiInfo apiInfo = apiInfoQueryRepository.findApiInfo(today);

        return Flux.fromIterable(stocksManagementNotifyList)
                .flatMap(stocksManagementNotify -> stocksPriceService.getAmericaStockPrice(stocksManagementNotify.ticker(), stocksManagementNotify.exchange(), apiInfo.token())
                        .flatMap(price -> {
                            log.info("price : {}", price);
                            try {
                                SendNotifyStocksTopic sendNotifyStocksTopic =
                                        SendNotifyStocksTopic.of(stocksManagementNotify.ticker(),
                                                stocksManagementNotify.name(),
                                                price,
                                                stocksManagementNotify.memberUUID(),
                                                stocksManagementNotify.nickname(),
                                                stocksManagementNotify.fcmToken(),
                                                isOpening);

                                kafkaProducerService.sendTopicMessage(americaTopicName, stocksManagementNotify.memberUUID(), sendNotifyStocksTopic);
                                log.info("Kafka 발행: {} - {}", stocksManagementNotify.memberUUID(), sendNotifyStocksTopic);
                                return Mono.empty();
                            } catch (Exception e) {
                                log.error("Kafka 발행 에러 발생 (ticker: {}): {}", stocksManagementNotify.memberUUID(), e.getMessage());
                                return Mono.error(new RuntimeException("Kafka send failed for " + stocksManagementNotify.memberUUID(), e));
                            }
                        })
                        .onErrorResume(e -> {
                            log.error("Error fetching price for ticker {}: {}", stocksManagementNotify.ticker(), e.getMessage());
                            return Mono.empty();
                        }))
                .then();
    }
}
