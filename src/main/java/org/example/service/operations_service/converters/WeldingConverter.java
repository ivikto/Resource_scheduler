package org.example.service.operations_service.converters;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.Production;
import org.example.entity.Resources;
import org.example.entity.operations_type.Welding;
import org.example.repo.ResourcesRepo;
import org.example.service.operations_service.OperationBuilderService;
import org.example.service.operations_service.OperationSaverService;
import org.example.service.operations_service.TypeOfOperations;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeldingConverter implements OperationConverter<Welding> {

    private final ResourcesRepo resourcesRepo;
    private final OperationSaverService operationSaverService;
    private final OperationBuilderService operationBuilderService;
    private Resources resource;

    @PostConstruct
    public void init() {
        this.resource = resourcesRepo.findFirstByName(this.getType().getSimpleName())
                .orElseThrow(() -> new IllegalStateException("Welding resource not found"));
    }

    @Override
    public Welding convert(Production production) {
        if (production == null) {
            throw new IllegalArgumentException("Production cannot be null");
        }
        Welding welding = operationBuilderService.buildOperation(production, resource, getSupportedNomenclatures(), Welding::new);

        operationSaverService.saveOperation(welding);

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
