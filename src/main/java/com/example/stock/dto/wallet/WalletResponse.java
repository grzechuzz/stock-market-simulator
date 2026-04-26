package com.example.stock.dto.wallet;

import java.util.List;

public record WalletResponse(
        String id,
        List<WalletStockResponse> stocks
) {}
