package com.example.stock.service;

import com.example.stock.dto.audit.AuditLogEntryResponse;
import com.example.stock.dto.audit.AuditLogResponse;
import com.example.stock.entity.AuditLog;
import com.example.stock.entity.OperationType;
import com.example.stock.mapper.AuditLogMapper;
import com.example.stock.repository.AuditLogRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuditLogServiceTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    @Mock
    private AuditLogMapper auditLogMapper;

    @InjectMocks
    private AuditLogService auditLogService;

    @Test
    void recordPersistsAuditLogEntry() {
        auditLogService.record(OperationType.BUY, "wallet-1", "apple");

        ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
        verify(auditLogRepository).save(captor.capture());

        AuditLog savedLog = captor.getValue();
        assertThat(savedLog.getType()).isEqualTo(OperationType.BUY);
        assertThat(savedLog.getWalletId()).isEqualTo("wallet-1");
        assertThat(savedLog.getStockName()).isEqualTo("apple");
    }

    @Test
    void getLogsReturnsMappedLogsInRepositoryOrder() {
        List<AuditLog> logs = List.of(
                new AuditLog(OperationType.BUY, "wallet-1", "apple"),
                new AuditLog(OperationType.SELL, "wallet-1", "apple")
        );
        AuditLogResponse response = new AuditLogResponse(List.of(
                new AuditLogEntryResponse("buy", "wallet-1", "apple"),
                new AuditLogEntryResponse("sell", "wallet-1", "apple")
        ));

        when(auditLogRepository.findAllByOrderByIdAsc()).thenReturn(logs);
        when(auditLogMapper.toAuditLogResponse(logs)).thenReturn(response);

        AuditLogResponse result = auditLogService.getLogs();

        assertThat(result).isSameAs(response);
    }
}
