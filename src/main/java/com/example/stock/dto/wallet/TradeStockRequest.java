package com.example.stock.dto.wallet;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record TradeStockRequest(
        @NotBlank
        @Pattern(regexp = "buy|sell")
        String type
) {}
