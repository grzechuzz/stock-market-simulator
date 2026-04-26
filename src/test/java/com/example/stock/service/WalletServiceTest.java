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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

    private static final String WALLET_ID = "wallet-1";
    private static final String STOCK_NAME = "apple";

    @Mock
    private BankStockRepository bankStockRepository;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private WalletStockRepository walletStockRepository;

    @Mock
    private WalletMapper walletMapper;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private WalletService walletService;

    @Test
    void buyCreatesWalletAndWalletStockWhenWalletDoesNotExist() {
        BankStock bankStock = new BankStock(STOCK_NAME, 2);
        Wallet savedWallet = new Wallet(WALLET_ID);

        when(bankStockRepository.findByNameForUpdate(STOCK_NAME)).thenReturn(Optional.of(bankStock));
        when(walletRepository.findById(WALLET_ID)).thenReturn(Optional.empty());
        when(walletRepository.save(any(Wallet.class))).thenReturn(savedWallet);
        when(walletStockRepository.findByWalletIdAndStockNameForUpdate(WALLET_ID, STOCK_NAME))
                .thenReturn(Optional.empty());

        walletService.tradeStock(WALLET_ID, STOCK_NAME, new TradeStockRequest("buy"));

        assertThat(bankStock.getQuantity()).isEqualTo(1);
        verify(walletStockRepository).save(any(WalletStock.class));
        verify(auditLogService).record(OperationType.BUY, WALLET_ID, STOCK_NAME);
    }

    @Test
    void buyIncreasesExistingWalletStock() {
        BankStock bankStock = new BankStock(STOCK_NAME, 2);
        Wallet wallet = new Wallet(WALLET_ID);
        WalletStock walletStock = new WalletStock(wallet, STOCK_NAME, 1);

        when(bankStockRepository.findByNameForUpdate(STOCK_NAME)).thenReturn(Optional.of(bankStock));
        when(walletRepository.findById(WALLET_ID)).thenReturn(Optional.of(wallet));
        when(walletStockRepository.findByWalletIdAndStockNameForUpdate(WALLET_ID, STOCK_NAME))
                .thenReturn(Optional.of(walletStock));

        walletService.tradeStock(WALLET_ID, STOCK_NAME, new TradeStockRequest("buy"));

        assertThat(bankStock.getQuantity()).isEqualTo(1);
        assertThat(walletStock.getQuantity()).isEqualTo(2);
        verify(walletStockRepository, never()).save(any(WalletStock.class));
        verify(auditLogService).record(OperationType.BUY, WALLET_ID, STOCK_NAME);
    }

    @Test
    void buyFailsWhenStockDoesNotExist() {
        when(bankStockRepository.findByNameForUpdate(STOCK_NAME)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> walletService.tradeStock(WALLET_ID, STOCK_NAME, new TradeStockRequest("buy")))
                .isInstanceOf(StockNotFoundException.class)
                .hasMessage("Stock not found: apple");

        verifyNoInteractions(walletRepository, walletStockRepository, auditLogService);
    }

    @Test
    void buyFailsWhenBankStockIsUnavailable() {
        BankStock bankStock = new BankStock(STOCK_NAME, 0);

        when(bankStockRepository.findByNameForUpdate(STOCK_NAME)).thenReturn(Optional.of(bankStock));

        assertThatThrownBy(() -> walletService.tradeStock(WALLET_ID, STOCK_NAME, new TradeStockRequest("buy")))
                .isInstanceOf(BankStockUnavailableException.class)
                .hasMessage("Bank has no stock available: apple");

        verifyNoInteractions(walletRepository, walletStockRepository, auditLogService);
    }

    @Test
    void sellDecreasesWalletStockAndIncreasesBankStock() {
        BankStock bankStock = new BankStock(STOCK_NAME, 2);
        WalletStock walletStock = new WalletStock(new Wallet(WALLET_ID), STOCK_NAME, 3);

        when(bankStockRepository.findByNameForUpdate(STOCK_NAME)).thenReturn(Optional.of(bankStock));
        when(walletStockRepository.findByWalletIdAndStockNameForUpdate(WALLET_ID, STOCK_NAME))
                .thenReturn(Optional.of(walletStock));

        walletService.tradeStock(WALLET_ID, STOCK_NAME, new TradeStockRequest("sell"));

        assertThat(bankStock.getQuantity()).isEqualTo(3);
        assertThat(walletStock.getQuantity()).isEqualTo(2);
        verify(walletStockRepository, never()).delete(any(WalletStock.class));
        verify(auditLogService).record(OperationType.SELL, WALLET_ID, STOCK_NAME);
    }

    @Test
    void sellDeletesWalletStockWhenSellingLastUnit() {
        BankStock bankStock = new BankStock(STOCK_NAME, 2);
        WalletStock walletStock = new WalletStock(new Wallet(WALLET_ID), STOCK_NAME, 1);

        when(bankStockRepository.findByNameForUpdate(STOCK_NAME)).thenReturn(Optional.of(bankStock));
        when(walletStockRepository.findByWalletIdAndStockNameForUpdate(WALLET_ID, STOCK_NAME))
                .thenReturn(Optional.of(walletStock));

        walletService.tradeStock(WALLET_ID, STOCK_NAME, new TradeStockRequest("sell"));

        assertThat(bankStock.getQuantity()).isEqualTo(3);
        verify(walletStockRepository).delete(walletStock);
        verify(auditLogService).record(OperationType.SELL, WALLET_ID, STOCK_NAME);
    }

    @Test
    void sellFailsWhenWalletDoesNotOwnStock() {
        BankStock bankStock = new BankStock(STOCK_NAME, 2);

        when(bankStockRepository.findByNameForUpdate(STOCK_NAME)).thenReturn(Optional.of(bankStock));
        when(walletStockRepository.findByWalletIdAndStockNameForUpdate(WALLET_ID, STOCK_NAME))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> walletService.tradeStock(WALLET_ID, STOCK_NAME, new TradeStockRequest("sell")))
                .isInstanceOf(WalletStockNotOwnedException.class)
                .hasMessage("Wallet does not own stock: apple");

        assertThat(bankStock.getQuantity()).isEqualTo(2);
        verifyNoInteractions(auditLogService);
    }

    @Test
    void tradeStockFailsForUnsupportedOperationType() {
        assertThatThrownBy(() -> walletService.tradeStock(WALLET_ID, STOCK_NAME, new TradeStockRequest("hold")))
                .isInstanceOf(UnsupportedOperationTypeException.class)
                .hasMessage("Unsupported operation type: hold");

        verifyNoInteractions(bankStockRepository, walletRepository, walletStockRepository, auditLogService);
    }

    @Test
    void getWalletReturnsMappedWalletStocks() {
        List<WalletStock> walletStocks = List.of(new WalletStock(new Wallet(WALLET_ID), STOCK_NAME, 2));
        WalletResponse response = new WalletResponse(WALLET_ID, List.of());

        when(walletStockRepository.findStocksByWalletId(WALLET_ID)).thenReturn(walletStocks);
        when(walletMapper.toWalletResponse(WALLET_ID, walletStocks)).thenReturn(response);

        WalletResponse result = walletService.getWallet(WALLET_ID);

        assertThat(result).isSameAs(response);
    }

    @Test
    void getStockQuantityReturnsExistingQuantity() {
        WalletStock walletStock = new WalletStock(new Wallet(WALLET_ID), STOCK_NAME, 2);

        when(bankStockRepository.existsById(STOCK_NAME)).thenReturn(true);
        when(walletStockRepository.findById(new WalletStockId(WALLET_ID, STOCK_NAME)))
                .thenReturn(Optional.of(walletStock));

        int quantity = walletService.getStockQuantity(WALLET_ID, STOCK_NAME);

        assertThat(quantity).isEqualTo(2);
    }

    @Test
    void getStockQuantityReturnsZeroWhenWalletDoesNotOwnExistingStock() {
        when(bankStockRepository.existsById(STOCK_NAME)).thenReturn(true);
        when(walletStockRepository.findById(new WalletStockId(WALLET_ID, STOCK_NAME)))
                .thenReturn(Optional.empty());

        int quantity = walletService.getStockQuantity(WALLET_ID, STOCK_NAME);

        assertThat(quantity).isZero();
    }

    @Test
    void getStockQuantityFailsWhenStockDoesNotExist() {
        when(bankStockRepository.existsById(STOCK_NAME)).thenReturn(false);

        assertThatThrownBy(() -> walletService.getStockQuantity(WALLET_ID, STOCK_NAME))
                .isInstanceOf(StockNotFoundException.class)
                .hasMessage("Stock not found: apple");

        verifyNoInteractions(walletStockRepository);
    }
}
