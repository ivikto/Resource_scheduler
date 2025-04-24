package org.example.service.operationsService.converters;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.Operation;
import org.example.entity.Production;
import org.example.entity.Resources;
import org.example.entity.operationsType.BandSaw;
import org.example.entity.operationsType.Welding;
import org.example.repo.ResourcesRepo;
import org.example.repo.operationsRepo.OperationsTypeRepo;
import org.example.service.Request;
import org.example.service.operationsService.OperationBuilder;
import org.example.service.operationsService.OperationSaver;
import org.example.service.operationsService.TypeOfOperations;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeldingConverter implements OperationConverter<Welding> {

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
    public Welding convert(Production production) {
        if (production == null) {
            throw new IllegalArgumentException("Production cannot be null");
        }
        Welding welding = operationBuilder.buildOperation(production, resource, getSupportedNomenclatures(), Welding::new);

        operationSaver.saveOperation(welding);

        return welding;
    }

    @Override
    public List<String> getSupportedNomenclatures() {
        return List.of(
                TypeOfOperations.WELDING_MULTI.getNomenclature(),
                TypeOfOperations.WELDING_STEEL.getNomenclature(),
                TypeOfOperations.WELDING_LASER.getNomenclature(),
                TypeOfOperations.WELDING_WORKER_HOURS.getNomenclature()
        );
    }

    @Override
    public Class<Welding> getType() {
        return Welding.class;
    }

}
