package org.example.service.operationsService.converters;

import lombok.RequiredArgsConstructor;
import org.example.entity.Operation;
import org.example.entity.Production;
import org.example.entity.operationsType.TurningMachine;
import org.example.service.operationsService.TypeOfOperations;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TurningMachineConverter implements OperationConverter<TurningMachine> {

    @Override
    public TurningMachine convert(Production production) {
        TurningMachine turningMachine = new TurningMachine();
        turningMachine.setRefKey(production.getRefKey());
        turningMachine.setNumber(production.getProductionId());
        turningMachine.setPriority(production.getPriority());

        double time = calculateTime(production.getOperations());
        turningMachine.setTime(time);

        return turningMachine;
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
    public Class<TurningMachine> getType() {
        return TurningMachine.class;
    }
}
