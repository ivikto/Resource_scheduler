package org.example.service.operations_service.converters;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.Production;
import org.example.entity.Resources;
import org.example.entity.operations_type.MillingMachine;
import org.example.repo.ResourcesRepo;
import org.example.service.operations_service.OperationBuilder;
import org.example.service.operations_service.OperationSaver;
import org.example.service.operations_service.TypeOfOperations;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MillingMachineConverter implements OperationConverter<MillingMachine> {

    private final ResourcesRepo resourcesRepo;
    private final OperationSaver operationSaver;
    private final OperationBuilder operationBuilder;
    private Resources resource;

    @PostConstruct
    public void init() {

        this.resource = resourcesRepo.findFirstByName(this.getType().getSimpleName())
                .orElseThrow(() -> new IllegalStateException("MillingMachine resource not found"));
    }

    @Override
    public MillingMachine convert(Production production) {
        if (production == null) {
            throw new IllegalArgumentException("Production cannot be null");
        }
        MillingMachine millingMachine = operationBuilder.buildOperation(production, resource, getSupportedNomenclatures(), MillingMachine::new);

        operationSaver.saveOperation(millingMachine);

        return millingMachine;
    }

    @Override
    public List<String> getSupportedNomenclatures() {
        return List.of(
                TypeOfOperations.MILLING_PR.getNomenclature(),
                TypeOfOperations.MILLING_WORKER_HOURS.getNomenclature()
        );
    }

    @Override
    public Class<MillingMachine> getType() {
        return MillingMachine.class;
    }

}
