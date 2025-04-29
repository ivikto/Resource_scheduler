package org.example.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.operations_type.OperationKit;
import org.example.entity.timeline.ScheduledOperation;
import org.example.repo.ProductionRepo;
import org.example.repo.OperationKitRepo;
import org.example.repo.ScheduledOperationRepo;
import org.example.service.operations_service.converters.OperationConverter;
import org.example.service.operations_service.converters.OperationConverterFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class OperationService {

    private final OperationKitRepo operationKitRepo;
    private final OperationConverterFactory converterFactory;
    private final ProductionRepo productionRepo;
    private final ScheduledOperationRepo scheduledOperationRepo;

    public Double timeSumForOperations() {
        List<OperationKit> operations = operationKitRepo.findByNotInTimeLine();

        return operations.stream()
                .mapToDouble(OperationKit::getTime)
                .sum();
    }

    public Map<String, Double> timeForAllOperations() {
        List<OperationKit> operations = operationKitRepo.findByNotInTimeLine();

        return operations.stream()
                .collect(Collectors.groupingBy(OperationKit::getName,
                        Collectors.summingDouble(op -> op.getTime() / 60.0)));

    }

    public <T> List<T> getAllOperations(Class<T> type) {
        OperationConverter<T> converter = converterFactory.getConverter(type);

        return productionRepo.findAllWithOperations().stream()
                .map(converter::convert)
                .toList();
    }

    public void saveOperations(JsonNode operationsNode) {

        Iterator<Map.Entry<String, JsonNode>> fields = operationsNode.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            String resourceId = entry.getKey();
            String operationsForResource = entry.getValue().toString();

            // Сохраняем операции для каждого ресурса
            scheduledOperationRepo.findByResourceId(resourceId)
                    .ifPresentOrElse(
                            existing -> {
                                existing.setOperations(operationsForResource);
                                existing.setOperationDate(LocalDateTime.now());
                                scheduledOperationRepo.save(existing);
                            },
                            () -> {
                                ScheduledOperation newOp = new ScheduledOperation();
                                newOp.setResourceId(resourceId);
                                newOp.setOperations(operationsForResource);
                                newOp.setOperationDate(LocalDateTime.now());
                                scheduledOperationRepo.save(newOp);
                            }
                    );
        }

    }


}
