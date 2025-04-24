package org.example.service.operationsService.converters;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.Operation;
import org.example.entity.Production;
import org.example.entity.operationsType.LaserCleaner;
import org.example.repo.ResourcesRepo;
import org.example.repo.operationsRepo.OperationsTypeRepo;
import org.example.service.Request;
import org.example.service.operationsService.TypeOfOperations;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LaserCleanerConverter implements OperationConverter<LaserCleaner> {

    private final Request request;
    private final OperationsTypeRepo operationsTypeRepo;
    private final ResourcesRepo resourcesRepo;

    @Override
    public LaserCleaner convert(Production production) {
        LaserCleaner laserCleaner = new LaserCleaner();
        laserCleaner.setRefKey(production.getRefKey());
        laserCleaner.setNumber(production.getProductionId());
        laserCleaner.setPriority(production.getPriority());
        laserCleaner.setResource(resourcesRepo.findFirstByName("LaserCleaner"));

        double time = calculateTime(production.getOperations());
        laserCleaner.setTime(time);
        laserCleaner.setNomenclatureName(production.getManufacturedProductName());


        if (operationsTypeRepo.existsByRefKeyAndNameAndTime(
                laserCleaner.getRefKey(),
                laserCleaner.getName(),
                laserCleaner.getTime())) {
            log.warn("Duplicate oreration: " + laserCleaner.getNomenclatureName());
        } else {
            if (laserCleaner.getTime() != 0) {
                operationsTypeRepo.save(laserCleaner);
            }
        }
        return laserCleaner;
    }

    @Override
    public List<String> getSupportedNomenclatures() {
        return List.of(
                TypeOfOperations.LASER_CLEANING_PR.getNomenclature(),
                TypeOfOperations.LASER_CLEANING.getNomenclature()
        );
    }

    private double calculateTime(List<Operation> operations) {
        return operations.stream()
                .filter(operation -> getSupportedNomenclatures().contains(operation.getNomenclature()))
                .mapToDouble(Operation::getOperationTime)
                .sum() * 60;
    }

    @Override
    public Class<LaserCleaner> getType() {
        return LaserCleaner.class;
    }

}
