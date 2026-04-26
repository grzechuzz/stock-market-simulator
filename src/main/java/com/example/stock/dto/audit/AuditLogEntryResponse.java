package com.example.stock.dto.audit;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AuditLogEntryResponse(
        String type,

        @JsonProperty("wallet_id")
        String walletId,

        @JsonProperty("stock_name")
        String stockName
) {}
