package org.example.service.operations_service.converters;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.Operation;
import org.example.entity.Production;
import org.example.entity.Resources;
import org.example.entity.operations_type.SheetBending;
import org.example.repo.ResourcesRepo;
import org.example.service.operations_service.OperationBuilderService;
import org.example.service.operations_service.OperationSaverService;
import org.example.service.operations_service.TypeOfOperations;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SheetBendingConverter implements OperationConverter<SheetBending> {

    private final ResourcesRepo resourcesRepo;
    private final OperationSaverService operationSaverService;
    private final OperationBuilderService operationBuilderService;
    private Resources resource;

    @PostConstruct
    public void init() {
        this.resource = resourcesRepo.findFirstByName(this.getType().getSimpleName())
                .orElseThrow(() -> new IllegalStateException("SheetBending resource not found"));
    }

    @Override
    public SheetBending convert(Production production) {
        if (production == null) {
            throw new IllegalArgumentException("Production cannot be null");
        }
        SheetBending sheetBending = operationBuilderService.buildOperation(production, resource, getSupportedNomenclatures(), SheetBending::new);

        operationSaverService.saveOperation(sheetBending);

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
