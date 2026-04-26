package com.example.stock.controller;

import com.example.stock.dto.wallet.TradeStockRequest;
import com.example.stock.dto.wallet.WalletResponse;
import com.example.stock.service.WalletService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @PostMapping("/wallets/{walletId}/stocks/{stockName}")
    public ResponseEntity<Void> tradeStock(
            @PathVariable @NotBlank String walletId,
            @PathVariable @NotBlank String stockName,
            @Valid @RequestBody TradeStockRequest request
    ) {
        walletService.tradeStock(walletId, stockName, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/wallets/{walletId}")
    public WalletResponse getWallet(@PathVariable @NotBlank String walletId) {
        return walletService.getWallet(walletId);
    }

    @GetMapping("/wallets/{walletId}/stocks/{stockName}")
    public Integer getStockQuantity(
            @PathVariable @NotBlank String walletId,
            @PathVariable @NotBlank String stockName
    ) {
        return walletService.getStockQuantity(walletId, stockName);
    }
}
