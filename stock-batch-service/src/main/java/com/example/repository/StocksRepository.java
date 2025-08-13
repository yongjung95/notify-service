package com.example.repository;

import com.example.domain.Stocks;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StocksRepository extends JpaRepository<Stocks, Long> {
    Optional<Stocks> findByTicker(String ticker);
}
