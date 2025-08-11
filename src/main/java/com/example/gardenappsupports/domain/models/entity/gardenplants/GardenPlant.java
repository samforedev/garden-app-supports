package com.example.gardenappsupports.domain.models.entity.gardenplants;

import com.example.gardenappsupports.domain.models.entity.plant.Plant;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import com.example.gardenappsupports.domain.models.entity.BaseEntity;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Document(collection = "gardenplants")
public class GardenPlant extends BaseEntity {
    @Indexed(unique = true)
    private String code;
    private String gardenCode;
    private Plant plant;
}
