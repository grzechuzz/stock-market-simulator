package com.example.stock.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(StockNotFoundException.class)
    public ProblemDetail handleStockNotFound(StockNotFoundException exception) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problem.setTitle("Stock not found");
        problem.setDetail(exception.getMessage());
        return problem;
    }

    @ExceptionHandler({
            BankStockUnavailableException.class,
            DuplicateStockNameException.class,
            UnsupportedOperationTypeException.class,
            WalletStockNotOwnedException.class
    })
    public ProblemDetail handleBadRequest(StockMarketException exception) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Bad request");
        problem.setDetail(exception.getMessage());
        return problem;
    }
}
