package com.example.service.impl;

import com.example.dto.ApiInfo;
import com.example.dto.SendNotifyStocksTopic;
import com.example.dto.StocksManagementNotify;
import com.example.repository.ApiInfoQueryRepository;
import com.example.repository.StocksManagementQueryRepository;
import com.example.service.KafkaProducerService;
import com.example.service.StocksManagementService;
import com.example.service.StocksPriceService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
class StocksManagementServiceImplTest {

    // 테스트할 Service를 Spring 컨텍스트에서 주입받습니다.
    @Autowired
    private StocksManagementService stocksManagementService;

    // 통합 테스트 시 실제 빈 대신 MockBean을 사용해 외부 의존성을 격리합니다.
    @MockitoBean
    private StocksManagementQueryRepository stocksManagementQueryRepository;

    @MockitoBean
    private ApiInfoQueryRepository apiInfoQueryRepository;

    @MockitoBean
    private StocksPriceService stocksPriceService;

    @MockitoBean
    private KafkaProducerService kafkaProducerService;

    // 필요한 DTO와 엔티티 Mocking
    @Mock
    private StocksManagementNotify stock1;
    @Mock
    private StocksManagementNotify stock2;
    @Mock
    private ApiInfo apiInfo;

    @Test
    void KAFKA_메시지_발행_테스트() {
        // Given: 테스트에 필요한 Mock 객체들의 행동 정의
        // 1. repository 호출 시 mock 데이터 반환
        when(stocksManagementQueryRepository.findStocksManagementNotifyForKoreaStock())
                .thenReturn(List.of(stock1, stock2));

        // 2. apiInfo 호출 시 mock 데이터 반환
        when(apiInfoQueryRepository.findApiInfo(any(String.class))).thenReturn(apiInfo);
        when(apiInfo.token()).thenReturn("test-token");

        // 3. StocksManagementNotify 객체에 대한 mock 데이터 설정
        when(stock1.ticker()).thenReturn("005930");
        when(stock1.name()).thenReturn("삼성전자");
        when(stock1.memberUUID()).thenReturn("1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d");
        when(stock1.nickname()).thenReturn("testuser1");
        when(stock1.fcmToken()).thenReturn("fcm-token-1");

        when(stock2.ticker()).thenReturn("000660");
        when(stock2.name()).thenReturn("SK하이닉스");
        when(stock2.memberUUID()).thenReturn("2b3c4d5e-6f7a-8b9c-0d1e-2f3a4b5c6d7e");
        when(stock2.nickname()).thenReturn("testuser2");
        when(stock2.fcmToken()).thenReturn("fcm-token-2");

        // 4. 주식 가격 서비스 호출 시 Mono.just()로 가격 반환하도록 설정
        when(stocksPriceService.getKoreaStockPrice(eq("005930"), any(String.class))).thenReturn(Mono.just(Double.valueOf("70000")));
        when(stocksPriceService.getKoreaStockPrice(eq("000660"), any(String.class))).thenReturn(Mono.just(Double.valueOf("100000")));

        // When: 테스트 대상 메서드 호출
        Mono<Void> resultMono = stocksManagementService.sendKoreaStockManagement(true);

        // Then: StepVerifier를 사용하여 Mono가 성공적으로 완료되는지 검증
        StepVerifier.create(resultMono)
                .verifyComplete();

        // Then: Mockito.verify()를 사용하여 의도한 대로 메서드들이 호출되었는지 검증
        verify(stocksManagementQueryRepository, times(1)).findStocksManagementNotifyForKoreaStock();
        verify(apiInfoQueryRepository, times(1)).findApiInfo(any(String.class));

        // 두 개의 주식에 대해 kafkaProducerService의 sendTopicMessage가 각각 한 번씩 호출되었는지 검증
        verify(kafkaProducerService, times(1)).sendTopicMessage(
                eq("stocks-data-topic"),
                eq("1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d"),
                any(SendNotifyStocksTopic.class)
        );
        verify(kafkaProducerService, times(1)).sendTopicMessage(
                eq("stocks-data-topic"),
                eq("2b3c4d5e-6f7a-8b9c-0d1e-2f3a4b5c6d7e"),
                any(SendNotifyStocksTopic.class)
        );
    }
}