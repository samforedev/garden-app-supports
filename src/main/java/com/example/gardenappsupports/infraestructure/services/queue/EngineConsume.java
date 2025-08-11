package com.example.gardenappsupports.infraestructure.services.queue;

import com.example.gardenappsupports.application.engine.IEngineConsume;
import com.example.gardenappsupports.domain.enums.ProcessStatus;
import com.example.gardenappsupports.domain.models.engine.EngineProcess;
import com.example.gardenappsupports.domain.repository.IEngineProcessRepository;
import com.example.gardenappsupports.infraestructure.services.engine.AssignHandlerRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EngineConsume implements IEngineConsume {

    private final IEngineProcessRepository processRepository;
    private final AssignHandlerRegistry assignHandlerRegistry;

    @Override
    @RabbitListener(queues = "${rabbitmq.engine.queue}")
    public void consume(String processId) throws Exception {
        UUID processUuid = UUID.fromString(processId);
        EngineProcess engineProcess = processRepository.findByProcessId(processUuid)
                .orElseThrow(() -> new Exception("No process found with id: " + processId));

        var handler = assignHandlerRegistry.getHandler(engineProcess.getEntityType());
        if (handler == null) {
            handleException(engineProcess);
            throw new Exception("No handler found for process with id: " + processId);
        }
        handler.process(engineProcess.getProcessType(), engineProcess.getDataRecords(), engineProcess);
    }

    private void handleException(EngineProcess engineProcess) {
        engineProcess.setStatus(ProcessStatus.FAILED);
        engineProcess.setProcessStatusReason("Unknown handler type: " + engineProcess.getEntityType());
        processRepository.save(engineProcess);
    }

}
