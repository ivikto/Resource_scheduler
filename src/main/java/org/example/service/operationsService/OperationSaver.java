package org.example.service.operationsService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.operationsType.OperationType;
import org.example.repo.operationsRepo.OperationsTypeRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OperationSaver {

    private final OperationsTypeRepo operationsTypeRepo;

    @Transactional
    public void saveOperation(OperationType operation) {
        if (operation.getTime() == 0) {
            log.debug("Skipping operation with zero time: {}", operation.getNomenclatureName());
            return;
        }

        if (!exists(operation)) {
            operationsTypeRepo.save(operation);
            log.info("Saved new operation: {}", operation.getNomenclatureName());
        } else {
            log.debug("Operation already exists: {}", operation.getNomenclatureName());
        }
    }

    private boolean exists(OperationType operation) {
        return operationsTypeRepo.existsByRefKeyAndNameAndTime(
                operation.getRefKey(),
                operation.getName(),
                operation.getTime()
        );
    }

}
