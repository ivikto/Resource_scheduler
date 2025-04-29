package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.operations_type.OperationKit;
import org.example.repo.OperationKitRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OperationKitService {

    private final OperationKitRepo operationKitRepo;

    public List<OperationKit> loadOperationsType () {

        return operationKitRepo.findByNotInTimeLine();
    }

    public void deleteOperationsType (int id) {
        OperationKit operationForDelete = operationKitRepo.findById(id).orElseThrow();
        operationForDelete.setMarkForDelete(true);
        operationKitRepo.save(operationForDelete);
        log.info("Deleted operationsType with id: {}", id);
    }

    public void addOperationTypeInTimeLine (int id) {
        OperationKit operationKit = operationKitRepo.findById(id).orElseThrow();
        operationKit.setInTimeLine(true);
        operationKitRepo.save(operationKit);
    }

}
