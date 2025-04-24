package org.example.service.operationsService.converters;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.Operation;
import org.example.entity.Production;
import org.example.entity.Resources;
import org.example.entity.operationsType.BandSaw;
import org.example.repo.ResourcesRepo;
import org.example.repo.operationsRepo.OperationsTypeRepo;
import org.example.service.Request;
import org.example.service.operationsService.TypeOfOperations;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BandSawConverter implements OperationConverter<BandSaw> {

    private final Request request;
    private final OperationsTypeRepo operationsTypeRepo;
    private final ResourcesRepo resourcesRepo;


    @Override
    public BandSaw convert(Production production) {
        BandSaw bandSaw = new BandSaw();
        bandSaw.setRefKey(production.getRefKey());
        bandSaw.setNumber(production.getProductionId());
        bandSaw.setPriority(production.getPriority());
        bandSaw.setResource(resourcesRepo.findFirstByName("BandSaw"));

        double time = calculateTime(production.getOperations());
        bandSaw.setTime(time);
        bandSaw.setNomenclatureName(production.getManufacturedProductName());

        if (operationsTypeRepo.existsByRefKeyAndNameAndTime(
                bandSaw.getRefKey(),
                bandSaw.getName(),
                bandSaw.getTime())) {
            log.warn("Duplicate oreration: " + bandSaw.getNomenclatureName());
        } else {
            if (bandSaw.getTime() != 0) {
                operationsTypeRepo.save(bandSaw);
            }
        }
        return bandSaw;
    }

    @Override
    public List<String> getSupportedNomenclatures() {
        return List.of(
                TypeOfOperations.BANDSAW_WORKER_HOURS.getNomenclature(),
                TypeOfOperations.BANDSAW_PR.getNomenclature()

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
