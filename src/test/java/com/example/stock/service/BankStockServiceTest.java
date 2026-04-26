package com.example.stock.service;

import com.example.stock.dto.bank.BankStocksResponse;
import com.example.stock.dto.bank.SetBankStocksRequest;
import com.example.stock.dto.bank.StockRequest;
import com.example.stock.dto.bank.StockResponse;
import com.example.stock.entity.BankStock;
import com.example.stock.exception.DuplicateStockNameException;
import com.example.stock.mapper.BankStockMapper;
import com.example.stock.repository.BankStockRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BankStockServiceTest {

    @Mock
    private BankStockRepository bankStockRepository;

    @Mock
    private BankStockMapper bankStockMapper;

    @InjectMocks
    private BankStockService bankStockService;

    @Test
    void getStocksReturnsMappedStocksOrderedByRepository() {
        List<BankStock> stocks = List.of(
                new BankStock("apple", 10),
                new BankStock("tesla", 3)
        );
        BankStocksResponse response = new BankStocksResponse(List.of(
                new StockResponse("apple", 10),
                new StockResponse("tesla", 3)
        ));

        when(bankStockRepository.findAllByOrderByNameAsc()).thenReturn(stocks);
        when(bankStockMapper.toBankStocksResponse(stocks)).thenReturn(response);

        BankStocksResponse result = bankStockService.getStocks();

        assertThat(result).isSameAs(response);
    }

    @Test
    void setStocksReplacesCurrentBankState() {
        SetBankStocksRequest request = new SetBankStocksRequest(List.of(
                new StockRequest("apple", 10),
                new StockRequest("tesla", 3)
        ));
        List<BankStock> stocks = List.of(
                new BankStock("apple", 10),
                new BankStock("tesla", 3)
        );

        when(bankStockMapper.toEntities(request.stocks())).thenReturn(stocks);

        bankStockService.setStocks(request);

        verify(bankStockRepository).deleteAllInBatch();
        verify(bankStockRepository).saveAll(stocks);
    }

    @Test
    void setStocksRejectsDuplicateStockNamesBeforeModifyingBankState() {
        SetBankStocksRequest request = new SetBankStocksRequest(List.of(
                new StockRequest("apple", 10),
                new StockRequest("apple", 3)
        ));

        assertThatThrownBy(() -> bankStockService.setStocks(request))
                .isInstanceOf(DuplicateStockNameException.class)
                .hasMessage("Duplicate stock name: apple");

        verifyNoInteractions(bankStockMapper, bankStockRepository);
    }
}
