package com.example.gardenappsupports.domain.repository;

import com.example.gardenappsupports.domain.models.audit.AuditLog;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface IAuditLogRepository extends MongoRepository<AuditLog, UUID> { }
