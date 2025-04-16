package org.example.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.Nomenclature;
import org.example.entity.Operation;
import org.example.entity.Production;
import org.example.repo.NomenclatureRepo;
import org.example.repo.OperationRepo;
import org.example.repo.ProductionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@Data
public class JsonParse {

    public static Set<String> set = new HashSet<>();

    private final NomenclatureRepo nomenclatureRepo;
    private final OperationRepo operationRepo;
    private final ProductionRepo productionRepo;


    public ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    public JsonNode rootArray;

    @Autowired
    public JsonParse(NomenclatureRepo nomenclatureRepo, OperationRepo operationRepo, ProductionRepo productionRepo) {
        this.nomenclatureRepo = nomenclatureRepo;
        this.operationRepo = operationRepo;
        this.productionRepo = productionRepo;
    }

    public void parse(String json) {
        try {
            rootArray = mapper.readTree(json);
            JsonNode valueArray = rootArray.get("value");
            List<Production> productions = new ArrayList<>();
            String NomenclaturaKey = null;

            // 1. Парсинг JSON в объекты
            for (JsonNode value : valueArray) {
                try {
                    Production production = mapper.treeToValue(value, Production.class);
                    JsonNode myNode = value.get("Продукция");
                    for (JsonNode operation : myNode) {

                        try {
                            NomenclaturaKey = operation.path("Номенклатура_Key").asText();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                    production.setManufacturedProductRefKey(NomenclaturaKey);
                    productions.add(production);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException("JSON parsing error", e);
                }
            }
            // 2. Обработка операций
            for (Production production : productions) {
                List<Operation> operations = production.getOperations();

                if (operations != null) {
                    for (Operation operation : operations) {
                        // Устанавливаем двунаправленную связь
                        operation.setProduction(production);  // Важно!

                        // Дополнительная обработка операции
                        operation.setNomenclature(nomenclatureRepo.findRefKeyByName(operation.getOperationKey()));
                    }
                }
                // 3. Сохранение
                if (productionRepo.existsByRefKey(production.getRefKey())) {
                    log.warn("Duplicate production ref key: " + production.getRefKey());
                } else {
                    productionRepo.save(production);  // Каскадное сохранение сработает благодаря CascadeType.ALL
                }
            }
        } catch (JsonProcessingException e) {
            log.error("JSON processing error", e);
            throw new RuntimeException("Failed to parse JSON", e);
        }
    }

    public String parseNum(String json) {

        Nomenclature nomenc = null;
        try {
            rootArray = mapper.readTree(json);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }

        JsonNode valueArray = rootArray.get("value");
        for (JsonNode value : valueArray) {
            try {
                nomenc = mapper.treeToValue(value, Nomenclature.class);
                //nomenclatureRepo.save(nomenc);

            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

        }
        return nomenc.getDescription();
    }
}