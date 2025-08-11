package com.example.gardenappsupports.infraestructure.services.engine;

import com.example.gardenappsupports.application.engine.IEntityAssignHandler;
import com.example.gardenappsupports.domain.enums.EntityType;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AssignHandlerRegistry {
    private final Map<EntityType, IEntityAssignHandler> handlerMap = new HashMap<>();

    public AssignHandlerRegistry(List<IEntityAssignHandler> handlers) {
        for (IEntityAssignHandler handler : handlers) {
            handlerMap.put(handler.getEntityType(), handler);
        }
    }
    public IEntityAssignHandler getHandler(EntityType entityType) {
        return handlerMap.get(entityType);
    }
}
