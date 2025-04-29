package org.example.controllers;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.operations_type.OperationKit;
import org.example.exception.OperationSavingError;
import org.example.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor

public class OperationApiController {

    private final OperationSplitService operationSplitService;
    private final OperationParserService operationParserService;
    private final OperationService operationService;
    private final OperationKitService operationKitService;
    private final ScheduledOperationService scheduledOperationService;

    // Передаем OperationKit для отображения +
    @GetMapping("/operations")
    public ResponseEntity<List<OperationKit>> getAllOperations() {
        List<OperationKit> operationKitList = operationKitService.loadOperationsType();
        if (operationKitList.isEmpty()) {
            log.error("No OperationKit found in DB {}", new Date());
            return ResponseEntity.notFound().build();

        }
        return ResponseEntity.ok(operationKitList);
    }


    // Добавляем OperationKit на таймлайн +
    @GetMapping("/addInTimeLine/{id}")
    public ResponseEntity<String> addOperationInTimeLine(@PathVariable(value = "id") int id) {
        log.info("Запрос addInTimeLine получен для ID: {}", id);
        operationKitService.addOperationTypeInTimeLine(id);

        return ResponseEntity.ok().build();
    }

    // Разделяем OperationKit на И частей +
    @PostMapping("/splitOperation/{id}")
    public void operationSplit(@PathVariable(value = "id") int id, @RequestParam(value = "count") int count, @RequestBody(required = false) List<Integer> durations) {
        log.info("Запрос split получен для ID: {} и Count: {}", id, count);
        operationSplitService.splitOperations(id, durations);
    }

    // Устанавливаем MarkForDelete для OperationKit - True +
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> operationDelete(@PathVariable(value = "id") int id) {
        log.info("Запрос delete получен для ID: {}", id);
        operationKitService.deleteOperationsType(id);

        return ResponseEntity.ok().build();
    }

    // Сохраняем операцию на таймлайне +
    @PostMapping("/save-operations")
    @Transactional
    public ResponseEntity<String> scheduledOperationSave(@RequestBody String jsonRequest) {
        log.info("New operation for save: {}", jsonRequest);

        try {
            operationService.saveOperations(operationParserService.parseOperation(jsonRequest));
        } catch (Exception e) {
            log.error("Error saving operations", e);
            return ResponseEntity.internalServerError()
                    .body("Error processing operations: " + e.getMessage());
        }

        return ResponseEntity.ok().build();
    }

    // Загружаем операции на таймлайн +
    @GetMapping("/load-operations")
    public ResponseEntity<String> scheduledOperationLoad() {

        return ResponseEntity.ok(scheduledOperationService.loadOperations().toString());

    }
    // Удаляем операции с таймлайна
    @DeleteMapping("/deleteFromTimeLine/{id}")
    public void scheduledOperationDelete(@PathVariable(value = "id") String operationId, @RequestBody Map<String, String> request, HttpServletResponse response) {
        log.info("Operation delete from TimeLine: {}", operationId);
        try {
            scheduledOperationService.deleteScheduledOperation(operationId, request, response);
        } catch (IOException e) {
            log.error("Error saving operations {}",operationId, e);
            throw new OperationSavingError(e.getMessage());
        }
    }
}
