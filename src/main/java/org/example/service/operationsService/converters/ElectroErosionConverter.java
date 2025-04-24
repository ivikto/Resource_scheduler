package org.example.service.operationsService.converters;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.Operation;
import org.example.entity.Production;
import org.example.entity.operationsType.ElectroErosion;
import org.example.repo.ResourcesRepo;
import org.example.repo.operationsRepo.OperationsTypeRepo;
import org.example.service.Request;
import org.example.service.operationsService.TypeOfOperations;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ElectroErosionConverter implements OperationConverter<ElectroErosion> {

    private final Request request;
    private final OperationsTypeRepo operationsTypeRepo;
    private final ResourcesRepo resourcesRepo;


    @Override
    public ElectroErosion convert(Production production) {
        ElectroErosion electroErosion = new ElectroErosion();
        electroErosion.setRefKey(production.getRefKey());
        electroErosion.setNumber(production.getProductionId());
        electroErosion.setPriority(production.getPriority());
        electroErosion.setResource(resourcesRepo.findFirstByName("ElectroErosion"));

        double time = calculateTime(production.getOperations());
        electroErosion.setTime(time);
        electroErosion.setNomenclatureName(production.getManufacturedProductName());


        if (operationsTypeRepo.existsByRefKeyAndNameAndTime(
                electroErosion.getRefKey(),
                electroErosion.getName(),
                electroErosion.getTime())) {
            log.warn("Duplicate oreration: " + electroErosion.getNomenclatureName());
        } else {
            if (electroErosion.getTime() != 0) {
                operationsTypeRepo.save(electroErosion);
            }
        }
        return electroErosion;
    }

    @Override
    public List<String> getSupportedNomenclatures() {
        return List.of(
                "EMPTY",
               "EMPTY"

        );
    }

    private double calculateTime(List<Operation> operations) {
        return operations.stream()
                .filter(operation -> getSupportedNomenclatures().contains(operation.getNomenclature()))
                .mapToDouble(Operation::getOperationTime)
                .sum() * 60;
    }

    @Override
    public Class<ElectroErosion> getType() {
        return ElectroErosion.class;
    }

}
