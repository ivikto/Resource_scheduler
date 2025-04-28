package org.example.service.operations_service.converters;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.Production;
import org.example.entity.Resources;
import org.example.entity.operations_type.GrindingMachine;
import org.example.repo.ResourcesRepo;
import org.example.service.operations_service.OperationBuilderService;
import org.example.service.operations_service.OperationSaverService;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GrindingMachineConverter implements OperationConverter<GrindingMachine> {

    private final ResourcesRepo resourcesRepo;
    private final OperationSaverService operationSaverService;
    private final OperationBuilderService operationBuilderService;
    private Resources resource;

    @PostConstruct
    public void init() {
        this.resource = resourcesRepo.findFirstByName(this.getType().getSimpleName())
                .orElseThrow(() -> new IllegalStateException("GrindingMachine resource not found"));
    }

    @Override
    public GrindingMachine convert(Production production) {
        if (production == null) {
            throw new IllegalArgumentException("Production cannot be null");
        }
        GrindingMachine grindingMachine = operationBuilderService.buildOperation(production, resource, getSupportedNomenclatures(), GrindingMachine::new);

        operationSaverService.saveOperation(grindingMachine);

        return grindingMachine;
    }

    @Override
    public List<String> getSupportedNomenclatures() {
        return List.of(
                "EMPTY",
               "EMPTY"

        );
    }

    @Override
    public Class<GrindingMachine> getType() {
        return GrindingMachine.class;
    }

}
