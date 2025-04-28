package org.example.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.exception.InvalidJsonDataException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class OperationParser {

    private final ObjectMapper objectMapper = new ObjectMapper();


    public JsonNode parseOperation(String jsonRequest) {
        JsonNode operationsNode = null;
        try {
            JsonNode rootNode = objectMapper.readTree(jsonRequest);
            String operationsJson = rootNode.path("operations").asText();
            if (operationsJson == null || operationsJson.isEmpty()) {
                throw new InvalidJsonDataException("Operations json is null or empty");
            }
            // Парсим внутренний JSON как Map<String, List<...>>
            operationsNode = objectMapper.readTree(operationsJson);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return operationsNode;
    }
}
