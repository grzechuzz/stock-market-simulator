package com.example.stock.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "audit_log")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private OperationType type;

    @Column(name = "wallet_id", nullable = false)
    private String walletId;

    @Column(name = "stock_name", nullable = false)
    private String stockName;

    @Column(name = "created_at", nullable = false, updatable = false, insertable = false)
    private Instant createdAt;

    public AuditLog(OperationType type, String walletId, String stockName) {
        validateType(type);
        validateWalletId(walletId);
        validateStockName(stockName);

        this.type = type;
        this.walletId = walletId;
        this.stockName = stockName;
    }

    private static void validateType(OperationType type) {
        if (type == null) {
            throw new IllegalArgumentException("Operation type must not be null");
        }
    }

    private static void validateWalletId(String walletId) {
        if (walletId == null || walletId.isBlank()) {
            throw new IllegalArgumentException("Wallet id must not be blank");
        }
    }

    private static void validateStockName(String stockName) {
        if (stockName == null || stockName.isBlank()) {
            throw new IllegalArgumentException("Stock name must not be blank");
        }
    }
}
