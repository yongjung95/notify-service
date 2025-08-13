package com.example.service;

import com.example.dto.StocksRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StocksService {
    Page<StocksRecord> findStocksList(Pageable pageable, String exchangeCountry, String searchText, String memberUUID);
}
