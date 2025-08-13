package com.example.repository;

import com.example.domain.StocksManagement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StocksManagementRepository extends JpaRepository<StocksManagement, Long> {
    Optional<StocksManagement> findByStocksIdAndMemberUUID(Long stocksId, String memberUUID);
}
