package org.example.service.operations_service.converters;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.Production;
import org.example.entity.Resources;
import org.example.entity.operations_type.LaserCleaner;
import org.example.repo.ResourcesRepo;
import org.example.service.operations_service.OperationBuilder;
import org.example.service.operations_service.OperationSaver;
import org.example.service.operations_service.TypeOfOperations;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LaserCleanerConverter implements OperationConverter<LaserCleaner> {

    private final ResourcesRepo resourcesRepo;
    private final OperationSaver operationSaver;
    private final OperationBuilder operationBuilder;
    private Resources resource;

    @PostConstruct
    public void init() {
        this.resource = resourcesRepo.findFirstByName(this.getType().getSimpleName())
                .orElseThrow(() -> new IllegalStateException("LaserCleaner resource not found"));
    }

    @Override
    public LaserCleaner convert(Production production) {
        if (production == null) {
            throw new IllegalArgumentException("Production cannot be null");
        }
        LaserCleaner laserCleaner = operationBuilder.buildOperation(production, resource, getSupportedNomenclatures(), LaserCleaner::new);

        operationSaver.saveOperation(laserCleaner);

        return laserCleaner;
    }

    @Override
    public List<String> getSupportedNomenclatures() {
        return List.of(
                TypeOfOperations.LASER_CLEANING_PR.getNomenclature(),
                TypeOfOperations.LASER_CLEANING.getNomenclature()
        );
    }

    @Override
    public Class<LaserCleaner> getType() {
        return LaserCleaner.class;
    }

}
