package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.Nomenclature;
import org.example.entity.Operation;
import org.example.entity.Production;
import org.example.entity.operations_type.OperationType;
import org.example.repo.NomenclatureRepo;
import org.example.repo.OperationRepo;
import org.example.repo.ProductionRepo;
import org.example.repo.StatusRepo;
import org.example.repo.OperationsTypeRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ProductionService {

    private final OperationRepo operationRepo;
    private final ProductionRepo productionRepo;
    private final StatusRepo statusRepo;
    private final OperationsTypeRepo operationsTypeRepo;
    private final NomenclatureRepo nomenclatureRepo;

    public void saveProductionsAndOperations(List<Production> productions, List<Production> newProductions) {
        for (Production production : productions) {
            if (productionRepo.existsByRefKey(production.getRefKey())) {
                if (production.getCondition().equals(statusRepo.findRefKeyByName("Сделан")) ||
                        production.getCondition().equals(statusRepo.findRefKeyByName("Проверено ОТК")) ||
                        production.getCondition().equals(statusRepo.findRefKeyByName("Готов к выдаче"))) {
                    log.info("Найдено изменение статуса по существующему Производству {}", production.getProductionId());

                    production.setFinish(true);
                    productionRepo.save(production);

                    List<OperationType> operationsTypeOfProduction = operationsTypeRepo.findByRefKey(production.getRefKey());
                    operationsTypeOfProduction.forEach(operationType -> operationType.setFinish(true));
                    operationsTypeRepo.saveAll(operationsTypeOfProduction);

                    List<Operation> operationsOfProduction = operationRepo.getByProduction(production);
                    operationsOfProduction.forEach(operation -> operation.setFinish(true));
                    operationRepo.saveAll(operationsOfProduction);
                    log.info("Выполнено изменение isFinish для operation {} и operationType {} ",
                            operationsOfProduction.stream().map(Operation::getId).toList(),
                            operationsTypeOfProduction.stream().map(OperationType::getId).toList());
                }

            } else {
                newProductions.add(production);
                log.info("Добавлено новое производство: {}", production.getProductionId());
                productionRepo.save(production);  // Каскадное сохранение сработает благодаря CascadeType.ALL
            }
        }
    }

    @Transactional
    public void saveOperationsNomenclature(List<Nomenclature> nomenclatureList) {
        Set<String> existNomenclature = nomenclatureRepo.findAllRefKeys();

        List<Nomenclature> newNomenclatureList = nomenclatureList.stream()
                .filter(n -> !existNomenclature.contains(n.getRefKey()))
                .toList();

        if (!newNomenclatureList.isEmpty()) {
            nomenclatureRepo.saveAll(newNomenclatureList);
            log.info("New nomenclature for Operations added {}", newNomenclatureList);
        } else {
            log.info("No new nomenclature for Operations");
        }
    }
}
