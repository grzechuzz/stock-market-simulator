package com.example.stock.exception;

public class UnsupportedOperationTypeException extends StockMarketException {

    public UnsupportedOperationTypeException(String type) {
        super("Unsupported operation type: " + type);
    }
}
