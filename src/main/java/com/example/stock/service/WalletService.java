package com.example.stock.service;

import com.example.stock.dto.wallet.TradeStockRequest;
import com.example.stock.dto.wallet.WalletResponse;
import com.example.stock.entity.BankStock;
import com.example.stock.entity.OperationType;
import com.example.stock.entity.Wallet;
import com.example.stock.entity.WalletStock;
import com.example.stock.entity.WalletStockId;
import com.example.stock.exception.BankStockUnavailableException;
import com.example.stock.exception.StockNotFoundException;
import com.example.stock.exception.UnsupportedOperationTypeException;
import com.example.stock.exception.WalletStockNotOwnedException;
import com.example.stock.mapper.WalletMapper;
import com.example.stock.repository.BankStockRepository;
import com.example.stock.repository.WalletRepository;
import com.example.stock.repository.WalletStockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final BankStockRepository bankStockRepository;
    private final WalletRepository walletRepository;
    private final WalletStockRepository walletStockRepository;
    private final WalletMapper walletMapper;
    private final AuditLogService auditLogService;

    @Transactional
    public void tradeStock(String walletId, String stockName, TradeStockRequest request) {
        OperationType operationType = toOperationType(request.type());

        switch (operationType) {
            case BUY -> {
                buyStock(walletId, stockName);
                auditLogService.record(operationType, walletId, stockName);
            }
            case SELL -> {
                sellStock(walletId, stockName);
                auditLogService.record(operationType, walletId, stockName);
            }
        }
    }

    @Transactional(readOnly = true)
    public WalletResponse getWallet(String walletId) {
        List<WalletStock> stocks = walletStockRepository.findStocksByWalletId(walletId);
        return walletMapper.toWalletResponse(walletId, stocks);
    }

    @Transactional(readOnly = true)
    public int getStockQuantity(String walletId, String stockName) {
        if (!bankStockRepository.existsById(stockName)) {
            throw new StockNotFoundException(stockName);
        }

        return walletStockRepository.findById(new WalletStockId(walletId, stockName))
                .map(WalletStock::getQuantity)
                .orElse(0);
    }

    private void buyStock(String walletId, String stockName) {
        BankStock bankStock = findBankStockForUpdate(stockName);

        if (bankStock.getQuantity() == 0) {
            throw new BankStockUnavailableException(stockName);
        }

        Wallet wallet = walletRepository.findById(walletId)
                .orElseGet(() -> walletRepository.save(new Wallet(walletId)));

        walletStockRepository.findByWalletIdAndStockNameForUpdate(walletId, stockName)
                .ifPresentOrElse(
                        WalletStock::increaseQuantity,
                        () -> walletStockRepository.save(new WalletStock(wallet, stockName, 1))
                );

        bankStock.decreaseQuantity();
    }

    private void sellStock(String walletId, String stockName) {
        BankStock bankStock = findBankStockForUpdate(stockName);

        WalletStock walletStock = walletStockRepository.findByWalletIdAndStockNameForUpdate(walletId, stockName)
                .orElseThrow(() -> new WalletStockNotOwnedException(stockName));

        if (walletStock.hasSingleUnit()) {
            walletStockRepository.delete(walletStock);
        } else {
            walletStock.decreaseQuantity();
        }

        bankStock.increaseQuantity();
    }

    private BankStock findBankStockForUpdate(String stockName) {
        return bankStockRepository.findByNameForUpdate(stockName)
                .orElseThrow(() -> new StockNotFoundException(stockName));
    }

    private static OperationType toOperationType(String type) {
        return switch (type) {
            case "buy" -> OperationType.BUY;
            case "sell" -> OperationType.SELL;
            default -> throw new UnsupportedOperationTypeException(type);
        };
    }
}
