package org.example.controllers;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.operations_type.OperationType;
import org.example.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor

public class OperationApiController {

    private final OperationSplitService operationSplitService;
    private final OperationParser operationParser;
    private final OperationsService operationsService;
    private final OperationsTypeService operationsTypeService;
    private final ScheduledOperationService scheduledOperationService;

    @GetMapping("/operations")
    public List<OperationType> getAllOperations() {
        return operationsTypeService.loadOperationsType();
    }

    @DeleteMapping("/delete/{id}")
    public void deleteOperations(@PathVariable(value = "id") int id) {
        log.info("Запрос delete получен для ID: {}", id);
        operationsTypeService.deleteOperationsType(id);
    }

    @GetMapping("/addInTimeLine/{id}")
    public void addOperationInTimeLine(@PathVariable(value = "id") int id) {
        log.info("Запрос addInTimeLine получен для ID: {}", id);
        operationsTypeService.addOperationTypeInTimeLine(id);
    }

    @GetMapping("/delFromTimeLine/{id}")
    public void delFromTimeLine(@PathVariable(value = "id") int id) {
        log.info("Запрос delFromTimeLine получен для ID: {}", id);
        operationsTypeService.delOperationTypeFromTimeLine(id);
    }

    @PostMapping("/splitOperation/{id}")
    @Transactional
    public void splitOperation(@PathVariable(value = "id") int id, @RequestParam(value = "count") int count, @RequestBody(required = false) List<Integer> durations) {
        log.info("Запрос split получен для ID: {} и Count: {}", id, count);
        operationSplitService.splitOperations(id, durations);
    }

    @PostMapping("/save-operations")
    @Transactional
    public ResponseEntity<String> saveOperations(@RequestBody String jsonRequest) {
        log.info("New operation for save: {}", jsonRequest);

        // Парсим внутренний JSON как Map<String, List<...>>
        try {
            operationsService.saveOperations(operationParser.parseOperation(jsonRequest));
        } catch (Exception e) {
            log.error("Error saving operations", e);
            return ResponseEntity.internalServerError()
                    .body("Error processing operations: " + e.getMessage());
        }

        return ResponseEntity.ok().build();
    }

    @GetMapping("/load-operations")
    public String loadOperations() {
        return operationsService.loadOperations().toString();

    }

    @DeleteMapping("/deleteFromTimeLine/{id}")
    @Transactional
    public void deleteFromTimeLine(@PathVariable(value = "id") String operationId, @RequestBody Map<String, String> request, HttpServletResponse response) {
        log.info("Operation delete from TimeLine: {}", operationId);
        try {
            scheduledOperationService.deleteScheduledOperation(operationId, request, response);
        } catch (IOException e) {
            log.error("Error saving operations {}",operationId, e);
            throw new RuntimeException(e);
        }
    }
}
