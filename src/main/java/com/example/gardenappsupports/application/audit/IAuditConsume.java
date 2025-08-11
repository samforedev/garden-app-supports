package com.example.gardenappsupports.application.audit;

public interface IAuditConsume {
    void consume(String message) throws Exception;
}
