package org.example.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.Operation;
import org.example.entity.operationsType.OperationType;
import org.example.entity.timeline.ScheduledOperation;
import org.example.repo.ScheduledOperationRepo;
import org.example.repo.operationsRepo.OperationsTypeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/api")

public class OperationApiController {

    @Autowired
    private OperationsTypeRepo operationsTypeRepo;
    @Autowired
    private ScheduledOperationRepo scheduledOperationRepo;

    @GetMapping("/operations")
    public List<OperationType> getAllOperations() {
        return operationsTypeRepo.findByNotInTimeLine();
    }

    @DeleteMapping("/delete/{id}")
    public void deleteOperations(@PathVariable(value = "id") int id) {
        OperationType operationForDelete = operationsTypeRepo.findById(id).orElseThrow();
        operationForDelete.setMarkForDelete(true);
        operationsTypeRepo.save(operationForDelete);
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

    @PostMapping("/save-operations")
    @Transactional
    public ResponseEntity<?> saveOperations(@RequestBody ScheduledOperation operation) {
        if (operation.getResourceId() == null || operation.getOperations() == null) {
            return ResponseEntity.badRequest().body("Invalid operation data");
        }

        // Ищем существующие операции для ресурса
        List<ScheduledOperation> existingOperations = scheduledOperationRepo
                .findByResourceId(operation.getResourceId())
                .map(List::of)
                .orElse(Collections.emptyList());

        // Если есть существующие - обновляем первую найденную
        if (!existingOperations.isEmpty()) {
            ScheduledOperation existing = existingOperations.get(0);
            existing.setOperations(operation.getOperations());
            existing.setOperationDate(LocalDateTime.now());
            scheduledOperationRepo.save(existing);
            return ResponseEntity.ok(existing);
        }
        // Если нет - создаем новую
        else {
            operation.setOperationDate(LocalDateTime.now());
            ScheduledOperation saved = scheduledOperationRepo.save(operation);
            return ResponseEntity.ok(saved);
        }
    }

    @GetMapping("/load-operations")
    public ResponseEntity<List<ScheduledOperation>> loadOperations(
            @RequestParam(required = false) String resourceId) {
        List<ScheduledOperation> operations = resourceId != null
                ? scheduledOperationRepo.findByResourceId(resourceId)
                .map(List::of)
                .orElse(Collections.emptyList())
                : scheduledOperationRepo.findAll();
        log.info("Loaded {} operations", operations.size());
        return ResponseEntity.ok(operations);
    }

    @DeleteMapping("/deleteFromTimeLine/{id}")
    @Transactional
    public void deleteFromTimeLine(@PathVariable(value = "id") Long operationId, @RequestBody Map<String, String> request, HttpServletResponse response) throws IOException {
        System.out.println(operationId);
        try {
            String resourceId = request.get("resourceId");

            // 1. Находим запись ScheduledOperation для данного ресурса
            Optional<ScheduledOperation> scheduledOpt = scheduledOperationRepo
                    .findByResourceId(resourceId);

            if (scheduledOpt.isEmpty()) {
                response.sendError(HttpStatus.NOT_FOUND.value(), "Operation not found");
                return;
            }

            ScheduledOperation scheduled = scheduledOpt.get();

            // 2. Парсим JSON операций
            ObjectMapper mapper = new ObjectMapper();
            Map<String, List<Map<String, Object>>> operationsMap = mapper.readValue(
                    scheduled.getOperations(),
                    new TypeReference<Map<String, List<Map<String, Object>>>>() {
                    });

            // 3. Удаляем конкретную операцию из JSON
            boolean wasRemoved = false;
            for (List<Map<String, Object>> ops : operationsMap.values()) {
                Iterator<Map<String, Object>> iterator = ops.iterator();
                while (iterator.hasNext()) {
                    Map<String, Object> op = iterator.next();
                    if (operationId.equals(op.get("id"))) {
                        iterator.remove();
                        wasRemoved = true;
                        break;
                    }
                }
            }

            if (!wasRemoved) {
                response.sendError(HttpStatus.NOT_FOUND.value(), "Operation not found in schedule");
                return;
            }

            // 4. Обновляем запись в БД
            scheduled.setOperations(mapper.writeValueAsString(operationsMap));
            scheduledOperationRepo.save(scheduled);

            response.setStatus(HttpStatus.NO_CONTENT.value());

        } catch (JsonProcessingException e) {
            response.sendError(HttpStatus.BAD_REQUEST.value(), "Invalid operations format");
        } catch (Exception e) {
            log.error("Error deleting operation", e);
            response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Ошибка при удалении");
        }
    }
}
