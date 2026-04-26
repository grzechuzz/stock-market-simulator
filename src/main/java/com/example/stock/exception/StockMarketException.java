package com.example.stock.exception;

public abstract class StockMarketException extends RuntimeException {

    protected StockMarketException(String message) {
        super(message);
    }
}
