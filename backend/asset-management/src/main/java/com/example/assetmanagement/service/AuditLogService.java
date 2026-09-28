package com.example.assetmanagement.service;

import com.example.assetmanagement.entity.AuditLog;
import com.example.assetmanagement.repository.AuditLogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuditLogService {

    private final AuditLogRepository repository;

    public AuditLogService(AuditLogRepository repository) {
        this.repository = repository;
    }

    public AuditLog log(
            String username,
            String action,
            String entityType,
            Long entityId,
            String description) {

        AuditLog auditLog = new AuditLog();

        auditLog.setUsername(username);
        auditLog.setAction(action);
        auditLog.setEntityType(entityType);
        auditLog.setEntityId(entityId);
        auditLog.setDescription(description);
        auditLog.setTimestamp(LocalDateTime.now());

        return repository.save(auditLog);
    }

    public List<AuditLog> getAllLogs() {
        return repository.findAll();
    }
}
