package org.example.service.operations_service.converters;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.Production;
import org.example.entity.Resources;
import org.example.entity.operations_type.BandSaw;
import org.example.repo.ResourcesRepo;
import org.example.service.operations_service.OperationBuilderService;
import org.example.service.operations_service.OperationSaverService;
import org.example.service.operations_service.TypeOfOperations;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BandSawConverter implements OperationConverter<BandSaw> {

    private final ResourcesRepo resourcesRepo;
    private final OperationSaverService operationSaverService;
    private final OperationBuilderService operationBuilderService;
    private Resources resource;

    @PostConstruct
    public void init() {
        this.resource = resourcesRepo.findFirstByName(this.getType().getSimpleName())
                .orElseThrow(() -> new IllegalStateException("BandSaw resource not found"));
    }

    @Override
    public BandSaw convert(Production production) {
        if (production == null) {
            throw new IllegalArgumentException("Production cannot be null");
        }
        //Builder
        BandSaw bandSaw = operationBuilderService.buildOperation(production, resource, getSupportedNomenclatures(), BandSaw::new);

        //Сохраняем
        operationSaverService.saveOperation(bandSaw);
        return bandSaw;
    }

    @Override
    public List<String> getSupportedNomenclatures() {
        return List.of(
                TypeOfOperations.BANDSAW_WORKER_HOURS.getNomenclature(),
                TypeOfOperations.BANDSAW_PR.getNomenclature()

        );
    }

    @Override
    public Class<BandSaw> getType() {
        return BandSaw.class;
    }

}
