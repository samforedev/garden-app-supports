package com.example.gardenappsupports.domain.repository;

import com.example.gardenappsupports.domain.models.entity.garden.Garden;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.UUID;

public interface IGardenRepository extends MongoRepository<Garden, UUID> {
    Optional<Garden> findByIdAndDeletedIsFalse(UUID id);
}
