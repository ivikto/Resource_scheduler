package org.example.service.operations_service;

import lombok.RequiredArgsConstructor;
import org.example.repo.ProductionRepo;
import org.example.service.operations_service.converters.OperationConverter;
import org.example.service.operations_service.converters.OperationConverterFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OperationsService {

    private final ProductionRepo productionRepo;
    private final OperationConverterFactory converterFactory;

    public <T> List<T> getAllOperations(Class<T> type) {
        OperationConverter<T> converter = converterFactory.getConverter(type);

        return productionRepo.findAllWithOperations().stream()
                .map(converter::convert)
                .toList();
    }
}
