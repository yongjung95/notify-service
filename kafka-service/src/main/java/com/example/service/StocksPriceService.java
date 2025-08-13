package com.example.service;

import reactor.core.publisher.Mono;

public interface StocksPriceService {
    Mono<Double> getKoreaStockPrice(String ticker, String token);
    Mono<Double> getAmericaStockPrice(String ticker, String exchange, String token);
}
