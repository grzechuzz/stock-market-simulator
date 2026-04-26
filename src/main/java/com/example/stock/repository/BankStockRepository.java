package com.example.stock.repository;

import com.example.stock.entity.BankStock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BankStockRepository extends JpaRepository<BankStock, String> {
    List<BankStock> findAllByOrderByNameAsc();
}
