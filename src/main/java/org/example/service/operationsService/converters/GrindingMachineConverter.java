package org.example.service.operationsService.converters;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.Operation;
import org.example.entity.Production;
import org.example.entity.operationsType.GrindingMachine;
import org.example.repo.ResourcesRepo;
import org.example.repo.operationsRepo.OperationsTypeRepo;
import org.example.service.Request;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GrindingMachineConverter implements OperationConverter<GrindingMachine> {

    private final Request request;
    private final OperationsTypeRepo operationsTypeRepo;
    private final ResourcesRepo resourcesRepo;


    @Override
    public GrindingMachine convert(Production production) {
        GrindingMachine grindingMachine = new GrindingMachine();
        grindingMachine.setRefKey(production.getRefKey());
        grindingMachine.setNumber(production.getProductionId());
        grindingMachine.setPriority(production.getPriority());
        grindingMachine.setResource(resourcesRepo.findFirstByName("GrindingMachine"));

        double time = calculateTime(production.getOperations());
        grindingMachine.setTime(time);
        grindingMachine.setNomenclatureName(production.getManufacturedProductName());


        if (operationsTypeRepo.existsByRefKeyAndNameAndTime(
                grindingMachine.getRefKey(),
                grindingMachine.getName(),
                grindingMachine.getTime())) {
            log.warn("Duplicate oreration: " + grindingMachine.getNomenclatureName());
        } else {
            if (grindingMachine.getTime() != 0) {
                operationsTypeRepo.save(grindingMachine);
            }
        }
        return grindingMachine;
    }

    @Override
    public List<String> getSupportedNomenclatures() {
        return List.of(
                "EMPTY",
               "EMPTY"

        );
    }

    private double calculateTime(List<Operation> operations) {
        return operations.stream()
                .filter(operation -> getSupportedNomenclatures().contains(operation.getNomenclature()))
                .mapToDouble(Operation::getOperationTime)
                .sum() * 60;
    }

    @Override
    public Class<GrindingMachine> getType() {
        return GrindingMachine.class;
    }

}
