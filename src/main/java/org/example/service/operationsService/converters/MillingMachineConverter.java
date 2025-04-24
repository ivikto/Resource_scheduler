package org.example.service.operationsService.converters;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.Operation;
import org.example.entity.Production;
import org.example.entity.operationsType.MillingMachine;
import org.example.repo.ResourcesRepo;
import org.example.repo.operationsRepo.OperationsTypeRepo;
import org.example.service.Request;
import org.example.service.operationsService.TypeOfOperations;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MillingMachineConverter implements OperationConverter<MillingMachine> {

    private final Request request;
    private final OperationsTypeRepo operationsTypeRepo;
    private final ResourcesRepo resourcesRepo;

    @Override
    public MillingMachine convert(Production production) {
        MillingMachine millingMachine = new MillingMachine();
        millingMachine.setRefKey(production.getRefKey());
        millingMachine.setNumber(production.getProductionId());
        millingMachine.setPriority(production.getPriority());
        millingMachine.setResource(resourcesRepo.findFirstByName("MillingMachine"));

        double time = calculateTime(production.getOperations());
        millingMachine.setTime(time);
        millingMachine.setNomenclatureName(production.getManufacturedProductName());


        if (operationsTypeRepo.existsByRefKeyAndNameAndTime(
                millingMachine.getRefKey(),
                millingMachine.getName(),
                millingMachine.getTime())) {
            log.warn("Duplicate oreration: " + millingMachine.getNomenclatureName());
        } else {
            if (millingMachine.getTime() != 0) {
                operationsTypeRepo.save(millingMachine);
            }
        }
        return millingMachine;
    }

    @Override
    public List<String> getSupportedNomenclatures() {
        return List.of(
                TypeOfOperations.MILLING_PR.getNomenclature(),
                TypeOfOperations.MILLING_WORKER_HOURS.getNomenclature()
        );
    }

    private double calculateTime(List<Operation> operations) {
        return operations.stream()
                .filter(operation -> getSupportedNomenclatures().contains(operation.getNomenclature()))
                .mapToDouble(Operation::getOperationTime)
                .sum() * 60;
    }

    @Override
    public Class<MillingMachine> getType() {
        return MillingMachine.class;
    }

}
