package com.example.repository;

import com.example.domain.StocksManagement;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class StocksManagementRepositoryTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private StocksManagementRepository stocksManagementRepository;

    @Test
    void 주식_관심종목_추가() throws Exception {
        //given
        String memberUUID = "111111";
        Long americaStockId = 32L;

        //when
        StocksManagement save =
                stocksManagementRepository.saveAndFlush(StocksManagement.of(americaStockId, memberUUID));

        //then
        Optional<StocksManagement> result = stocksManagementRepository.findById(save.getId());
        assertThat(result.isPresent()).isTrue();
    }

    @Test
    void 주식_관심종목_제거() throws Exception {
        //given
        String memberUUID = "111111";
        Long americaStockId = 32L;

        StocksManagement save =
                stocksManagementRepository.saveAndFlush(StocksManagement.of(americaStockId, memberUUID));

        //when
        stocksManagementRepository.delete(save);

        //then
        Optional<StocksManagement> result = stocksManagementRepository.findById(save.getId());
        assertThat(result.isEmpty()).isTrue();
    }
}