package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.entity.operations_type.OperationKit;
import org.example.repo.OperationKitRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OperationSplitService {

    private final OperationKitRepo operationKitRepo;

    @Transactional
    public void splitOperations(int id, List<Integer> durations) {

        OperationKit oldOperation = operationKitRepo.findById(id).orElseThrow();

        List<OperationKit> splitOperations = new ArrayList<>();
        for (Integer duration : durations) {
            OperationKit splitOperation = OperationKit.builder()
                    .color(oldOperation.getColor())
                    .number(oldOperation.getNumber())
                    .name(oldOperation.getName())
                    .time(duration)
                    .priority(oldOperation.getPriority())
                    .resource(oldOperation.getResource())
                    .nomenclatureName(oldOperation.getNomenclatureName())
                    .refKey(oldOperation.getRefKey())
                    .isEdited(true)
                    .inTimeLine(oldOperation.isInTimeLine())
                    .build();
            splitOperations.add(splitOperation);
        }

        operationKitRepo.saveAll(splitOperations);
        operationKitRepo.delete(oldOperation);
    }
}
