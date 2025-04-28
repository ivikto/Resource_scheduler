package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.entity.Operation;
import org.example.entity.operations_type.OperationType;
import org.example.repo.OperationsTypeRepo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OperationSplitService {

    private final OperationsTypeRepo operationsTypeRepo;

    public void splitOperations(int id, List<Integer> durations) {

        OperationType oldOperation = operationsTypeRepo.findById(id).orElseThrow();

        List<OperationType> splitOperations = new ArrayList<>();
        for (Integer duration : durations) {
            OperationType splitOperation = OperationType.builder()
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

        operationsTypeRepo.saveAll(splitOperations);
        operationsTypeRepo.delete(oldOperation);
    }
}
