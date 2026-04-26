package com.example.stock.exception;

public class StockNotFoundException extends StockMarketException {

    public StockNotFoundException(String stockName) {
        super("Stock not found: " + stockName);
    }
}
