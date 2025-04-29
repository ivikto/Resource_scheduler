package org.example.service.operations_service.converters;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.Production;
import org.example.entity.Resources;
import org.example.entity.operations_type.Paint;
import org.example.repo.ResourcesRepo;
import org.example.service.operations_service.OperationBuilderService;
import org.example.service.operations_service.OperationSaverService;
import org.example.service.operations_service.TypeOfOperations;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaintConverter implements OperationConverter<Paint> {

    private final ResourcesRepo resourcesRepo;
    private final OperationSaverService operationSaverService;
    private final OperationBuilderService operationBuilderService;
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

        Paint paint = operationBuilderService.buildOperation(production, resource, getSupportedNomenclatures(), Paint::new);
        operationSaverService.saveOperation(paint);

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
