package org.example.service.operations_service.converters;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.Production;
import org.example.entity.Resources;
import org.example.entity.operations_type.Montage;
import org.example.repo.ResourcesRepo;
import org.example.service.operations_service.OperationBuilder;
import org.example.service.operations_service.OperationSaver;
import org.example.service.operations_service.TypeOfOperations;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MontageConverter implements OperationConverter<Montage> {

    private final ResourcesRepo resourcesRepo;
    private final OperationSaver operationSaver;
    private final OperationBuilder operationBuilder;
    private Resources resource;

    @PostConstruct
    public void init() {
        this.resource = resourcesRepo.findFirstByName(this.getType().getSimpleName())
                .orElseThrow(() -> new IllegalStateException("Montage resource not found"));
    }

    @Override
    public Montage convert(Production production) {
        if (production == null) {
            throw new IllegalArgumentException("Production cannot be null");
        }
        Montage montage = operationBuilder.buildOperation(production, resource, getSupportedNomenclatures(), Montage::new);

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

    @Override
    public Class<Montage> getType() {
        return Montage.class;
    }

}
