package com.example.stock.dto.bank;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record StockRequest(
        @NotBlank
        String name,

        @NotNull
        @PositiveOrZero
        Integer quantity
) {}
