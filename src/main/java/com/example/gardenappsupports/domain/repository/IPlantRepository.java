package com.example.gardenappsupports.domain.repository;

import com.example.gardenappsupports.domain.models.entity.plant.Plant;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.UUID;

public interface IPlantRepository extends MongoRepository<Plant, UUID> {
    Optional<Plant> findByIdAndDeletedFalse(UUID id);
}
