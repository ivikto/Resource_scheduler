package org.example.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.OperationNomenclature;
import org.example.entity.Operation;
import org.example.entity.Production;
import org.example.exception.InvalidJsonDataException;
import org.example.exception.NomenclatureKeyMissingException;
import org.example.repo.OperationNomenclatureRepo;
import org.example.repo.OperationRepo;
import org.example.repo.ProductionRepo;
import org.example.repo.StatusRepo;
import org.example.repo.OperationKitRepo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Data
@RequiredArgsConstructor
public class OdataParserService {

    private List<Production> productions = new ArrayList<>();
    private List<Production> newProductions = new ArrayList<>();
    @Getter
    private List<Operation> allOperations = new ArrayList<>();

    private final OperationNomenclatureRepo operationNomenclatureRepo;
    private final OperationRepo operationRepo;
    private final ProductionRepo productionRepo;
    private final StatusRepo statusRepo;
    private final OperationKitRepo operationKitRepo;
    private final ProductionService productionService;

    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    private JsonNode rootArray;

    public List<Production> getProductions(String json) {
        parseProductions(json);
        parseOperations();
        productionService.saveProductionsAndOperations(productions, newProductions);

        return productions;
    }

    public OperationNomenclature getNomenclatureName(String json) {
        return parseNum(json);
    }

    private void parseProductions(String json) {
        try {
            rootArray = mapper.readTree(json);
            JsonNode valueArray = rootArray.get("value");

            // 1. Парсинг JSON в объекты
            for (JsonNode value : valueArray) {
                jsonToProduction(value);
            }

        } catch (JsonProcessingException e) {
            throw new InvalidJsonDataException(e.getMessage());
        }
    }

    private void jsonToProduction(JsonNode value) {

        try {
            Production production = mapper.treeToValue(value, Production.class);
            JsonNode myNode = value.get("Продукция");
            String numKey = getNomenclaturaKey(myNode);
            production.setManufacturedProductRefKey(numKey);
            productions.add(production);
        } catch (JsonProcessingException e) {
            throw new InvalidJsonDataException("JSON parsing error", e);
        }
    }

    private String getNomenclaturaKey(JsonNode myNode) {
        String numKey = null;
        for (JsonNode operation : myNode) {
            try {
                numKey = operation.path("Номенклатура_Key").asText();
            } catch (Exception e) {
                throw new NomenclatureKeyMissingException(e.getMessage());
            }
        }
        return numKey;
    }

    private void parseOperations() {
        // 2. Обработка операций
        for (Production production : productions) {
            List<Operation> operations = production.getOperations();
            if (operations != null) {
                for (Operation operation : operations) {
                    // Устанавливаем двунаправленную связь
                    operation.setProduction(production);  // Важно!
                    // Дополнительная обработка операции
                    operation.setNomenclature(operationNomenclatureRepo.findRefKeyByName(operation.getOperationKey()));
                    allOperations.add(operation);
                }
            }
        }
    }

    private OperationNomenclature parseNum(String json) {
        OperationNomenclature operationNomenclature = null;
        try {
            rootArray = mapper.readTree(json);
        } catch (JsonProcessingException e) {
            throw new InvalidJsonDataException(e.getMessage());
        }

        JsonNode valueArray = rootArray.get("value");
        for (JsonNode value : valueArray) {
            try {
                operationNomenclature = mapper.treeToValue(value, OperationNomenclature.class);

            } catch (JsonProcessingException e) {
                throw new InvalidJsonDataException(e.getMessage());
            }

        }
        assert operationNomenclature != null;
        return operationNomenclature;
    }
}