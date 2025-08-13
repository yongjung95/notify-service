package com.example.service;

import com.example.domain.ApiInfo;
import com.example.dto.client.MarketHolidayResponse;
import com.example.dto.client.TokenResponse;
import com.example.repository.StockApiInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApiInfoService {

    @Value("${korea-invest.api.base-url}")
    private String koreaInvestBaseUrl;
    @Value("${korea-invest.api.app-key}")
    private String koreaInvestAppKey;
    @Value("${korea-invest.api.app-secret}")
    private String koreaInvestAppSecret;

    private final StockApiInfoRepository stockApiInfoRepository;

    public String getApiInfo() {
        Date date = new Date();
        SimpleDateFormat todayFormat = new SimpleDateFormat("yyyyMMdd");
        String today = todayFormat.format(date);

        WebClient webClient = WebClient.builder()
                .baseUrl(koreaInvestBaseUrl)
                .build();

        Map<String, String> requestBody = Map.of(
                "grant_type", "client_credentials",
                "appkey", koreaInvestAppKey,
                "appsecret", koreaInvestAppSecret
        );

        try {
            // 1. 토큰 요청
            TokenResponse tokenResponse = webClient.post()
                    .uri("/oauth2/tokenP")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(TokenResponse.class)
                    .timeout(Duration.ofSeconds(10))
                    .block(); // 동기 처리

            String token = "Bearer " + tokenResponse.accessToken();

            // 2. 휴장일 확인
            MarketHolidayResponse marketHolidayResponse = requestIsMarketHoliday(today, token);

            // 3. 결과 처리 및 저장
            String opndYn = marketHolidayResponse.output().get(0).opndYn();
            ApiInfo apiInfo = ApiInfo.of(token, today, opndYn.equals("Y"));

            return stockApiInfoRepository.save(apiInfo).getToken();

        } catch (Exception e) {
            log.error("Failed to get stock API info: {}", e.getMessage(), e);
            throw new RuntimeException("Stock API 정보 조회 실패", e);
        }
    }

    private MarketHolidayResponse requestIsMarketHoliday(String today, String token) {
        WebClient webClient = WebClient.builder()
                .baseUrl(koreaInvestBaseUrl)
                .build();

        try {
            return webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/uapi/domestic-stock/v1/quotations/chk-holiday")
                            .queryParam("BASS_DT", today)
                            .queryParam("CTX_AREA_NK", "")
                            .queryParam("CTX_AREA_FK", "")
                            .build()
                    )
                    .header("authorization", token)
                    .header("appkey", koreaInvestAppKey)
                    .header("appsecret", koreaInvestAppSecret)
                    .header("tr_id", "CTCA0903R")
                    .retrieve()
                    .bodyToMono(MarketHolidayResponse.class)
                    .timeout(Duration.ofSeconds(10))
                    .block(); // 동기 처리

        } catch (Exception e) {
            log.error("Failed to check market holiday for date {}: {}", today, e.getMessage(), e);
            throw new RuntimeException("휴장일 확인 실패", e);
        }
    }

    @Cacheable(key = "#today", cacheNames = "token")
    public String getToken(String today) {
        Optional<String> token = stockApiInfoRepository.findTokenByIssueDate(today);

        return token.orElseGet(this::getApiInfo);
    }
}
