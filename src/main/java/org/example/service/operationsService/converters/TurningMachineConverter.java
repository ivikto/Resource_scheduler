package org.example.service.operationsService.converters;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.Operation;
import org.example.entity.Production;
import org.example.entity.operationsType.TurningMachine;
import org.example.repo.ResourcesRepo;
import org.example.repo.operationsRepo.OperationsTypeRepo;
import org.example.service.Request;
import org.example.service.operationsService.TypeOfOperations;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TurningMachineConverter implements OperationConverter<TurningMachine> {

    private final Request request;
    private final OperationsTypeRepo operationsTypeRepo;
    private final ResourcesRepo resourcesRepo;

    @Override
    public TurningMachine convert(Production production) {
        TurningMachine turningMachine = new TurningMachine();
        turningMachine.setRefKey(production.getRefKey());
        turningMachine.setNumber(production.getProductionId());
        turningMachine.setPriority(production.getPriority());
        turningMachine.setResource(resourcesRepo.findFirstByName("TurningMachine"));

        double time = calculateTime(production.getOperations());
        turningMachine.setTime(time);
        String name = getNumName(production);
        turningMachine.setNomenclatureName(name);
        production.setManufacturedProductName(name);

        if (operationsTypeRepo.existsByRefKeyAndNameAndTime(
                turningMachine.getRefKey(),
                turningMachine.getName(),
                turningMachine.getTime())) {
            log.warn("Duplicate oreration: " + turningMachine.getNomenclatureName());
        } else {
            if (turningMachine.getTime() != 0) {
                operationsTypeRepo.save(turningMachine);
            }
        }
        return turningMachine;
    }

    @Override
    public List<String> getSupportedNomenclatures() {
        return List.of(
                TypeOfOperations.LATHE_PR.getNomenclature(),
                TypeOfOperations.LATHE_WORKER_HOURS.getNomenclature()
        );
    }

    private double calculateTime(List<Operation> operations) {
        return operations.stream()
                .filter(operation -> getSupportedNomenclatures().contains(operation.getNomenclature()))
                .mapToDouble(Operation::getOperationTime)
                .sum() * 60;
    }

    @Override
    public Class<TurningMachine> getType() {
        return TurningMachine.class;
    }

    @Override
    public String getNumName(Production production) {
        String name = null;
        name = request.getNameOfNomenclature(production.getManufacturedProductRefKey());

        return name;
    }
}
