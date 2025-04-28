package org.example.service.operations_service;

import org.example.entity.Operation;
import org.example.entity.Production;
import org.example.entity.Resources;
import org.example.entity.operations_type.OperationType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

@Service
public class OperationBuilder {

    public <T extends OperationType> T buildOperation(
            Production production,
            Resources resource,
            List<String> supportedNum,
            Supplier<T> operationConstructor) {

        Objects.requireNonNull(production, "Production cannot be null");
        Objects.requireNonNull(resource, "Resource cannot be null");
        Objects.requireNonNull(operationConstructor, "Constructor cannot be null");

        T operation = operationConstructor.get();
        operation.setRefKey(production.getRefKey());
        operation.setNumber(production.getProductionId());
        operation.setPriority(production.getPriority());
        operation.setResource(resource);
        operation.setTime(calculateTime(production.getOperations(), supportedNum));
        operation.setNomenclatureName(production.getManufacturedProductName());

        return operation;
    }

    private double calculateTime(List<Operation> operations, List<String> supportedNum) {
        if (operations == null) return 0;

        return operations.stream()
                .filter(Objects::nonNull)
                .filter(operation -> supportedNum.contains(operation.getNomenclature()))
                .mapToDouble(Operation::getOperationTime)
                .sum() * 60; // переводим в минуты
    }
}
