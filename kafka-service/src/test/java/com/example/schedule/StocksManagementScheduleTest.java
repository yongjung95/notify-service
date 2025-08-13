package com.example.schedule;

import com.example.dto.ApiInfo;
import com.example.repository.ApiInfoQueryRepository;
import com.example.repository.StocksManagementQueryRepository;
import com.example.service.KafkaProducerService;
import com.example.service.StocksManagementService;
import com.example.service.StocksPriceService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.Mockito.*;

@SpringBootTest
class StocksManagementScheduleTest {

    @MockitoSpyBean
    private StocksManagementService stocksManagementService;

    // 서비스의 의존성들을 MockBean으로 만들어 외부 시스템과의 통신을 차단합니다.
    @MockitoBean
    private StocksManagementQueryRepository stocksManagementQueryRepository;

    @MockitoBean
    private StocksPriceService stocksPriceService;

    @MockitoBean
    private ApiInfoQueryRepository apiInfoQueryRepository;

    @MockitoBean
    private KafkaProducerService kafkaProducerService;

    @Test
    @DisplayName("스케줄러가 정해진 시간에 메서드를 호출해야 한다")
    void shouldExecuteScheduledMethod() {
        // Given: 스케줄러가 호출하는 서비스의 의존성들에 대한 Mock 동작을 설정합니다.
        // `findStocksManagementNotifyForKoreaStock()`이 빈 리스트를 반환하도록 Mocking
        when(stocksManagementQueryRepository.findStocksManagementNotifyForKoreaStock())
                .thenReturn(List.of());

        // `findApiInfo()`가 특정 객체를 반환하도록 Mocking
        when(apiInfoQueryRepository.findApiInfo(any(String.class)))
                .thenReturn(new ApiInfo("token", true));

        // `getKoreaStockPrice()`가 Mono를 반환하도록 Mocking
        // 이 Mocking이 없으면 NullPointerException이 발생합니다.
        when(stocksPriceService.getKoreaStockPrice(any(String.class), any(String.class)))
                .thenReturn(Mono.just(Double.valueOf("10000")));

        // `sendTopicMessage()`는 void를 반환하므로 Mocking할 필요가 없습니다.

        // When & Then: `verify`와 `timeout`을 사용해 특정 시간(예: 5초) 내에 스케줄러가 실행되었는지 검증합니다.
        // `stockManagementService.sendKoreaStockManagement` 메서드가 `anyBoolean()` 인자와 함께 호출되었는지 확인합니다.
        verify(stocksManagementService, timeout(5000).times(1)).sendKoreaStockManagement(true);
    }
}