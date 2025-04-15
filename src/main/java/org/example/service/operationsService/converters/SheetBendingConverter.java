package org.example.service.operationsService.converters;

import lombok.RequiredArgsConstructor;
import org.example.entity.Operation;
import org.example.entity.Production;
import org.example.entity.operationsType.LaserCutter;
import org.example.entity.operationsType.SheetBending;
import org.example.service.operationsService.TypeOfOperations;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SheetBendingConverter implements OperationConverter<SheetBending> {

    @Override
    public SheetBending convert(Production production) {
        SheetBending sheetBending = new SheetBending();
        sheetBending.setRefKey(production.getRefKey());
        sheetBending.setNumber(production.getProductionId());
        sheetBending.setPriority(production.getPriority());

        double time = calculateTime(production.getOperations());
        sheetBending.setTime(time);

        return sheetBending;
    }

    @Override
    public List<String> getSupportedNomenclatures() {
        return List.of(
                TypeOfOperations.SHEET_BENDING_SINGLE.getNomenclature(),
                TypeOfOperations.SHEET_BENDING_WORKER_HOURS.getNomenclature()
        );
    }

    private double calculateTime(List<Operation> operations) {
        return operations.stream()
                .filter(operation -> getSupportedNomenclatures().contains(operation.getNomenclature()))
                .mapToDouble(Operation::getOperationTime)
                .sum() * 60;
    }

    @Override
    public Class<SheetBending> getType() {
        return SheetBending.class;
    }
}
