package com.example.gardenappsupports.application.entity;

import com.example.gardenappsupports.domain.models.engine.DataRecord;

import javax.xml.crypto.Data;

public interface IGardenService {
    DataRecord activateProcess(DataRecord dataRecord);
    DataRecord deactivateProcess(DataRecord dataRecord);
    DataRecord deleteProcess(DataRecord dataRecord);
}
