package org.example.service.operations_service.converters;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.Production;
import org.example.entity.Resources;
import org.example.entity.operations_type.RollingMachine;
import org.example.repo.ResourcesRepo;
import org.example.service.operations_service.OperationBuilderService;
import org.example.service.operations_service.OperationSaverService;
import org.example.service.operations_service.TypeOfOperations;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RollingMachineConverter implements OperationConverter<RollingMachine> {

    private final ResourcesRepo resourcesRepo;
    private final OperationSaverService operationSaverService;
    private final OperationBuilderService operationBuilderService;
    private Resources resource;

    @PostConstruct
    public void init() {
        this.resource = resourcesRepo.findFirstByName(this.getType().getSimpleName())
                .orElseThrow(() -> new IllegalStateException("RollingMachine resource not found"));
    }

    @Override
    public RollingMachine convert(Production production) {
        if (production == null) {
            throw new IllegalArgumentException("Production cannot be null");
        }
        RollingMachine rollingMachine = operationBuilderService.buildOperation(production, resource, getSupportedNomenclatures(), RollingMachine::new);

        operationSaverService.saveOperation(rollingMachine);

        return rollingMachine;
    }

    @Override
    public List<String> getSupportedNomenclatures() {
        return List.of(
                TypeOfOperations.ROLLING.getNomenclature(),
                TypeOfOperations.ROLLING_PR.getNomenclature()
        );
    }

    @Override
    public Class<RollingMachine> getType() {
        return RollingMachine.class;
    }

}
