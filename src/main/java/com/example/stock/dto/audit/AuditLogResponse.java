package com.example.stock.dto.audit;

import java.util.List;

public record AuditLogResponse(
        List<AuditLogEntryResponse> log
) {}
