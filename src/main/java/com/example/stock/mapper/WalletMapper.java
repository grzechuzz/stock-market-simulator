package com.example.stock.mapper;

import com.example.stock.dto.wallet.WalletResponse;
import com.example.stock.dto.wallet.WalletStockResponse;
import com.example.stock.entity.WalletStock;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface WalletMapper {

    @Mapping(target = "name", source = "stockName")
    WalletStockResponse toStockResponse(WalletStock walletStock);

    List<WalletStockResponse> toStockResponses(List<WalletStock> walletStocks);

    default WalletResponse toWalletResponse(String walletId, List<WalletStock> walletStocks) {
        return new WalletResponse(walletId, toStockResponses(walletStocks));
    }
}
