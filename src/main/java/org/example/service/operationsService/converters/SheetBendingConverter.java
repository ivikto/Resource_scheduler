package org.example.service.operationsService.converters;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.Operation;
import org.example.entity.Production;
import org.example.entity.operationsType.SheetBending;
import org.example.repo.ResourcesRepo;
import org.example.repo.operationsRepo.OperationsTypeRepo;
import org.example.service.Request;
import org.example.service.operationsService.TypeOfOperations;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SheetBendingConverter implements OperationConverter<SheetBending> {

    private final Request request;
    private final OperationsTypeRepo operationsTypeRepo;
    private final ResourcesRepo resourcesRepo;

    @Override
    public SheetBending convert(Production production) {
        SheetBending sheetBending = new SheetBending();
        sheetBending.setRefKey(production.getRefKey());
        sheetBending.setNumber(production.getProductionId());
        sheetBending.setPriority(production.getPriority());
        sheetBending.setResource(resourcesRepo.findFirstByName("SheetBending"));

        double time = calculateTime(production.getOperations());
        sheetBending.setTime(time);

        sheetBending.setNomenclatureName(production.getManufacturedProductName());


        if (operationsTypeRepo.existsByRefKeyAndNameAndTime(
                sheetBending.getRefKey(),
                sheetBending.getName(),
                sheetBending.getTime())) {
            log.warn("Duplicate oreration: " + sheetBending.getNomenclatureName());
        } else {
            if (sheetBending.getTime() != 0) {
                operationsTypeRepo.save(sheetBending);
            }
        }
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
