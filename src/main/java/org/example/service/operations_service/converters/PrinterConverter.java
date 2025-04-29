package org.example.service.operations_service.converters;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.Production;
import org.example.entity.Resources;
import org.example.entity.operations_type.Printer;
import org.example.repo.ResourcesRepo;
import org.example.service.operations_service.OperationBuilderService;
import org.example.service.operations_service.OperationSaverService;
import org.example.service.operations_service.TypeOfOperations;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PrinterConverter implements OperationConverter<Printer> {

    private final ResourcesRepo resourcesRepo;
    private final OperationSaverService operationSaverService;
    private final OperationBuilderService operationBuilderService;
    private Resources resource;

    @PostConstruct
    public void init() {
        this.resource = resourcesRepo.findFirstByName(this.getType().getSimpleName())
                .orElseThrow(() -> new IllegalStateException("Printer resource not found"));
    }

    @Override
    public Printer convert(Production production) {
        if (production == null) {
            throw new IllegalArgumentException("Production cannot be null");
        }
        Printer printer = operationBuilderService.buildOperation(production, resource, getSupportedNomenclatures(), Printer::new);

        operationSaverService.saveOperation(printer);

        return printer;
    }

    @Override
    public List<String> getSupportedNomenclatures() {
        return List.of(
                TypeOfOperations.PRINT_3D_PR.getNomenclature(),
                TypeOfOperations.PRINT_3D_WORKER_HOURS.getNomenclature()
        );
    }

    @Override
    public Class<Printer> getType() {
        return Printer.class;
    }

}
