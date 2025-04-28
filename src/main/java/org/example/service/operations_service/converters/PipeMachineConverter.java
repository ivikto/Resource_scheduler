package org.example.service.operations_service.converters;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.Production;
import org.example.entity.Resources;
import org.example.entity.operations_type.PipeMachine;
import org.example.repo.ResourcesRepo;
import org.example.service.operations_service.OperationBuilder;
import org.example.service.operations_service.OperationSaver;
import org.example.service.operations_service.TypeOfOperations;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PipeMachineConverter implements OperationConverter<PipeMachine> {

    private final ResourcesRepo resourcesRepo;
    private final OperationSaver operationSaver;
    private final OperationBuilder operationBuilder;
    private Resources resource;

    @PostConstruct
    public void init() {
        this.resource = resourcesRepo.findFirstByName(this.getType().getSimpleName())
                .orElseThrow(() -> new IllegalStateException("PipeMachine resource not found"));
    }

    @Override
    public PipeMachine convert(Production production) {
        if (production == null) {
            throw new IllegalArgumentException("Production cannot be null");
        }
        PipeMachine pipeMachine = operationBuilder.buildOperation(production, resource, getSupportedNomenclatures(), PipeMachine::new);

        operationSaver.saveOperation(pipeMachine);

        return pipeMachine;
    }

    @Override
    public List<String> getSupportedNomenclatures() {
        return List.of(
                TypeOfOperations.PIPE_BENDING.getNomenclature(),
                TypeOfOperations.PIPE_CNC_BENDING.getNomenclature()
        );
    }

    @Override
    public Class<PipeMachine> getType() {
        return PipeMachine.class;
    }

}
