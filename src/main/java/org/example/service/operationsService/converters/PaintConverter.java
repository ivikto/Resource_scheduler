package org.example.service.operationsService.converters;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.Operation;
import org.example.entity.Production;
import org.example.entity.operationsType.Paint;
import org.example.repo.ResourcesRepo;
import org.example.repo.operationsRepo.OperationsTypeRepo;
import org.example.service.Request;
import org.example.service.operationsService.TypeOfOperations;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaintConverter implements OperationConverter<Paint> {

    private final Request request;
    private final OperationsTypeRepo operationsTypeRepo;
    private final ResourcesRepo resourcesRepo;

    @Override
    public Paint convert(Production production) {
        Paint paint = new Paint();
        paint.setRefKey(production.getRefKey());
        paint.setNumber(production.getProductionId());
        paint.setPriority(production.getPriority());
        paint.setResource(resourcesRepo.findFirstByName("Paint"));

        double time = calculateTime(production.getOperations());
        paint.setTime(time);
        paint.setNomenclatureName(production.getManufacturedProductName());


        if (operationsTypeRepo.existsByRefKeyAndNameAndTime(
                paint.getRefKey(),
                paint.getName(),
                paint.getTime())) {
            log.warn("Duplicate oreration: " + paint.getNomenclatureName());
        } else {
            if (paint.getTime() != 0) {
                operationsTypeRepo.save(paint);
            }
        }
        return paint;
    }

    @Override
    public List<String> getSupportedNomenclatures() {
        return List.of(
                TypeOfOperations.PAINTING_WORKER_HOURS.getNomenclature(),
                TypeOfOperations.PAINTING_POWDER_COATING.getNomenclature(),
                TypeOfOperations.PAINTING_POWDER_PRIMER.getNomenclature()
        );
    }

    private double calculateTime(List<Operation> operations) {
        return operations.stream()
                .filter(operation -> getSupportedNomenclatures().contains(operation.getNomenclature()))
                .mapToDouble(Operation::getOperationTime)
                .sum() * 60;
    }

    @Override
    public Class<Paint> getType() {
        return Paint.class;
    }

}
