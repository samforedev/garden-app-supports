package com.example.gardenappsupports.infraestructure.services.queue;

import com.example.gardenappsupports.application.audit.IAuditConsume;
import com.example.gardenappsupports.application.audit.IAuditService;
import com.example.gardenappsupports.domain.models.audit.AuditLog;
import com.example.gardenappsupports.domain.repository.IAuditLogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuditLogConsume implements IAuditConsume {

    private final ObjectMapper objectMapper;
    private final IAuditService auditService;

    @Override
    @RabbitListener(queues = "${rabbitmq.audit.queue}")
    public void consume(String message) throws Exception {
        var log = objectMapper.readValue(message, AuditLog.class);
        auditService.saveLog(log);
    }
}
