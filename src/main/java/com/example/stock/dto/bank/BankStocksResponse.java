package com.example.stock.dto.bank;

import java.util.List;

public record BankStocksResponse(
        List<StockResponse> stocks
) {}
