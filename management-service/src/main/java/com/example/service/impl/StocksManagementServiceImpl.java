package com.example.service.impl;

import com.example.domain.Stocks;
import com.example.domain.StocksManagement;
import com.example.exception.DuplicateStockManagementException;
import com.example.exception.NotFoundStockException;
import com.example.exception.StockManagementAccessException;
import com.example.repository.StocksManagementRepository;
import com.example.repository.StocksRepository;
import com.example.service.StocksManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class StocksManagementServiceImpl implements StocksManagementService {

    private final StocksManagementRepository stocksManagementRepository;

    private final StocksRepository stocksRepository;

    @Override
    public Long save(String ticker, String memberUUID) {
        Stocks stocks =
                stocksRepository.findStocksByTicker(ticker).orElseThrow(NotFoundStockException::new);

        Optional<StocksManagement> stocksManagement =
                stocksManagementRepository.findByStocksIdAndMemberUUID(stocks.getId(), memberUUID);

        if (stocksManagement.isPresent()) {
            throw new DuplicateStockManagementException();
        }

        return stocksManagementRepository.save(StocksManagement.of(stocks.getId(), memberUUID)).getId();
    }

    @Override
    public void delete(String ticker, String memberUUID) {
        Stocks stocks =
                stocksRepository.findStocksByTicker(ticker).orElseThrow(NotFoundStockException::new);

        StocksManagement stocksManagement =
                stocksManagementRepository.findByStocksIdAndMemberUUID(stocks.getId(), memberUUID)
                        .orElseThrow(StockManagementAccessException::new);

        stocksManagementRepository.delete(stocksManagement);
    }
}
