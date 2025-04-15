package org.example.service.operationsService.converters;

import lombok.RequiredArgsConstructor;
import org.example.entity.Operation;
import org.example.entity.Production;
import org.example.entity.operationsType.RollingMachine;
import org.example.service.operationsService.TypeOfOperations;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RollingMachineConverter implements OperationConverter<RollingMachine> {

    @Override
    public RollingMachine convert(Production production) {
        RollingMachine rollingMachine = new RollingMachine();
        rollingMachine.setRefKey(production.getRefKey());
        rollingMachine.setNumber(production.getProductionId());
        rollingMachine.setPriority(production.getPriority());

        double time = calculateTime(production.getOperations());
        rollingMachine.setTime(time);

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
}
