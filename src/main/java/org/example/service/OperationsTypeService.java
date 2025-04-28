package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.operations_type.OperationType;
import org.example.repo.OperationsTypeRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OperationsTypeService {

    private final OperationsTypeRepo operationsTypeRepo;

    public List<OperationType> loadOperationsType () {

        return operationsTypeRepo.findByNotInTimeLine();
    }

    public void deleteOperationsType (int id) {
        OperationType operationForDelete = operationsTypeRepo.findById(id).orElseThrow();
        operationForDelete.setMarkForDelete(true);
        operationsTypeRepo.save(operationForDelete);
        log.info("Deleted operationsType with id: {}", id);
    }

    public void addOperationTypeInTimeLine (int id) {
        OperationType operationType = operationsTypeRepo.findById(id).orElseThrow();
        operationType.setInTimeLine(true);
        operationsTypeRepo.save(operationType);
    }

    public void delOperationTypeFromTimeLine (int id) {
        OperationType operationType = operationsTypeRepo.findById(id).orElseThrow();
        operationType.setInTimeLine(false);
        operationsTypeRepo.save(operationType);
    }
}
