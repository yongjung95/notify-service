package com.example.service.impl;

import com.example.dto.ApiInfo;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StocksPriceServiceImplTest {

    @InjectMocks
    private StocksPriceServiceImpl stocksPriceService;

    // WebClient를 MockWebServer에 연결하기 위해 WebClient 인스턴스를 직접 생성
    private WebClient webClient;

    // MockWebServer 인스턴스
    private MockWebServer mockWebServer;

    // Mocking 할 API 정보
    @Mock
    private ApiInfo apiInfo;

    // 의존성 주입을 위한 Mock 변수
    private String koreaInvestAppKey;
    private String koreaInvestAppSecret;

    @BeforeEach
    void setUp() throws IOException {
        // MockWebServer 시작
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        // WebClient 인스턴스 생성 및 MockWebServer의 URL로 설정
        webClient = WebClient.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .build();

        // @InjectMocks가 초기화된 후, private 필드인 webClient를 Reflection을 사용해 주입
        ReflectionTestUtils.setField(stocksPriceService, "webClient", webClient);
        ReflectionTestUtils.setField(stocksPriceService, "koreaInvestAppKey", "test-key");
        ReflectionTestUtils.setField(stocksPriceService, "koreaInvestAppSecret", "test-secret");
    }

    @AfterEach
    void tearDown() throws IOException {
        // MockWebServer 종료
        mockWebServer.shutdown();
    }

    @Test
    void 주식_가격_가져오기() {
        // Given: Mock API 응답 설정
        Double expectedPrice = Double.valueOf("50000");
        String mockResponseJson = "{\"output\":{\"stck_prpr\":\"" + expectedPrice + "\"}}";
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(mockResponseJson)
                .addHeader("Content-Type", "application/json"));

        String ticker = "005930";
        when(apiInfo.token()).thenReturn("test-token");

        // When: 메서드 호출
        Mono<Double> resultMono = stocksPriceService.getKoreaStockPrice(ticker, apiInfo.token());

        // Then: StepVerifier를 사용하여 Mono의 결과를 검증
        StepVerifier.create(resultMono)
                .expectNext(expectedPrice)
                .verifyComplete();
    }
}