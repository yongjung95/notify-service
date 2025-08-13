package com.example.service.impl;

import com.example.domain.Stocks;
import com.example.domain.StocksPriceManagement;
import com.example.exception.DuplicateStockPriceManagementException;
import com.example.exception.NotFoundStockException;
import com.example.exception.StockPriceManagementAccessException;
import com.example.repository.StocksPriceManagementRepository;
import com.example.repository.StocksRepository;
import com.example.service.StocksPriceManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StocksPriceManagementServiceImpl implements StocksPriceManagementService {

    private final StocksPriceManagementRepository stocksPriceManagementRepository;

    private final StocksRepository stocksRepository;

    @Override
    public Long save(String ticker, Double tagetPrice, String memberUUID) {
        Stocks stocks = stocksRepository.findStocksByTicker(ticker).orElseThrow(NotFoundStockException::new);

        Optional<StocksPriceManagement> stocksPriceManagement =
                stocksPriceManagementRepository.findByStockIdAndMemberUUID(stocks.getId(), memberUUID);

        if (stocksPriceManagement.isPresent()) {
            throw new DuplicateStockPriceManagementException();
        }

        return stocksPriceManagementRepository.save(StocksPriceManagement.of(stocks.getId(), tagetPrice, memberUUID)).getId();
    }

    @Override
    public void delete(String ticker, String memberUUID) {
        Stocks stocks = stocksRepository.findStocksByTicker(ticker).orElseThrow(NotFoundStockException::new);

        StocksPriceManagement stocksPriceManagement =
                stocksPriceManagementRepository.findByStockIdAndMemberUUID(stocks.getId(), memberUUID)
                        .orElseThrow(StockPriceManagementAccessException::new);

        stocksPriceManagementRepository.delete(stocksPriceManagement);
    }
}
