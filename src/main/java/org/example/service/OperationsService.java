package org.example.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.operations_type.OperationType;
import org.example.entity.timeline.ScheduledOperation;
import org.example.repo.ProductionRepo;
import org.example.repo.OperationsTypeRepo;
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
public class OperationsService {

    private final OperationsTypeRepo operationsTypeRepo;
    private final OperationConverterFactory converterFactory;
    private final ProductionRepo productionRepo;
    private final ScheduledOperationRepo scheduledOperationRepo;

    public Double timeSumForOperations() {
        List<OperationType> operations = operationsTypeRepo.findByNotInTimeLine();

        return operations.stream()
                .mapToDouble(OperationType::getTime)
                .sum();
    }

    public Map<String, Double> timeForAllOperations() {
        List<OperationType> operations = operationsTypeRepo.findByNotInTimeLine();

        return operations.stream()
                .collect(Collectors.groupingBy(OperationType::getName,
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

    public StringBuilder loadOperations() {
        StringBuilder builder = new StringBuilder();
        String operationsJson = "{\"operations\":{";
        builder.append(operationsJson);

        List<ScheduledOperation> dbOperations = scheduledOperationRepo.findAll();
        for (ScheduledOperation dbOperation : dbOperations) {
            builder.append("\"");
            builder.append(dbOperation.getResourceId()).append("\":");
            builder.append(dbOperation.getOperations()).append(",");
        }
        builder.replace(builder.length() - 1, builder.length(), "");
        builder.append("}}");

        log.info("Load operations: {}", builder);

        return builder;
    }
}
