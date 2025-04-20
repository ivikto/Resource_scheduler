package org.example.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.operationsType.OperationType;
import org.example.repo.operationsRepo.OperationsTypeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/addInTimeLine/{id}")
    public void addInTimeLine(@PathVariable(value = "id") int id) {
        log.info("Запрос получен для ID: {}", id);
        OperationType operationType = operationsTypeRepo.findById(id).orElseThrow();
        operationType.setInTimeLine(true);
        operationsTypeRepo.save(operationType);
        log.info("addInTimeLine - Выполнен");
        log.info("operationType: {}", operationType);
    }

    @GetMapping("/delFromTimeLine/{id}")
    public void delFromTimeLine(@PathVariable(value = "id") int id) {
        log.info("Запрос получен для ID: {}", id);
        OperationType operationType = operationsTypeRepo.findById(id).orElseThrow();
        operationType.setInTimeLine(false);
        operationsTypeRepo.save(operationType);
        log.info("delFromTimeLine - Выполнен");
        log.info("operationType: {}", operationType);
    }
}
