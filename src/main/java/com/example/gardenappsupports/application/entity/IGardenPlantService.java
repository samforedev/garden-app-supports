package com.example.gardenappsupports.application.entity;

import com.example.gardenappsupports.domain.models.engine.DataRecord;

import java.util.UUID;

public interface IGardenPlantService {
    DataRecord activateProcess(String gardenCode, DataRecord dataRecord);
    DataRecord deactivateProcess(String gardenCode, DataRecord dataRecord);
    DataRecord removeProcess(String gardenCode, DataRecord dataRecord);
    DataRecord addProcess(String gardenCode, DataRecord dataRecord);
}
