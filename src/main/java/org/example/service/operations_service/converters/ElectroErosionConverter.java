package org.example.service.operations_service.converters;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.Production;
import org.example.entity.Resources;
import org.example.entity.operations_type.ElectroErosion;
import org.example.repo.ResourcesRepo;
import org.example.service.operations_service.OperationBuilderService;
import org.example.service.operations_service.OperationSaverService;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ElectroErosionConverter implements OperationConverter<ElectroErosion> {

    private final ResourcesRepo resourcesRepo;
    private final OperationSaverService operationSaverService;
    private final OperationBuilderService operationBuilderService;
    private Resources resource;

    @PostConstruct
    public void init() {
        this.resource = resourcesRepo.findFirstByName(this.getType().getSimpleName())
                .orElseThrow(() -> new IllegalStateException("ElectroErosion resource not found"));
    }


    @Override
    public ElectroErosion convert(Production production) {
        if (production == null) {
            throw new IllegalArgumentException("Production cannot be null");
        }
        ElectroErosion electroErosion = operationBuilderService.buildOperation(production, resource, getSupportedNomenclatures(), ElectroErosion::new);
        operationSaverService.saveOperation(electroErosion);

        return electroErosion;
    }

    @Override
    public List<String> getSupportedNomenclatures() {
        return List.of(
                "EMPTY",
               "EMPTY"

        );
    }

    @Override
    public Class<ElectroErosion> getType() {
        return ElectroErosion.class;
    }

}
