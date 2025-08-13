package com.example.service;

import com.example.domain.Stocks;
import com.example.domain.StocksType;
import com.example.exception.NotFoundStockException;
import com.example.exception.StockManagementAccessException;
import com.example.repository.StocksManagementRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class StocksManagementServiceTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private StocksManagementService stocksManagementService;

    @Autowired
    private StocksManagementRepository stocksManagementRepository;

    @Test
    void 국내주식_관심_종목_추가() throws Exception {
        //given
        entityManager.merge(Stocks.of("005930", "삼성전자", "KOREA", "KRX", StocksType.STOCK));

        //when
        String ticker = "005930";
        String memberUUID = UUID.randomUUID().toString();

        Long save = stocksManagementService.save(ticker, memberUUID);

        //then
        assertThat(save).isNotNull();
    }

    @Test
    void 국내주식_관심_종목_추가_에러() throws Exception {
        //given
        entityManager.merge(Stocks.of("005930", "삼성전자", "KOREA", "KRX", StocksType.STOCK));

        //when
        String ticker = "005931";
        String memberUUID = UUID.randomUUID().toString();

        //then
        assertThatThrownBy(() -> stocksManagementService.save(ticker, memberUUID))
                .isInstanceOf(NotFoundStockException.class);
    }

    @Test
    void 국내주식_관심_종목_제거() throws Exception {
        //given
        entityManager.merge(Stocks.of("005930", "삼성전자", "KOREA", "KRX", StocksType.STOCK));

        String ticker = "005930";
        String memberUUID = UUID.randomUUID().toString();

        Long save = stocksManagementService.save(ticker, memberUUID);

        //when
        stocksManagementService.delete(ticker, memberUUID);

        //then
        assertThat(stocksManagementRepository.findById(save)).isEmpty();
    }

    @Test
    void 국내주식_관심_종목_제거_에러() throws Exception {
        //given
        entityManager.merge(Stocks.of("005930", "삼성전자", "KOREA", "KRX", StocksType.STOCK));

        String ticker = "005930";
        String memberUUID = UUID.randomUUID().toString();

        Long save = stocksManagementService.save(ticker, memberUUID);

        //when
        String deleteMemberUUID = "111112";

        //then
        assertThatThrownBy(() -> stocksManagementService.delete(ticker, deleteMemberUUID))
                .isInstanceOf(StockManagementAccessException.class);
    }
}