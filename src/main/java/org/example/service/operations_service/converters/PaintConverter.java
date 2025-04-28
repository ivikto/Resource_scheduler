package org.example.service.operations_service.converters;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.Production;
import org.example.entity.Resources;
import org.example.entity.operations_type.Paint;
import org.example.repo.ResourcesRepo;
import org.example.service.operations_service.OperationBuilder;
import org.example.service.operations_service.OperationSaver;
import org.example.service.operations_service.TypeOfOperations;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaintConverter implements OperationConverter<Paint> {

    private final ResourcesRepo resourcesRepo;
    private final OperationSaver operationSaver;
    private final OperationBuilder operationBuilder;
    private Resources resource;

    @PostConstruct
    public void init() {
        this.resource = resourcesRepo.findFirstByName(this.getType().getSimpleName())
                .orElseThrow(() -> new IllegalStateException("Paint resource not found"));
    }

    @Override
    public Paint convert(Production production) {
        if (production == null) {
            throw new IllegalArgumentException("Production cannot be null");
        }

        Paint paint = operationBuilder.buildOperation(production, resource, getSupportedNomenclatures(), Paint::new);
        operationSaver.saveOperation(paint);

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

    @Override
    public Class<Paint> getType() {
        return Paint.class;
    }

}
