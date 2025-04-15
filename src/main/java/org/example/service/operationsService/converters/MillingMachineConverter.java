package org.example.service.operationsService.converters;

import lombok.RequiredArgsConstructor;
import org.example.entity.Operation;
import org.example.entity.Production;
import org.example.entity.operationsType.MillingMachine;
import org.example.entity.operationsType.MillingMachine;
import org.example.service.operationsService.TypeOfOperations;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MillingMachineConverter implements OperationConverter<MillingMachine> {

    @Override
    public MillingMachine convert(Production production) {
        MillingMachine millingMachine = new MillingMachine();
        millingMachine.setRefKey(production.getRefKey());
        millingMachine.setNumber(production.getProductionId());
        millingMachine.setPriority(production.getPriority());

        double time = calculateTime(production.getOperations());
        millingMachine.setTime(time);

        return millingMachine;
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
    public Class<MillingMachine> getType() {
        return MillingMachine.class;
    }
}
