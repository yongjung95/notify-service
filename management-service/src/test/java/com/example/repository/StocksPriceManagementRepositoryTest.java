package com.example.repository;

import com.example.domain.StocksPriceManagement;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class StocksPriceManagementRepositoryTest {

    @Autowired
    private StocksPriceManagementRepository stocksPriceManagementRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void 주식_가격_알림_저장() throws Exception {
        //given
        String memberUUID = UUID.randomUUID().toString();
        Long stockId = 1L;
        Double price = 100.00;

        //when
        StocksPriceManagement save =
                stocksPriceManagementRepository.saveAndFlush(StocksPriceManagement.of(stockId, price, memberUUID));

        //then
        StocksPriceManagement result = entityManager.find(StocksPriceManagement.class, save.getId());

        assertThat(result.getMemberUUID()).isEqualTo(memberUUID);
    }

    @Test
    void 주식_가격_알림_삭제() throws Exception {
        //given
        String memberUUID = UUID.randomUUID().toString();
        Long stockId = 1L;
        Double price = 100.00;
        StocksPriceManagement save =
                stocksPriceManagementRepository.saveAndFlush(StocksPriceManagement.of(stockId, price, memberUUID));
        //when
        stocksPriceManagementRepository.delete(save);

        //then
        StocksPriceManagement result = entityManager.find(StocksPriceManagement.class, save.getId());

        assertThat(result).isNull();
    }
}