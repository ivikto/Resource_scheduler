package org.example.service.operationsService.converters;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.Operation;
import org.example.entity.Production;
import org.example.entity.Resources;
import org.example.entity.operationsType.BandSaw;
import org.example.entity.operationsType.LaserCutter;
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
public class LaserCutterConverter implements OperationConverter<LaserCutter> {

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
    public LaserCutter convert(Production production) {
        if (production == null) {
            throw new IllegalArgumentException("Production cannot be null");
        }

        LaserCutter laserCutter = operationBuilder.buildOperation(production, resource, getSupportedNomenclatures(), LaserCutter::new);

        operationSaver.saveOperation(laserCutter);

        return laserCutter;
    }

    @Override
    public List<String> getSupportedNomenclatures() {
        return List.of(
                TypeOfOperations.LASER_WORKER_HOURS.getNomenclature(),
                TypeOfOperations.LASER_CUTTING.getNomenclature(),
                TypeOfOperations.LASER_FINISHING_WORK.getNomenclature(),
                TypeOfOperations.LASER_FINISHING_WORK2.getNomenclature()
        );
    }


    @Override
    public Class<LaserCutter> getType() {
        return LaserCutter.class;
    }

}
