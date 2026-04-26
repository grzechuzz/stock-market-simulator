package com.example.stock.controller;

import com.example.stock.dto.bank.BankStocksResponse;
import com.example.stock.dto.bank.SetBankStocksRequest;
import com.example.stock.service.BankStockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BankStockController {

    private final BankStockService bankStockService;

    @GetMapping("/stocks")
    public BankStocksResponse getStocks() {
        return bankStockService.getStocks();
    }

    @PostMapping("/stocks")
    public ResponseEntity<Void> setStocks(@Valid @RequestBody SetBankStocksRequest request) {
        bankStockService.setStocks(request);
        return ResponseEntity.ok().build();
    }
}
