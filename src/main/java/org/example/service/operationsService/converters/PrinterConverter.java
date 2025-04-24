package org.example.service.operationsService.converters;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.Operation;
import org.example.entity.Production;
import org.example.entity.operationsType.Printer;
import org.example.repo.ResourcesRepo;
import org.example.repo.operationsRepo.OperationsTypeRepo;
import org.example.service.Request;
import org.example.service.operationsService.TypeOfOperations;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PrinterConverter implements OperationConverter<Printer> {

    private final Request request;
    private final OperationsTypeRepo operationsTypeRepo;
    private final ResourcesRepo resourcesRepo;

    @Override
    public Printer convert(Production production) {
        Printer printer = new Printer();
        printer.setRefKey(production.getRefKey());
        printer.setNumber(production.getProductionId());
        printer.setPriority(production.getPriority());
        printer.setResource(resourcesRepo.findFirstByName("Printer"));

        double time = calculateTime(production.getOperations());
        printer.setTime(time);

        printer.setNomenclatureName(production.getManufacturedProductName());


        if (operationsTypeRepo.existsByRefKeyAndNameAndTime(
                printer.getRefKey(),
                printer.getName(),
                printer.getTime())) {
            log.warn("Duplicate oreration: " + printer.getNomenclatureName());
        } else {
            if (printer.getTime() != 0) {
                operationsTypeRepo.save(printer);
            }
        }
        return printer;
    }

    @Override
    public List<String> getSupportedNomenclatures() {
        return List.of(
                TypeOfOperations.PRINT_3D_PR.getNomenclature(),
                TypeOfOperations.PRINT_3D_WORKER_HOURS.getNomenclature()
        );
    }

    private double calculateTime(List<Operation> operations) {
        return operations.stream()
                .filter(operation -> getSupportedNomenclatures().contains(operation.getNomenclature()))
                .mapToDouble(Operation::getOperationTime)
                .sum() * 60;
    }

    @Override
    public Class<Printer> getType() {
        return Printer.class;
    }

}
