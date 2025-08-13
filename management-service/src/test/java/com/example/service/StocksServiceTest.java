package com.example.service;

import com.example.domain.Stocks;
import com.example.domain.StocksType;
import com.example.dto.StocksRecord;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class StocksServiceTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private StocksService stocksService;

    @Test
    @Transactional
    void 국내_주식_목록_조회() throws Exception {
        //given
        entityManager.merge(Stocks.of("005930", "삼성전자", "KOREA", "KRX", StocksType.STOCK));
        entityManager.merge(Stocks.of("000660", "SK하이닉스", "KOREA", "KRX", StocksType.STOCK));

        //when
        String searchText = "SK하이닉스";
        String exchangeCountry = "KOREA";
        Page<StocksRecord> result = stocksService.findStocksList(PageRequest.of(1, 10), exchangeCountry, searchText);

        //then
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    @Transactional
    void 미국_주식_목록_조회() throws Exception {
        //given
        entityManager.merge(Stocks.of("QQQ", "NVESCO QQQ TRUST", "AMERICA", "NASDAQ", StocksType.ETF));
        entityManager.merge(Stocks.of("APPL", "애플", "AMERICA", "NASDAQ", StocksType.STOCK));
        entityManager.merge(Stocks.of("BRK/A", "버크셔 해서웨이 A", "AMERICA", "NASDAQ", StocksType.STOCK));

        //when
        String searchText = "애플";
        String exchangeCountry = "AMERICA";

        Page<StocksRecord> result = stocksService.findStocksList(PageRequest.of(1, 10), exchangeCountry, searchText);

        //then
        assertThat(result.getTotalElements()).isEqualTo(1);
    }
}