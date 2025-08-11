package com.example.gardenappsupports.domain.repository;

import com.example.gardenappsupports.domain.models.engine.EngineProcess;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.UUID;

public interface IEngineProcessRepository extends MongoRepository<EngineProcess, UUID> {
    Optional<EngineProcess> findByProcessId(UUID processId);
}
