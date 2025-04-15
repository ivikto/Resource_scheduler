package org.example.service.operationsService.converters;

import lombok.RequiredArgsConstructor;
import org.example.entity.Operation;
import org.example.entity.Production;
import org.example.entity.operationsType.BandSaw;
import org.example.entity.operationsType.LaserCleaner;
import org.example.service.operationsService.TypeOfOperations;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BandSawConverter implements OperationConverter<BandSaw> {

    @Override
    public BandSaw convert(Production production) {
        BandSaw bandSaw = new BandSaw();
        bandSaw.setRefKey(production.getRefKey());
        bandSaw.setNumber(production.getProductionId());
        bandSaw.setPriority(production.getPriority());

        double time = calculateTime(production.getOperations());
        bandSaw.setTime(time);

        return bandSaw;
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
    public Class<BandSaw> getType() {
        return BandSaw.class;
    }
}
