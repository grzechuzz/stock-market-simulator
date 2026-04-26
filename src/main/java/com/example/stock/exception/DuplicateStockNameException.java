package com.example.stock.exception;

public class DuplicateStockNameException extends StockMarketException {

    public DuplicateStockNameException(String stockName) {
        super("Duplicate stock name: " + stockName);
    }
}
