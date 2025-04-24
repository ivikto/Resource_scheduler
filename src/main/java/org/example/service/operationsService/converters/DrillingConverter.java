package org.example.service.operationsService.converters;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.Operation;
import org.example.entity.Production;
import org.example.entity.Resources;
import org.example.entity.operationsType.BandSaw;
import org.example.entity.operationsType.Drilling;
import org.example.repo.ResourcesRepo;
import org.example.repo.operationsRepo.OperationsTypeRepo;
import org.example.service.Request;
import org.example.service.operationsService.OperationBuilder;
import org.example.service.operationsService.OperationSaver;
import org.example.service.operationsService.TypeOfOperations;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DrillingConverter implements OperationConverter<Drilling> {

    private final ResourcesRepo resourcesRepo;
    private final OperationSaver operationSaver;
    private final OperationBuilder operationBuilder;
    private Resources resource;

    @PostConstruct
    public void init() {
        this.resource = resourcesRepo.findFirstByName("BandSaw")
                .orElseThrow(() -> new IllegalStateException("BandSaw resource not found"));
    }

    @Override
    public Drilling convert(Production production) {
        if (production == null) {
            throw new IllegalArgumentException("Production cannot be null");
        }

        Drilling drilling = operationBuilder.buildOperation(production, resource, getSupportedNomenclatures(), Drilling::new);

        operationSaver.saveOperation(drilling);


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
