package com.example.gardenappsupports.infraestructure.services.entity;

import com.example.gardenappsupports.application.audit.IAuditService;
import com.example.gardenappsupports.application.entity.IGardenService;
import com.example.gardenappsupports.domain.enums.ProcessStatus;
import com.example.gardenappsupports.domain.enums.StatusEntity;
import com.example.gardenappsupports.domain.errors.Errors;
import com.example.gardenappsupports.domain.models.audit.AuditLog;
import com.example.gardenappsupports.domain.models.audit.AuditUser;
import com.example.gardenappsupports.domain.models.engine.DataRecord;
import com.example.gardenappsupports.domain.models.entity.garden.Garden;
import com.example.gardenappsupports.domain.repository.IGardenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GardenService implements IGardenService {

    private final IGardenRepository gardenRepository;
    private final IAuditService auditService;

    @Override
    public DataRecord activateProcess(DataRecord dataRecord) {
        try {
            Optional<Garden> gardenFound = gardenRepository.findByIdAndDeletedIsFalse(dataRecord.getEntityId());
            if (gardenFound.isEmpty())
                return mapDataRecord(dataRecord.getEntityId(), ProcessStatus.FAILED, Errors.garden_not_found);

            if (gardenFound.get().getStatus() == StatusEntity.ACTIVE)
                return mapDataRecord(dataRecord.getEntityId(), ProcessStatus.FAILED, Errors.garden_already_active);

            gardenFound.get().setStatus(StatusEntity.ACTIVE);
            updateData(gardenFound.get());

            auditService.saveLog(new AuditLog(
                    UUID.randomUUID(),
                    gardenFound.get().getId(),
                    "GARDEN",
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
            Optional<Garden> gardenFound = gardenRepository.findByIdAndDeletedIsFalse(dataRecord.getEntityId());
            if (gardenFound.isEmpty())
                return mapDataRecord(dataRecord.getEntityId(), ProcessStatus.FAILED, Errors.garden_not_found);

            if (gardenFound.get().getStatus() == StatusEntity.INACTIVE)
                return mapDataRecord(dataRecord.getEntityId(), ProcessStatus.FAILED, Errors.garden_already_deactivate);

            gardenFound.get().setStatus(StatusEntity.INACTIVE);
            updateData(gardenFound.get());

            auditService.saveLog(new AuditLog(
                    UUID.randomUUID(),
                    gardenFound.get().getId(),
                    "GARDEN",
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
            Optional<Garden> gardenFound = gardenRepository.findByIdAndDeletedIsFalse(dataRecord.getEntityId());
            if (gardenFound.isEmpty())
                return mapDataRecord(dataRecord.getEntityId(), ProcessStatus.FAILED, Errors.garden_not_found);

            gardenFound.get().setStatus(StatusEntity.DELETED);
            updateData(gardenFound.get());

            auditService.saveLog(new AuditLog(
                    UUID.randomUUID(),
                    gardenFound.get().getId(),
                    "GARDEN",
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

    private void updateData(Garden garden) {
        garden.setUpdatedAt(LocalDateTime.now());
        garden.setVersion(UUID.randomUUID());
        gardenRepository.save(garden);
    }

}
