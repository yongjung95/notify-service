package com.example.repository;

import com.example.domain.StocksPriceManagement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StocksPriceManagementRepository extends JpaRepository<StocksPriceManagement, Long> {
    Optional<StocksPriceManagement> findByStockIdAndMemberUUID(Long stockId, String memberUUID);
}
