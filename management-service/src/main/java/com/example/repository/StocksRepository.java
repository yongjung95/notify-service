package com.example.repository;

import com.example.domain.Stocks;
import com.example.dto.StocksRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface StocksRepository {
    Page<StocksRecord> findStocksList(Pageable pageable, String exchangeCountry, String searchText, String memberUUID);
    Optional<Stocks> findStocksByTicker(String ticker);
}
