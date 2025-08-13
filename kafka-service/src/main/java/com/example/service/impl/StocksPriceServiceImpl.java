package com.example.service.impl;

import com.example.service.StocksPriceService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@Slf4j
@Service
@RequiredArgsConstructor
public class StocksPriceServiceImpl implements StocksPriceService {

    private final WebClient webClient;

    @Value("${korea-invest.api.app-key}")
    private String koreaInvestAppKey;
    @Value("${korea-invest.api.app-secret}")
    private String koreaInvestAppSecret;

    @Cacheable(value = "stockPrices", key = "#ticker")
    public Mono<Double> getKoreaStockPrice(String ticker, String token) {
        String trId = "FHKST01010100";

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/uapi/domestic-stock/v1/quotations/inquire-price-2")
                        .queryParam("FID_COND_MRKT_DIV_CODE", "J")
                        .queryParam("FID_INPUT_ISCD", ticker)
                        .build())
                .header("authorization", token)
                .header("appkey", koreaInvestAppKey)
                .header("appsecret", koreaInvestAppSecret)
                .header("tr_id", trId)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new RuntimeException("API Error: " + clientResponse.statusCode() + " - " + errorBody))))
                .bodyToMono(JsonNode.class)
                .map(jsonNode -> jsonNode.path("output").path("stck_prpr").asDouble())
                .onErrorResume(e -> {
                    log.error("Error fetching price for ticker {}: {}", ticker, e.getMessage());
                    return Mono.empty();
                });
    }

    @Override
    public Mono<Double> getAmericaStockPrice(String ticker, String exchange, String token) {
        String trId = "HHDFS00000300";

        String excd = switch (exchange) {
            case "AMEX" -> "AMS";
            case "NYSE" -> "NYS";
            case "NASDAQ" -> "NAS";
            default -> "";
        };

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/uapi/overseas-price/v1/quotations/search-info")
                        .queryParam("EXCD", excd)
                        .queryParam("SYMB", ticker)
                        .build())
                .header("authorization", token)
                .header("appkey", koreaInvestAppKey)
                .header("appsecret", koreaInvestAppSecret)
                .header("tr_id", trId)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new RuntimeException("API Error: " + clientResponse.statusCode() + " - " + errorBody))))
                .bodyToMono(JsonNode.class)
                .map(jsonNode -> jsonNode.path("output").path("last").asDouble())
                .onErrorResume(e -> {
                    log.error("Error fetching price for ticker {}: {}", ticker, e.getMessage());
                    return Mono.empty();
                });
    }
}
