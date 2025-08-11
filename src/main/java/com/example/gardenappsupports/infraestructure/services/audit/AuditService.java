package com.example.gardenappsupports.infraestructure.services.audit;

import com.example.gardenappsupports.application.audit.IAuditService;
import com.example.gardenappsupports.domain.models.audit.AuditLog;
import com.example.gardenappsupports.domain.repository.IAuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditService implements IAuditService {

    private final IAuditLogRepository auditLogRepository;

    @Override
    public void saveLog(AuditLog auditLog) {
        auditLogRepository.save(auditLog);
    }
}
