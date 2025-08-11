package com.example.gardenappsupports.domain.models.entity.garden;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class GardenProperties {
    private double area;
    private Map<String, String> extraInfo;
}
