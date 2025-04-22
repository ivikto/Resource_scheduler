package org.example.service.operationsService.converters;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.Operation;
import org.example.entity.Production;
import org.example.entity.operationsType.RollingMachine;
import org.example.repo.ResourcesRepo;
import org.example.repo.operationsRepo.OperationsTypeRepo;
import org.example.service.Request;
import org.example.service.operationsService.TypeOfOperations;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RollingMachineConverter implements OperationConverter<RollingMachine> {

    private final Request request;
    private final OperationsTypeRepo operationsTypeRepo;
    private final ResourcesRepo resourcesRepo;

    @Override
    public RollingMachine convert(Production production) {
        RollingMachine rollingMachine = new RollingMachine();
        rollingMachine.setRefKey(production.getRefKey());
        rollingMachine.setNumber(production.getProductionId());
        rollingMachine.setPriority(production.getPriority());
        rollingMachine.setResource(resourcesRepo.findFirstByName("RollingMachine"));

        double time = calculateTime(production.getOperations());
        rollingMachine.setTime(time);
        String name = getNumName(production);
        rollingMachine.setNomenclatureName(name);
        production.setManufacturedProductName(name);

        if (operationsTypeRepo.existsByRefKeyAndNameAndTime(
                rollingMachine.getRefKey(),
                rollingMachine.getName(),
                rollingMachine.getTime())) {
            log.warn("Duplicate oreration: " + rollingMachine.getNomenclatureName());
        } else {
            if (rollingMachine.getTime() != 0) {
                operationsTypeRepo.save(rollingMachine);
            }
        }
        return rollingMachine;
    }

    @Override
    public List<String> getSupportedNomenclatures() {
        return List.of(
                TypeOfOperations.ROLLING.getNomenclature(),
                TypeOfOperations.ROLLING_PR.getNomenclature()
        );
    }

    private double calculateTime(List<Operation> operations) {
        return operations.stream()
                .filter(operation -> getSupportedNomenclatures().contains(operation.getNomenclature()))
                .mapToDouble(Operation::getOperationTime)
                .sum() * 60;
    }

    @Override
    public Class<RollingMachine> getType() {
        return RollingMachine.class;
    }

    @Override
    public String getNumName(Production production) {
        String name = null;
        name = request.getNameOfNomenclature(production.getManufacturedProductRefKey());

        return name;
    }
}
