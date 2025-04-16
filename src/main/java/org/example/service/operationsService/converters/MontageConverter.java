package org.example.service.operationsService.converters;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.Operation;
import org.example.entity.Production;
import org.example.entity.operationsType.Montage;
import org.example.repo.operationsRepo.OperationsTypeRepo;
import org.example.service.Request;
import org.example.service.operationsService.TypeOfOperations;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MontageConverter implements OperationConverter<Montage> {

    private final Request request;
    private final OperationsTypeRepo operationsTypeRepo;

    @Override
    public Montage convert(Production production) {
        Montage montage = new Montage();
        montage.setRefKey(production.getRefKey());
        montage.setNumber(production.getProductionId());
        montage.setPriority(production.getPriority());

        double time = calculateTime(production.getOperations());
        montage.setTime(time);
        String name = getNumName(production);
        montage.setNomenclatureName(name);
        production.setManufacturedProductName(name);

        if (operationsTypeRepo.existsByRefKeyAndNameAndTime(
                montage.getRefKey(),
                montage.getName(),
                montage.getTime())) {
            log.warn("Duplicate oreration: " + montage.getNomenclatureName());
        } else {
            if (montage.getTime() != 0) {
                operationsTypeRepo.save(montage);
            }
        }
        return montage;
    }

    @Override
    public List<String> getSupportedNomenclatures() {
        return List.of(
                TypeOfOperations.MONTAGE_ASSEMBLY_PR.getNomenclature(),
                TypeOfOperations.MONTAGE_COMPLEX_ASSEMBLY.getNomenclature(),
                TypeOfOperations.MONTAGE_ASSEMBLY_WORKER_HOURS.getNomenclature()
        );
    }

    private double calculateTime(List<Operation> operations) {
        return operations.stream()
                .filter(operation -> getSupportedNomenclatures().contains(operation.getNomenclature()))
                .mapToDouble(Operation::getOperationTime)
                .sum() * 60;
    }

    @Override
    public Class<Montage> getType() {
        return Montage.class;
    }

    @Override
    public String getNumName(Production production) {
        String name = null;
        name = request.getNameOfNomenclature(production.getManufacturedProductRefKey());

        return name;
    }


}
