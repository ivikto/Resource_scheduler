package org.example.service.operationsService.converters;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.Operation;
import org.example.entity.Production;
import org.example.entity.operationsType.PipeMachine;
import org.example.repo.ResourcesRepo;
import org.example.repo.operationsRepo.OperationsTypeRepo;
import org.example.service.Request;
import org.example.service.operationsService.TypeOfOperations;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PipeMachineConverter implements OperationConverter<PipeMachine> {

    private final Request request;
    private final OperationsTypeRepo operationsTypeRepo;
    private final ResourcesRepo resourcesRepo;

    @Override
    public PipeMachine convert(Production production) {
        PipeMachine pipeMachine = new PipeMachine();
        pipeMachine.setRefKey(production.getRefKey());
        pipeMachine.setNumber(production.getProductionId());
        pipeMachine.setPriority(production.getPriority());
        pipeMachine.setResource(resourcesRepo.findFirstByName("PipeMachine"));

        double time = calculateTime(production.getOperations());
        pipeMachine.setTime(time);
        String name = getNumName(production);
        pipeMachine.setNomenclatureName(name);
        production.setManufacturedProductName(name);

        if (operationsTypeRepo.existsByRefKeyAndNameAndTime(
                pipeMachine.getRefKey(),
                pipeMachine.getName(),
                pipeMachine.getTime())) {
            log.warn("Duplicate oreration: " + pipeMachine.getNomenclatureName());
        } else {
            if (pipeMachine.getTime() != 0) {
                operationsTypeRepo.save(pipeMachine);
            }
        }
        return pipeMachine;
    }

    @Override
    public List<String> getSupportedNomenclatures() {
        return List.of(
                TypeOfOperations.PIPE_BENDING.getNomenclature(),
                TypeOfOperations.PIPE_CNC_BENDING.getNomenclature()
        );
    }

    private double calculateTime(List<Operation> operations) {
        return operations.stream()
                .filter(operation -> getSupportedNomenclatures().contains(operation.getNomenclature()))
                .mapToDouble(Operation::getOperationTime)
                .sum() * 60;
    }

    @Override
    public Class<PipeMachine> getType() {
        return PipeMachine.class;
    }

    @Override
    public String getNumName(Production production) {
        String name = null;
        name = request.getNameOfNomenclature(production.getManufacturedProductRefKey());

        return name;
    }
}
