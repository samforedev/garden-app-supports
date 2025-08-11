package com.example.gardenappsupports.infraestructure.services.engine;

import com.example.gardenappsupports.application.engine.IEntityAssignHandler;
import com.example.gardenappsupports.application.entity.IGardenPlantService;
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
public class GardenPlantAssignHandler implements IEntityAssignHandler {

    private final IGardenPlantService gardenPlantService;
    private final IEngineProcessRepository engineProcessRepository;

    @Override
    public EntityType getEntityType() {
        return EntityType.GARDEN_PLANT;
    }

    @Override
    public void process(ProcessType process, List<DataRecord> dataRecords, EngineProcess engineProcess) throws Exception {

        String gardenCode = engineProcess.getExtraInfo().get("GARDEN_CODE");
        if (gardenCode == null) {
            engineProcess.setStatus(ProcessStatus.FAILED);
            engineProcess.setDataRecords(dataRecords);
            engineProcess.setProcessStatusReason("Garden code is null");
            engineProcessRepository.save(engineProcess);
            return;
        }

        switch (process) {
            case ACTIVE:
                List<DataRecord> activateResults = activateGardenPlants(dataRecords, gardenCode);
                updateEngineProcess(engineProcess, activateResults);
                break;
            case DEACTIVATE:
                List<DataRecord> deactivateResults = deactivateGardenPlants(dataRecords, gardenCode);
                updateEngineProcess(engineProcess, deactivateResults);
                break;
            case REMOVE:
                List<DataRecord> removeResults = removeGardenPlants(dataRecords, gardenCode);
                updateEngineProcess(engineProcess, removeResults);
                break;
            case ADD:
                List<DataRecord> addResults = addGardenPlants(dataRecords, gardenCode);
                updateEngineProcess(engineProcess, addResults);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported Process: " + process);
        }
    }

    @Override
    public void updateEngineProcess(EngineProcess engineProcess, List<DataRecord> dataRecords) throws Exception {
        boolean isFailed = dataRecords.stream()
                .anyMatch(record -> record.getStatus() == ProcessStatus.FAILED);

        ProcessStatus status = isFailed ? ProcessStatus.COMPLETED_WITH_ERROR : ProcessStatus.COMPLETED;
        engineProcess.setStatus(status);

        engineProcess.setDataRecords(dataRecords);
        engineProcess.setProcessStatusReason("");
        engineProcessRepository.save(engineProcess);
    }

    private List<DataRecord> activateGardenPlants(List<DataRecord> dataRecords, String gardenCode) {
        ArrayList<DataRecord> results = new ArrayList<>();
        for (DataRecord dataRecord : dataRecords) {
            results.add(gardenPlantService.activateProcess(gardenCode, dataRecord));
        }
        return results;
    }

    private List<DataRecord> deactivateGardenPlants(List<DataRecord> dataRecords, String gardenCode) {
        ArrayList<DataRecord> results = new ArrayList<>();
        for (DataRecord dataRecord : dataRecords) {
            results.add(gardenPlantService.deactivateProcess(gardenCode, dataRecord));
        }
        return results;
    }

    private List<DataRecord> removeGardenPlants(List<DataRecord> dataRecords, String gardenCode) {
        ArrayList<DataRecord> results = new ArrayList<>();
        for (DataRecord dataRecord : dataRecords) {
            results.add(gardenPlantService.removeProcess(gardenCode, dataRecord));
        }
        return results;
    }

    private List<DataRecord> addGardenPlants(List<DataRecord> dataRecords, String gardenCode) {
        ArrayList<DataRecord> results = new ArrayList<>();
        for (DataRecord dataRecord : dataRecords) {
            results.add(gardenPlantService.addProcess(gardenCode, dataRecord));
        }
        return results;
    }
}
