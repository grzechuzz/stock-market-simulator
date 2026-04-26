package com.example.stock.controller;

import com.example.stock.dto.audit.AuditLogResponse;
import com.example.stock.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping("/log")
    public AuditLogResponse getLogs() {
        return auditLogService.getLogs();
    }
}
