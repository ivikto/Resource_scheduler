package org.example.service.operationsService.converters;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.Operation;
import org.example.entity.Production;
import org.example.entity.operationsType.LaserCutter;
import org.example.repo.ResourcesRepo;
import org.example.repo.operationsRepo.OperationsTypeRepo;
import org.example.service.Request;
import org.example.service.operationsService.TypeOfOperations;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LaserCutterConverter implements OperationConverter<LaserCutter> {

    private final Request request;
    private final OperationsTypeRepo operationsTypeRepo;
    private final ResourcesRepo resourcesRepo;

    @Override
    public LaserCutter convert(Production production) {
        LaserCutter laserCutter = new LaserCutter();
        laserCutter.setRefKey(production.getRefKey());
        laserCutter.setNumber(production.getProductionId());
        laserCutter.setPriority(production.getPriority());
        laserCutter.setResource(resourcesRepo.findFirstByName("LaserCutter"));

        double time = calculateTime(production.getOperations());
        laserCutter.setTime(time);

        laserCutter.setNomenclatureName(production.getManufacturedProductName());


        if (operationsTypeRepo.existsByRefKeyAndNameAndTime(
                laserCutter.getRefKey(),
                laserCutter.getName(),
                laserCutter.getTime())) {
            log.warn("Duplicate oreration: " + laserCutter.getNomenclatureName());
        } else {
            if (laserCutter.getTime() != 0) {
                operationsTypeRepo.save(laserCutter);
            }
        }

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

    private double calculateTime(List<Operation> operations) {
        return operations.stream()
                .filter(operation -> getSupportedNomenclatures().contains(operation.getNomenclature()))
                .mapToDouble(Operation::getOperationTime)
                .sum() * 60;
    }

    @Override
    public Class<LaserCutter> getType() {
        return LaserCutter.class;
    }

}
