package com.example.stock.exception;

public class WalletStockNotOwnedException extends StockMarketException {

    public WalletStockNotOwnedException(String stockName) {
        super("Wallet does not own stock: " + stockName);
    }
}
