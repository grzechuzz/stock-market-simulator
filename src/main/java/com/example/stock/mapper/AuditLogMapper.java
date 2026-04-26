package com.example.stock.mapper;

import com.example.stock.dto.audit.AuditLogEntryResponse;
import com.example.stock.dto.audit.AuditLogResponse;
import com.example.stock.entity.AuditLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AuditLogMapper {

    @Mapping(target = "type", expression = "java(auditLog.getType().name().toLowerCase())")
    AuditLogEntryResponse toResponse(AuditLog auditLog);

    List<AuditLogEntryResponse> toResponses(List<AuditLog> auditLogs);

    default AuditLogResponse toAuditLogResponse(List<AuditLog> auditLogs) {
        return new AuditLogResponse(toResponses(auditLogs));
    }
}
