package com.example.stock.repository;

import com.example.stock.entity.WalletStock;
import com.example.stock.entity.WalletStockId;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface WalletStockRepository extends JpaRepository<WalletStock, WalletStockId> {

    @Query("""
            select walletStock from WalletStock walletStock
            where walletStock.id.walletId = :walletId
            order by walletStock.id.stockName asc
            """)
    List<WalletStock> findStocksByWalletId(String walletId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select walletStock from WalletStock walletStock
            where walletStock.id.walletId = :walletId and walletStock.id.stockName = :stockName
            """)
    Optional<WalletStock> findByWalletIdAndStockNameForUpdate(String walletId, String stockName);
}
