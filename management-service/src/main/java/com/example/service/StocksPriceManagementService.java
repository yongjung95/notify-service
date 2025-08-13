package com.example.service;

public interface StocksPriceManagementService {
    Long save(String ticker, Double tagetPrice, String memberUUID);
    void delete(String ticker, String memberUUID);
}
