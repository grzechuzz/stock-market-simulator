package com.example.stock.exception;

public class BankStockUnavailableException extends StockMarketException {

    public BankStockUnavailableException(String stockName) {
        super("Bank has no stock available: " + stockName);
    }
}
