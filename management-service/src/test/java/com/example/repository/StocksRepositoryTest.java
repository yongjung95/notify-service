package com.example.repository;

import com.example.domain.Stocks;
import com.example.domain.StocksType;
import com.example.dto.StocksRecord;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class StocksRepositoryTest {

    @Autowired
    private StocksRepository stocksRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @Transactional
    void 국내_주식_목록_조회() throws Exception {
        //given
        entityManager.merge(Stocks.of("005930", "삼성전자", "KOREA", "KRX", StocksType.STOCK));
        entityManager.merge(Stocks.of("000660", "SK하이닉스", "KOREA", "KRX", StocksType.STOCK));

        //when
        String searchText = "SK하이닉스";
        String exchangeCountry = "KOREA";
        String memberUUID = UUID.randomUUID().toString();
        Page<StocksRecord> result = stocksRepository.findStocksList(Pageable.ofSize(10), exchangeCountry, searchText, memberUUID);

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
        String memberUUID = UUID.randomUUID().toString();

        Page<StocksRecord> result = stocksRepository.findStocksList(PageRequest.of(1, 10), exchangeCountry, searchText, memberUUID);

        //then
        assertThat(result.getTotalElements()).isEqualTo(1);
    }
}