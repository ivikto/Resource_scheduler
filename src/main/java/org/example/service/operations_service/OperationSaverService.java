package org.example.service.operations_service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.operations_type.OperationKit;
import org.example.repo.OperationKitRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OperationSaverService {

    private final OperationKitRepo operationKitRepo;

    @Transactional
    public void saveOperation(OperationKit operation) {
        if (operation.getTime() == 0) {
            log.debug("Skipping operation with zero time: {}", operation.getNomenclatureName());
            return;
        }

        if (!exists(operation)) {
            operationKitRepo.save(operation);
            log.info("Saved new operation: {}", operation.getNomenclatureName());
        } else {
            log.debug("Operation already exists: {}", operation.getNomenclatureName());
        }
    }

    private boolean exists(OperationKit operation) {
        return operationKitRepo.existsByRefKeyAndNameAndTime(
                operation.getRefKey(),
                operation.getName(),
                operation.getTime()
        );
    }

}
