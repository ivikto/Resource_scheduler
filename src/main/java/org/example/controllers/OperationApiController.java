package org.example.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RestController
@RequestMapping("/api")

public class OperationApiController {

    private final OperationsTypeRepo operationsTypeRepo;

    private final ScheduledOperationRepo scheduledOperationRepo;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public OperationApiController(OperationsTypeRepo operationsTypeRepo, ScheduledOperationRepo scheduledOperationRepo) {
        this.operationsTypeRepo = operationsTypeRepo;
        this.scheduledOperationRepo = scheduledOperationRepo;
    }

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
    public ResponseEntity<?> saveOperations(@RequestBody String jsonRequest) {
        System.out.println("New operation: " + jsonRequest);

        try {
            JsonNode rootNode = objectMapper.readTree(jsonRequest);
            String operationsJson = rootNode.path("operations").asText();

            if (operationsJson == null || operationsJson.isEmpty()) {
                return ResponseEntity.badRequest().body("Operations data is missing");
            }

            // Парсим внутренний JSON как Map<String, List<...>>
            JsonNode operationsNode = objectMapper.readTree(operationsJson);

            Iterator<Map.Entry<String, JsonNode>> fields = operationsNode.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                String resourceId = entry.getKey();
                String operationsForResource = entry.getValue().toString();

                // Сохраняем операции для каждого ресурса
                scheduledOperationRepo.findByResourceId(resourceId)
                        .ifPresentOrElse(
                                existing -> {
                                    existing.setOperations(operationsForResource);
                                    existing.setOperationDate(LocalDateTime.now());
                                    scheduledOperationRepo.save(existing);
                                },
                                () -> {
                                    ScheduledOperation newOp = new ScheduledOperation();
                                    newOp.setResourceId(resourceId);
                                    newOp.setOperations(operationsForResource);
                                    newOp.setOperationDate(LocalDateTime.now());
                                    scheduledOperationRepo.save(newOp);
                                }
                        );
            }

            return ResponseEntity.ok().build();

        } catch (Exception e) {
            log.error("Error saving operations", e);
            return ResponseEntity.internalServerError()
                    .body("Error processing operations: " + e.getMessage());
        }
    }

    @GetMapping("/load-operations")
    public String loadOperations() {

        StringBuilder builder = new StringBuilder();
        String operationsJson = "{\"operations\":{";
        builder.append(operationsJson);

        List<ScheduledOperation> dbOperations = scheduledOperationRepo.findAll();
        for (ScheduledOperation dbOperation : dbOperations) {
            builder.append("\"");
            builder.append(dbOperation.getResourceId()).append("\":");
            builder.append(dbOperation.getOperations()).append(",");
        }
        builder.replace(builder.length() - 1, builder.length(), "");
        builder.append("}}");

        System.out.println("Load operations: " + builder.toString());

        return builder.toString();

    }

    @DeleteMapping("/deleteFromTimeLine/{id}")
    @Transactional
    public void deleteFromTimeLine(@PathVariable(value = "id") String operationId, @RequestBody Map<String, String> request, HttpServletResponse response) throws IOException {

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

            // 1. Парсим JSON операций
            ObjectMapper mapper = new ObjectMapper();
            List<Map<String, Object>> operationsList = mapper.readValue(
                    scheduled.getOperations(),
                    new TypeReference<List<Map<String, Object>>>() {}
            );

// 2. Удаляем операцию по ID (надежный способ)
            operationsList.removeIf(op -> {
                Object id = op.get("id");
                // Сравниваем как строки
                return id.toString().equals(operationId);
            });


            // 4. Обновляем запись в БД
            scheduled.setOperations(mapper.writeValueAsString(operationsList));
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
