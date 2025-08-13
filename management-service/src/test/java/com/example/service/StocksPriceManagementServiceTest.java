package com.example.service;

import com.example.domain.Stocks;
import com.example.domain.StocksType;
import com.example.exception.NotFoundStockException;
import com.example.exception.StockPriceManagementAccessException;
import com.example.repository.StocksPriceManagementRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class StocksPriceManagementServiceTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private StocksPriceManagementService stocksPriceManagementService;

    @Autowired
    private StocksPriceManagementRepository stocksPriceManagementRepository;

    @Test
    void 국내주식_가격_알림_추가() throws Exception {
        //given
        entityManager.merge(Stocks.of("005930", "삼성전자", "KOREA", "KRX", StocksType.STOCK));

        //when
        String ticker = "005930";
        String memberUUID = UUID.randomUUID().toString();
        Double price = 100.00;

        Long save = stocksPriceManagementService.save(ticker, price, memberUUID);

        //then
        assertThat(save).isNotNull();
    }

    @Test
    void 국내주식_가격_알림_추가_에러() throws Exception {
        //given
        entityManager.merge(Stocks.of("005930", "삼성전자", "KOREA", "KRX", StocksType.STOCK));

        //when
        String ticker = "005931";
        String memberUUID = UUID.randomUUID().toString();
        Double price = 100.00;

        //then
        assertThatThrownBy(() -> stocksPriceManagementService.save(ticker, price, memberUUID))
                .isInstanceOf(NotFoundStockException.class);
    }

    @Test
    void 국내주식_관심_종목_제거() throws Exception {
        //given
        entityManager.merge(Stocks.of("005930", "삼성전자", "KOREA", "KRX", StocksType.STOCK));

        String ticker = "005930";
        String memberUUID = UUID.randomUUID().toString();
        Double price = 100.00;

        Long save = stocksPriceManagementService.save(ticker, price, memberUUID);

        //when
        stocksPriceManagementService.delete(ticker, memberUUID);

        //then
        assertThat(stocksPriceManagementRepository.findById(save)).isEmpty();
    }

    @Test
    void 국내주식_관심_종목_제거_에러() throws Exception {
        //given
        entityManager.merge(Stocks.of("005930", "삼성전자", "KOREA", "KRX", StocksType.STOCK));

        String ticker = "005930";
        String memberUUID = UUID.randomUUID().toString();
        Double price = 100.00;

        Long save = stocksPriceManagementService.save(ticker, price, memberUUID);

        //when
        String deleteMemberUUID = "111112";

        //then
        assertThatThrownBy(() -> stocksPriceManagementService.delete(ticker, deleteMemberUUID))
                .isInstanceOf(StockPriceManagementAccessException.class);
    }
}