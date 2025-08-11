package com.example.gardenappsupports.infraestructure.services.entity;

import com.example.gardenappsupports.application.entity.IPlantService;
import com.example.gardenappsupports.domain.enums.ProcessStatus;
import com.example.gardenappsupports.domain.enums.StatusEntity;
import com.example.gardenappsupports.domain.errors.Errors;
import com.example.gardenappsupports.domain.models.audit.AuditLog;
import com.example.gardenappsupports.domain.models.audit.AuditUser;
import com.example.gardenappsupports.domain.models.engine.DataRecord;
import com.example.gardenappsupports.domain.models.entity.garden.Garden;
import com.example.gardenappsupports.domain.models.entity.plant.Plant;
import com.example.gardenappsupports.domain.repository.IPlantRepository;
import com.example.gardenappsupports.infraestructure.services.audit.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PlantService implements IPlantService {

    private final IPlantRepository plantRepository;
    private final AuditService auditService;

    @Override
    public DataRecord activateProcess(DataRecord dataRecord) {
        try {
            Optional<Plant> plantFound = plantRepository.findByIdAndDeletedFalse(dataRecord.getEntityId());
            if (plantFound.isEmpty())
                return mapDataRecord(dataRecord.getEntityId(), ProcessStatus.FAILED, Errors.plant_not_found);

            if (plantFound.get().getStatus() == StatusEntity.ACTIVE)
                return mapDataRecord(dataRecord.getEntityId(), ProcessStatus.FAILED, Errors.plant_already_active);

            plantFound.get().setStatus(StatusEntity.ACTIVE);
            updateData(plantFound.get());

            auditService.saveLog(new AuditLog(
                    UUID.randomUUID(),
                    plantFound.get().getId(),
                    "PLANT",
                    "ACTIVATE MANY",
                    new AuditUser("", ""),
                    LocalDateTime.now(),
                    "",
                    new HashMap<>())
            );

            return mapDataRecord(dataRecord.getEntityId(), ProcessStatus.DONE, "");
        } catch (Exception ex) {
            return mapDataRecord(dataRecord.getEntityId(), ProcessStatus.FAILED, ex.getMessage());
        }
    }

    @Override
    public DataRecord deactivateProcess(DataRecord dataRecord) {
        try {
            Optional<Plant> plantFound = plantRepository.findByIdAndDeletedFalse(dataRecord.getEntityId());
            if (plantFound.isEmpty())
                return mapDataRecord(dataRecord.getEntityId(), ProcessStatus.FAILED, Errors.plant_not_found);

            if (plantFound.get().getStatus() == StatusEntity.INACTIVE)
                return mapDataRecord(dataRecord.getEntityId(), ProcessStatus.FAILED, Errors.plant_already_deactivate);

            plantFound.get().setStatus(StatusEntity.INACTIVE);
            updateData(plantFound.get());

            auditService.saveLog(new AuditLog(
                    UUID.randomUUID(),
                    plantFound.get().getId(),
                    "PLANT",
                    "DEACTIVATE MANY",
                    new AuditUser("", ""),
                    LocalDateTime.now(),
                    "",
                    new HashMap<>())
            );

            return mapDataRecord(dataRecord.getEntityId(), ProcessStatus.DONE, "");
        } catch (Exception ex) {
            return mapDataRecord(dataRecord.getEntityId(), ProcessStatus.FAILED, ex.getMessage());
        }
    }

    @Override
    public DataRecord deleteProcess(DataRecord dataRecord) {
        try {
            Optional<Plant> plantFound = plantRepository.findByIdAndDeletedFalse(dataRecord.getEntityId());
            if (plantFound.isEmpty())
                return mapDataRecord(dataRecord.getEntityId(), ProcessStatus.FAILED, Errors.plant_not_found);

            plantFound.get().setStatus(StatusEntity.DELETED);
            updateData(plantFound.get());

            auditService.saveLog(new AuditLog(
                    UUID.randomUUID(),
                    plantFound.get().getId(),
                    "PLANT",
                    "DELETE MANY",
                    new AuditUser("", ""),
                    LocalDateTime.now(),
                    "",
                    new HashMap<>())
            );

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

    private void updateData(Plant plant) {
        plant.setUpdatedAt(LocalDateTime.now());
        plant.setVersion(UUID.randomUUID());
        plantRepository.save(plant);
    }

}
