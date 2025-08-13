package com.example.service;

public interface StocksManagementService {
    Long save(String ticker, String memberUUID);
    void delete(String ticker, String memberUUID);
}
