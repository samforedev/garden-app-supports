package com.example.gardenappsupports.application.audit;

import com.example.gardenappsupports.domain.models.audit.AuditLog;

public interface IAuditService {
    void saveLog(AuditLog auditLog);
}
