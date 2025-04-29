package org.example.service.operations_service.converters;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.Production;
import org.example.entity.Resources;
import org.example.entity.operations_type.Montage;
import org.example.repo.ResourcesRepo;
import org.example.service.operations_service.OperationBuilderService;
import org.example.service.operations_service.OperationSaverService;
import org.example.service.operations_service.TypeOfOperations;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MontageConverter implements OperationConverter<Montage> {

    private final ResourcesRepo resourcesRepo;
    private final OperationBuilderService operationBuilderService;
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

        return operationBuilderService.buildOperation(production, resource, getSupportedNomenclatures(), Montage::new);
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
