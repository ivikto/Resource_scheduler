package org.example.service.operationsService.converters;

import lombok.RequiredArgsConstructor;
import org.example.entity.Operation;
import org.example.entity.Production;
import org.example.entity.operationsType.PipeMachine;
import org.example.service.operationsService.TypeOfOperations;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PipeMachineConverter implements OperationConverter<PipeMachine> {

    @Override
    public PipeMachine convert(Production production) {
        PipeMachine pipeMachine = new PipeMachine();
        pipeMachine.setRefKey(production.getRefKey());
        pipeMachine.setNumber(production.getProductionId());
        pipeMachine.setPriority(production.getPriority());

        double time = calculateTime(production.getOperations());
        pipeMachine.setTime(time);

        return pipeMachine;
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
    public Class<PipeMachine> getType() {
        return PipeMachine.class;
    }
}
