package com.example.stock.dto.bank;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record SetBankStocksRequest(
        @NotNull List<@Valid StockRequest> stocks
) {}
