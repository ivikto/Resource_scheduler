package org.example.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.timeline.ScheduledOperation;
import org.example.repo.ScheduledOperationRepo;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduledOperationService {

    private final ScheduledOperationRepo scheduledOperationRepo;

    public void deleteScheduledOperation(String operationId, Map<String, String> request, HttpServletResponse response) throws IOException {
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
                    new TypeReference<>() {
                    }
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

    public StringBuilder loadOperations() {
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

        log.info("Load operations: {}", builder);

        return builder;
    }
}
