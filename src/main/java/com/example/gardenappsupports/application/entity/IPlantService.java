package com.example.gardenappsupports.application.entity;

import com.example.gardenappsupports.domain.models.engine.DataRecord;

public interface IPlantService {
    DataRecord activateProcess(DataRecord dataRecord);
    DataRecord deactivateProcess(DataRecord dataRecord);
    DataRecord deleteProcess(DataRecord dataRecord);
}
