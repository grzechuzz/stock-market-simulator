package com.example.stock.repository;

import com.example.stock.entity.BankStock;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BankStockRepository extends JpaRepository<BankStock, String> {
    List<BankStock> findAllByOrderByNameAsc();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select bankStock from BankStock bankStock where bankStock.name = :name")
    Optional<BankStock> findByNameForUpdate(String name);
}
