package com.example.stock.service;

import com.example.stock.dto.bank.BankStocksResponse;
import com.example.stock.dto.bank.SetBankStocksRequest;
import com.example.stock.entity.BankStock;
import com.example.stock.exception.DuplicateStockNameException;
import com.example.stock.mapper.BankStockMapper;
import com.example.stock.repository.BankStockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BankStockService {
    private final BankStockRepository bankStockRepository;
    private final BankStockMapper bankStockMapper;

    @Transactional(readOnly = true)
    public BankStocksResponse getStocks() {
        List<BankStock> bankStocks = bankStockRepository.findAllByOrderByNameAsc();
        return bankStockMapper.toBankStocksResponse(bankStocks);
    }

    @Transactional
    public void setStocks(SetBankStocksRequest request) {
        validateUniqueStockNames(request);
        List<BankStock> bankStocks = bankStockMapper.toEntities(request.stocks());

        bankStockRepository.deleteAllInBatch();
        bankStockRepository.saveAll(bankStocks);
    }

    private static void validateUniqueStockNames(SetBankStocksRequest request) {
        Set<String> stockNames = new HashSet<>();

        for (var stock : request.stocks()) {
            if (!stockNames.add(stock.name())) {
                throw new DuplicateStockNameException(stock.name());
            }
        }
    }
}
