package org.example.service.operationsService.converters;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.Operation;
import org.example.entity.Production;
import org.example.entity.operationsType.Drilling;
import org.example.repo.operationsRepo.OperationsTypeRepo;
import org.example.service.Request;
import org.example.service.operationsService.TypeOfOperations;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DrillingConverter implements OperationConverter<Drilling> {

    private final Request request;
    private final OperationsTypeRepo operationsTypeRepo;

    @Override
    public Drilling convert(Production production) {
        Drilling drilling = new Drilling();
        drilling.setRefKey(production.getRefKey());
        drilling.setNumber(production.getProductionId());
        drilling.setPriority(production.getPriority());

        double time = calculateTime(production.getOperations());
        drilling.setTime(time);
        String name = getNumName(production);
        drilling.setNomenclatureName(name);
        production.setManufacturedProductName(name);

        if (operationsTypeRepo.existsByRefKeyAndNameAndTime(
                drilling.getRefKey(),
                drilling.getName(),
                drilling.getTime())) {
            log.warn("Duplicate oreration: " + drilling.getNomenclatureName());
        } else {
            if (drilling.getTime() != 0) {
                operationsTypeRepo.save(drilling);
            }
        }
        return drilling;
    }

    @Override
    public List<String> getSupportedNomenclatures() {
        return List.of(
                TypeOfOperations.DRILLING.getNomenclature()
        );
    }

    private double calculateTime(List<Operation> operations) {
        return operations.stream()
                .filter(operation -> getSupportedNomenclatures().contains(operation.getNomenclature()))
                .mapToDouble(Operation::getOperationTime)
                .sum() * 60;
    }

    @Override
    public Class<Drilling> getType() {
        return Drilling.class;
    }

    @Override
    public String getNumName(Production production) {
        String name = null;
        name = request.getNameOfNomenclature(production.getManufacturedProductRefKey());

        return name;
    }


}
