package com.example.gardenappsupports.application.engine;

import com.example.gardenappsupports.domain.enums.EntityType;
import com.example.gardenappsupports.domain.enums.ProcessType;
import com.example.gardenappsupports.domain.models.engine.DataRecord;
import com.example.gardenappsupports.domain.models.engine.EngineProcess;

import java.util.List;

public interface IEntityAssignHandler {
    EntityType getEntityType();
    void process(ProcessType process, List<DataRecord> dataRecords, EngineProcess engineProcess) throws Exception;
    void updateEngineProcess(EngineProcess engineProcess, List<DataRecord> dataRecords) throws Exception;
}
