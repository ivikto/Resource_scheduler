package org.example.service.operationsService.converters;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.Operation;
import org.example.entity.Production;
import org.example.entity.operationsType.Welding;
import org.example.repo.ResourcesRepo;
import org.example.repo.operationsRepo.OperationsTypeRepo;
import org.example.service.Request;
import org.example.service.operationsService.TypeOfOperations;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeldingConverter implements OperationConverter<Welding> {

    private final Request request;
    private final OperationsTypeRepo operationsTypeRepo;
    private final ResourcesRepo resourcesRepo;

    @Override
    public Welding convert(Production production) {
        Welding welding = new Welding();
        welding.setRefKey(production.getRefKey());
        welding.setNumber(production.getProductionId());
        welding.setPriority(production.getPriority());
        welding.setResource(resourcesRepo.findFirstByName("Welding"));

        double time = calculateTime(production.getOperations());
        welding.setTime(time);
        String name = getNumName(production);
        welding.setNomenclatureName(name);
        production.setManufacturedProductName(name);

        if (operationsTypeRepo.existsByRefKeyAndNameAndTime(
                welding.getRefKey(),
                welding.getName(),
                welding.getTime())) {
            log.warn("Duplicate oreration: " + welding.getNomenclatureName());
        } else {
            if (welding.getTime() != 0) {
                operationsTypeRepo.save(welding);
            }
        }
        return welding;
    }

    @Override
    public List<String> getSupportedNomenclatures() {
        return List.of(
                TypeOfOperations.WELDING_MULTI.getNomenclature(),
                TypeOfOperations.WELDING_STEEL.getNomenclature(),
                TypeOfOperations.WELDING_LASER.getNomenclature(),
                TypeOfOperations.WELDING_WORKER_HOURS.getNomenclature()
        );
    }

    private double calculateTime(List<Operation> operations) {
        return operations.stream()
                .filter(operation -> getSupportedNomenclatures().contains(operation.getNomenclature()))
                .mapToDouble(Operation::getOperationTime)
                .sum() * 60;
    }

    @Override
    public Class<Welding> getType() {
        return Welding.class;
    }

    @Override
    public String getNumName(Production production) {
        String name = null;
        name = request.getNameOfNomenclature(production.getManufacturedProductRefKey());

        return name;
    }
}
