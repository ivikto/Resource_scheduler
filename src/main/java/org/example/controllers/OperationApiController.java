package org.example.controllers;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.operationsType.OperationType;
import org.example.repo.operationsRepo.OperationsTypeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")

public class OperationApiController {

    @Autowired
    private OperationsTypeRepo operationsTypeRepo;

    @GetMapping("/operations")
    public List<OperationType> getAllOperations() {
        return operationsTypeRepo.findByNotInTimeLine();
    }

    @DeleteMapping ("/operations/{id}")
    public List<OperationType> deleteOperations(@PathVariable(value = "id") int id) {
        return operationsTypeRepo.findByNotInTimeLine();
    }

    @GetMapping("/addInTimeLine/{id}")
    public void addInTimeLine(@PathVariable(value = "id") int id) {
        log.info("Запрос addInTimeLine получен для ID: {}", id);
        OperationType operationType = operationsTypeRepo.findById(id).orElseThrow();
        operationType.setInTimeLine(true);
        operationsTypeRepo.save(operationType);
    }

    @GetMapping("/delFromTimeLine/{id}")
    public void delFromTimeLine(@PathVariable(value = "id") int id) {
        log.info("Запрос delFromTimeLine получен для ID: {}", id);
        OperationType operationType = operationsTypeRepo.findById(id).orElseThrow();
        operationType.setInTimeLine(false);
        operationsTypeRepo.save(operationType);
    }

    @PostMapping("/splitOperation/{id}")
    public void splitOperation(@PathVariable(value = "id") int id, @RequestParam(value = "count") int count, @RequestBody(required = false) List<Integer> durations) {
        log.info("Запрос split получен для ID: {} и Count: {}", id, count);
        OperationType oldOperation = operationsTypeRepo.findById(id).orElseThrow();
        List<OperationType> splitOperations = new ArrayList<>();
        for (int i = 0; i < durations.size(); i++) {
            OperationType splitOperation = OperationType.builder()
                    .color(oldOperation.getColor())
                    .number(oldOperation.getNumber())
                    .name(oldOperation.getName())
                    .time(durations.get(i))
                    .priority(oldOperation.getPriority())
                    .resource(oldOperation.getResource())
                    .nomenclatureName(oldOperation.getNomenclatureName())
                    .refKey(oldOperation.getRefKey())
                    .isEdited(true)
                    .inTimeLine(oldOperation.isInTimeLine())
                    .build();
            splitOperations.add(splitOperation);
        }
        System.out.println(splitOperations);
        operationsTypeRepo.saveAll(splitOperations);
        operationsTypeRepo.delete(oldOperation);
    }
}
