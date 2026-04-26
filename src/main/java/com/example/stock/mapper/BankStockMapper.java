package com.example.stock.mapper;

import com.example.stock.dto.bank.BankStocksResponse;
import com.example.stock.dto.bank.StockRequest;
import com.example.stock.dto.bank.StockResponse;
import com.example.stock.entity.BankStock;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BankStockMapper {
    BankStock toEntity(StockRequest stockRequest);

    List<BankStock> toEntities(List<StockRequest> stockRequests);

    StockResponse toResponse(BankStock bankStock);

    List<StockResponse> toResponses(List<BankStock> bankStocks);

    default BankStocksResponse toBankStocksResponse(List<BankStock> bankStocks) {
        return new BankStocksResponse(toResponses(bankStocks));
    }
}
