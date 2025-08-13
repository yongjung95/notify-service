package com.example.service.impl;

import com.example.dto.StocksRecord;
import com.example.repository.StocksRepository;
import com.example.service.StocksService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StocksServiceImpl implements StocksService {

    private final StocksRepository stocksRepository;

    @Override
    public Page<StocksRecord> findStocksList(Pageable pageable, String exchangeCountry, String searchText, String memberUUID) {

        Page<StocksRecord> stocksPage = stocksRepository.findStocksList(pageable, exchangeCountry, searchText, memberUUID);

        return new PageImpl<>(stocksPage.getContent(), pageable, stocksPage.getTotalElements());
    }
}
