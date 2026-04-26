package com.example.stock.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WalletStockId implements Serializable {

    @Column(name = "wallet_id", nullable = false)
    private String walletId;

    @Column(name = "stock_name", nullable = false)
    private String stockName;

    public WalletStockId(String walletId, String stockName) {
        validateWalletId(walletId);
        validateStockName(stockName);

        this.walletId = walletId;
        this.stockName = stockName;
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
