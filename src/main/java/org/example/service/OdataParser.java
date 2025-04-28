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
import org.example.entity.Nomenclature;
import org.example.entity.Operation;
import org.example.entity.Production;
import org.example.exception.InvalidJsonDataException;
import org.example.exception.NomenclatureKeyMissingException;
import org.example.repo.NomenclatureRepo;
import org.example.repo.OperationRepo;
import org.example.repo.ProductionRepo;
import org.example.repo.StatusRepo;
import org.example.repo.operationsRepo.OperationsTypeRepo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@Data
@RequiredArgsConstructor
public class OdataParser {

    @Getter
    private static Set<String> set = new HashSet<>();
    private List<Production> productions = new ArrayList<>();
    private List<Production> newProductions = new ArrayList<>();
    @Getter
    private List<Operation> allOperations = new ArrayList<>();

    private final NomenclatureRepo nomenclatureRepo;
    private final OperationRepo operationRepo;
    private final ProductionRepo productionRepo;
    private final StatusRepo statusRepo;
    private final OperationsTypeRepo operationsTypeRepo;
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

    public Nomenclature getNomenclatureName(String json) {
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
        String nomenclaturaKey = null;
        try {
            Production production = mapper.treeToValue(value, Production.class);
            JsonNode myNode = value.get("Продукция");
            for (JsonNode operation : myNode) {
                try {
                    nomenclaturaKey = operation.path("Номенклатура_Key").asText();
                } catch (Exception e) {
                    throw new NomenclatureKeyMissingException(e.getMessage());
                }
            }
            production.setManufacturedProductRefKey(nomenclaturaKey);
            productions.add(production);
        } catch (JsonProcessingException e) {
            throw new InvalidJsonDataException("JSON parsing error", e);
        }
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
                    operation.setNomenclature(nomenclatureRepo.findRefKeyByName(operation.getOperationKey()));
                    allOperations.add(operation);
                }
            }
        }
    }

    private Nomenclature parseNum(String json) {
        Nomenclature nomenclature = null;
        try {
            rootArray = mapper.readTree(json);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            throw new InvalidJsonDataException(e.getMessage());
        }

        JsonNode valueArray = rootArray.get("value");
        for (JsonNode value : valueArray) {
            try {
                nomenclature = mapper.treeToValue(value, Nomenclature.class);

            } catch (JsonProcessingException e) {
                throw new InvalidJsonDataException(e.getMessage());
            }

        }
        assert nomenclature != null;
        return nomenclature;
    }
}