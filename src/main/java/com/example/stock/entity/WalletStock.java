package com.example.stock.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "wallet_stock")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WalletStock {

    @EmbeddedId
    private WalletStockId id;

    @MapsId("walletId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    @Column(nullable = false)
    private int quantity;

    public WalletStock(Wallet wallet, String stockName, int quantity) {
        validateWallet(wallet);
        validateStockName(stockName);
        validateQuantity(quantity);

        this.id = new WalletStockId(wallet.getId(), stockName);
        this.wallet = wallet;
        this.quantity = quantity;
    }

    public String getWalletId() {
        return id.getWalletId();
    }

    public String getStockName() {
        return id.getStockName();
    }

    public void increaseQuantity() {
        quantity++;
    }

    public void decreaseQuantity() {
        if (quantity <= 1) {
            throw new IllegalStateException("Last wallet stock unit should be removed instead of decreased");
        }

        quantity--;
    }

    public boolean hasSingleUnit() {
        return quantity == 1;
    }

    private static void validateWallet(Wallet wallet) {
        if (wallet == null) {
            throw new IllegalArgumentException("Wallet must not be null");
        }
    }

    private static void validateStockName(String stockName) {
        if (stockName == null || stockName.isBlank()) {
            throw new IllegalArgumentException("Stock name must not be blank");
        }
    }

    private static void validateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Wallet stock quantity must be positive");
        }
    }
}
