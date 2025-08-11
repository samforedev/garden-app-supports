package com.example.gardenappsupports.infraestructure.services.entity;

import com.example.gardenappsupports.application.audit.IAuditService;
import com.example.gardenappsupports.application.entity.IGardenPlantService;
import com.example.gardenappsupports.domain.enums.ProcessStatus;
import com.example.gardenappsupports.domain.enums.StatusEntity;
import com.example.gardenappsupports.domain.errors.Errors;
import com.example.gardenappsupports.domain.models.audit.AuditLog;
import com.example.gardenappsupports.domain.models.audit.AuditUser;
import com.example.gardenappsupports.domain.models.engine.DataRecord;
import com.example.gardenappsupports.domain.models.entity.gardenplants.GardenPlant;
import com.example.gardenappsupports.domain.models.entity.plant.Plant;
import com.example.gardenappsupports.domain.repository.IGardenPlantRepository;
import com.example.gardenappsupports.domain.repository.IPlantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class GardenPlantService implements IGardenPlantService {

    private final IGardenPlantRepository gardenPlantRepository;
    private final IPlantRepository plantRepository;
    private final IAuditService auditService;

    @Override
    public DataRecord activateProcess(String gardenCode, DataRecord dataRecord) {
        try {
            Optional<GardenPlant> gardenPlantFound = gardenPlantRepository
                    .findByIdAndDeletedIsFalse(dataRecord.getEntityId());
            if (gardenPlantFound.isEmpty())
                return mapDataRecord(dataRecord.getEntityId(), ProcessStatus.FAILED, Errors.GardenPlant_not_found);
            GardenPlant gardenPlant = gardenPlantFound.get();

            if (!Objects.equals(gardenPlant.getGardenCode(), gardenCode))
                return mapDataRecord(dataRecord.getEntityId(), ProcessStatus.FAILED, Errors.GardenPlant_has_not_garden);

            if (gardenPlant.getStatus() == StatusEntity.ACTIVE)
                return mapDataRecord(dataRecord.getEntityId(), ProcessStatus.FAILED, Errors.GardenPlant_already_active);

            gardenPlant.setStatus(StatusEntity.ACTIVE);
            updateData(gardenPlant);

            auditService.saveLog(new AuditLog(
                    UUID.randomUUID(),
                    gardenPlant.getId(),
                    "GARDEN PLANT",
                    "ACTIVATE MANY",
                    new AuditUser("", ""),
                    LocalDateTime.now(),
                    "",
                    Map.of("GARDEN_CODE",
                            Objects.requireNonNullElse(gardenCode, ""))
            ));

            return mapDataRecord(dataRecord.getEntityId(), ProcessStatus.DONE, "");
        } catch (Exception ex) {
            return mapDataRecord(dataRecord.getEntityId(), ProcessStatus.FAILED, ex.getMessage());
        }
    }

    @Override
    public DataRecord deactivateProcess(String gardenCode, DataRecord dataRecord) {
        try {
            Optional<GardenPlant> gardenPlantFound = gardenPlantRepository
                    .findByIdAndDeletedIsFalse(dataRecord.getEntityId());
            if (gardenPlantFound.isEmpty())
                return mapDataRecord(dataRecord.getEntityId(), ProcessStatus.FAILED, Errors.GardenPlant_not_found);
            GardenPlant gardenPlant = gardenPlantFound.get();

            if (!Objects.equals(gardenPlant.getGardenCode(), gardenCode))
                return mapDataRecord(dataRecord.getEntityId(), ProcessStatus.FAILED, Errors.GardenPlant_has_not_garden);

            if (gardenPlant.getStatus() == StatusEntity.INACTIVE)
                return mapDataRecord(dataRecord.getEntityId(), ProcessStatus.FAILED, Errors.GardenPlant_already_deactivate);

            gardenPlant.setStatus(StatusEntity.INACTIVE);
            updateData(gardenPlant);

            auditService.saveLog(new AuditLog(
                    UUID.randomUUID(),
                    gardenPlant.getId(),
                    "GARDEN PLANT",
                    "DEACTIVATE MANY",
                    new AuditUser("", ""),
                    LocalDateTime.now(),
                    "",
                    Map.of("GARDEN_CODE",
                            Objects.requireNonNullElse(gardenCode, ""))
            ));

            return mapDataRecord(dataRecord.getEntityId(), ProcessStatus.DONE, "");
        } catch (Exception ex) {
            return mapDataRecord(dataRecord.getEntityId(), ProcessStatus.FAILED, ex.getMessage());
        }
    }

    @Override
    public DataRecord removeProcess(String gardenCode, DataRecord dataRecord) {
        try {
            Optional<GardenPlant> gardenPlantFound = gardenPlantRepository
                    .findByIdAndDeletedIsFalse(dataRecord.getEntityId());
            if (gardenPlantFound.isEmpty())
                return mapDataRecord(dataRecord.getEntityId(), ProcessStatus.FAILED, Errors.GardenPlant_not_found);
            GardenPlant gardenPlant = gardenPlantFound.get();

            if (!Objects.equals(gardenPlant.getGardenCode(), gardenCode))
                return mapDataRecord(dataRecord.getEntityId(), ProcessStatus.FAILED, Errors.GardenPlant_has_not_garden);

            gardenPlant.setStatus(StatusEntity.DELETED);
            gardenPlant.setDeleted(true);
            updateData(gardenPlant);

            auditService.saveLog(new AuditLog(
                    UUID.randomUUID(),
                    gardenPlant.getId(),
                    "GARDEN PLANT",
                    "REMOVE MANY",
                    new AuditUser("", ""),
                    LocalDateTime.now(),
                    "",
                    Map.of("GARDEN_CODE",
                            Objects.requireNonNullElse(gardenCode, ""))
            ));

            return mapDataRecord(dataRecord.getEntityId(), ProcessStatus.DONE, "");
        } catch (Exception ex) {
            return mapDataRecord(dataRecord.getEntityId(), ProcessStatus.FAILED, ex.getMessage());
        }
    }

    @Override
    public DataRecord addProcess(String gardenCode, DataRecord dataRecord) {
        try {

            Optional<Plant> plantFound = plantRepository.findByIdAndDeletedFalse(dataRecord.getEntityId());
            if (plantFound.isEmpty())
                return mapDataRecord(dataRecord.getEntityId(), ProcessStatus.FAILED, Errors.plant_not_found);
            Plant plant = plantFound.get();

            List<GardenPlant> gardenPlants = gardenPlantRepository.findAllByGardenCodeAndDeletedIsFalse(gardenCode);
            boolean isPresentPlant = gardenPlants.stream()
                    .anyMatch(p -> p.getPlant().getCode().equals(plant.getCode()));

            if (isPresentPlant)
                return mapDataRecord(dataRecord.getEntityId(), ProcessStatus.FAILED, Errors.GardenPlant_already_include);

            GardenPlant gardenPlantCreated = GardenPlant.builder()
                    .code(generateGardenPlantCode(gardenCode))
                    .gardenCode(gardenCode)
                    .plant(Plant.builder()
                            .id(plant.getId())
                            .code(plant.getCode())
                            .name(plant.getName())
                            .description(plant.getDescription())
                            .plantType(plant.getPlantType())
                            .properties(plant.getProperties())
                            .createdAt(plant.getCreatedAt())
                            .updatedAt(plant.getUpdatedAt())
                            .version(plant.getVersion())
                            .build())
                    .build();

            gardenPlantCreated.setId(UUID.randomUUID());
            gardenPlantCreated.setCreatedAt(LocalDateTime.now());
            gardenPlantCreated.setUpdatedAt(LocalDateTime.now());
            gardenPlantCreated.setVersion(UUID.randomUUID());
            gardenPlantRepository.save(gardenPlantCreated);

            auditService.saveLog(new AuditLog(
                    UUID.randomUUID(),
                    gardenPlantCreated.getId(),
                    "GARDEN PLANT",
                    "ADD MANY",
                    new AuditUser("", ""),
                    LocalDateTime.now(),
                    "",
                    Map.of("GARDEN_CODE",
                            Objects.requireNonNullElse(gardenCode, ""))
            ));

            return mapDataRecord(dataRecord.getEntityId(), ProcessStatus.DONE, "");
        } catch (Exception ex) {
            return mapDataRecord(dataRecord.getEntityId(), ProcessStatus.FAILED, ex.getMessage());
        }
    }

    private DataRecord mapDataRecord(UUID entityId, ProcessStatus status, String reason) {
        return DataRecord.builder()
                .entityId(entityId)
                .status(status)
                .statusReason(reason)
                .build();
    }

    private void updateData(GardenPlant gardenPlant) {
        gardenPlant.setUpdatedAt(LocalDateTime.now());
        gardenPlant.setVersion(UUID.randomUUID());
        gardenPlantRepository.save(gardenPlant);
    }

    private String generateGardenPlantCode(String gardenCode) {
        GardenPlant lastGardenPlant = gardenPlantRepository.findFirstByGardenCodeOrderByCreatedAtDesc(gardenCode);

        int nextNumber = 1;
        if (lastGardenPlant != null) {
            String[] codeParts =  lastGardenPlant.getCode().split("\\.");
            String lastCode = codeParts[1];
            nextNumber = Integer.parseInt(lastCode) + 1;
        }

        String formattedNumber = String.format("%04d", nextNumber);
        return gardenCode +"."+formattedNumber;
    }

}
