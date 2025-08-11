package com.example.gardenappsupports.domain.repository;

import com.example.gardenappsupports.domain.models.entity.gardenplants.GardenPlant;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IGardenPlantRepository extends MongoRepository<GardenPlant, UUID> {
    Optional<GardenPlant> findByIdAndDeletedIsFalse(UUID id);
    List<GardenPlant> findAllByGardenCodeAndDeletedIsFalse(String gardenCode);
    GardenPlant findFirstByGardenCodeOrderByCreatedAtDesc(String gardenCode);
}
