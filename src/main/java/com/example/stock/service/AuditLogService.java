package com.example.stock.service;

import com.example.stock.dto.audit.AuditLogResponse;
import com.example.stock.entity.AuditLog;
import com.example.stock.entity.OperationType;
import com.example.stock.mapper.AuditLogMapper;
import com.example.stock.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final AuditLogMapper auditLogMapper;

    @Transactional
    public void record(OperationType type, String walletId, String stockName) {
        auditLogRepository.save(new AuditLog(type, walletId, stockName));
    }

    @Transactional(readOnly = true)
    public AuditLogResponse getLogs() {
        return auditLogMapper.toAuditLogResponse(auditLogRepository.findAllByOrderByIdAsc());
    }
}
