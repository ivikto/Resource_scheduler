package org.example.service.operations_service.converters;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.Production;
import org.example.entity.Resources;
import org.example.entity.operations_type.Drilling;
import org.example.repo.ResourcesRepo;
import org.example.service.operations_service.OperationBuilderService;
import org.example.service.operations_service.OperationSaverService;
import org.example.service.operations_service.TypeOfOperations;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DrillingConverter implements OperationConverter<Drilling> {

    private final ResourcesRepo resourcesRepo;
    private final OperationSaverService operationSaverService;
    private final OperationBuilderService operationBuilderService;
    private Resources resource;

    @PostConstruct
    public void init() {
        this.resource = resourcesRepo.findFirstByName(this.getType().getSimpleName())
                .orElseThrow(() -> new IllegalStateException("Drilling resource not found"));
    }

    @Override
    public Drilling convert(Production production) {
        if (production == null) {
            throw new IllegalArgumentException("Production cannot be null");
        }

        Drilling drilling = operationBuilderService.buildOperation(production, resource, getSupportedNomenclatures(), Drilling::new);

        operationSaverService.saveOperation(drilling);


        return drilling;
    }

    @Override
    public List<String> getSupportedNomenclatures() {
        return List.of(
                TypeOfOperations.DRILLING.getNomenclature()
        );
    }

    @Override
    public Class<Drilling> getType() {
        return Drilling.class;
    }


}
