package com.example.gardenappsupports.infraestructure.services.engine;

import com.example.gardenappsupports.application.engine.IEntityAssignHandler;
import com.example.gardenappsupports.application.entity.IPlantService;
import com.example.gardenappsupports.domain.enums.EntityType;
import com.example.gardenappsupports.domain.enums.ProcessStatus;
import com.example.gardenappsupports.domain.enums.ProcessType;
import com.example.gardenappsupports.domain.models.engine.DataRecord;
import com.example.gardenappsupports.domain.models.engine.EngineProcess;
import com.example.gardenappsupports.domain.repository.IEngineProcessRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PlantAssignHandler implements IEntityAssignHandler {

    private final IPlantService plantService;
    private final IEngineProcessRepository engineProcessRepository;

    @Override
    public EntityType getEntityType() {
        return EntityType.PLANT;
    }

    @Override
    public void process(ProcessType process, List<DataRecord> dataRecords, EngineProcess engineProcess) throws Exception {
        switch (process) {
            case ACTIVE:
                List<DataRecord> activateResults = activatePlants(dataRecords);
                updateEngineProcess(engineProcess, activateResults);
                break;
            case DEACTIVATE:
                List<DataRecord> deactivateResults = deactivatePlants(dataRecords);
                updateEngineProcess(engineProcess, deactivateResults);
                break;
            case DELETE:
                List<DataRecord> deleteResults = deletePlants(dataRecords);
                updateEngineProcess(engineProcess, deleteResults);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported Process: " + process);
        }
    }

    @Override
    public void updateEngineProcess(EngineProcess engineProcess, List<DataRecord> dataRecords) throws Exception {
        boolean isFailed = dataRecords.stream()
                .anyMatch(record -> record.getStatus() == ProcessStatus.FAILED);

        ProcessStatus status = isFailed ? ProcessStatus.FAILED : ProcessStatus.COMPLETED;
        engineProcess.setStatus(status);

        engineProcess.setDataRecords(dataRecords);
        engineProcess.setProcessStatusReason("");
        engineProcessRepository.save(engineProcess);
    }

    private List<DataRecord> activatePlants(List<DataRecord> dataRecords) {
        ArrayList<DataRecord> results = new ArrayList<>();
        for (DataRecord dataRecord : dataRecords) {
            results.add(plantService.activateProcess(dataRecord));
        }
        return results;
    }

    private List<DataRecord> deactivatePlants(List<DataRecord> dataRecords) {
        ArrayList<DataRecord> results = new ArrayList<>();
        for (DataRecord dataRecord : dataRecords) {
            results.add(plantService.deactivateProcess(dataRecord));
        }
        return results;
    }

    private List<DataRecord> deletePlants(List<DataRecord> dataRecords) {
        ArrayList<DataRecord> results = new ArrayList<>();
        for (DataRecord dataRecord : dataRecords) {
            results.add(plantService.deleteProcess(dataRecord));
        }
        return results;
    }
}
