package org.example.service.operationsService.converters;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.Operation;
import org.example.entity.Production;
import org.example.entity.Resources;
import org.example.entity.operationsType.BandSaw;
import org.example.entity.operationsType.GrindingMachine;
import org.example.repo.ResourcesRepo;
import org.example.repo.operationsRepo.OperationsTypeRepo;
import org.example.service.Request;
import org.example.service.operationsService.OperationBuilder;
import org.example.service.operationsService.OperationSaver;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GrindingMachineConverter implements OperationConverter<GrindingMachine> {

    private final ResourcesRepo resourcesRepo;
    private final OperationSaver operationSaver;
    private final OperationBuilder operationBuilder;
    private Resources resource;

    @PostConstruct
    public void init() {
        this.resource = resourcesRepo.findFirstByName("BandSaw")
                .orElseThrow(() -> new IllegalStateException("BandSaw resource not found"));
    }

    @Override
    public GrindingMachine convert(Production production) {
        if (production == null) {
            throw new IllegalArgumentException("Production cannot be null");
        }
        GrindingMachine grindingMachine = operationBuilder.buildOperation(production, resource, getSupportedNomenclatures(), GrindingMachine::new);

        operationSaver.saveOperation(grindingMachine);

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
