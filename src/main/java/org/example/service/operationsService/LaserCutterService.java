package org.example.service.operationsService;

import lombok.RequiredArgsConstructor;
import org.example.entity.Operation;
import org.example.entity.Production;
import org.example.entity.operationsType.LaserCutter;
import org.example.repo.ProductionRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LaserCutterService {

    private final ProductionRepo productionRepo;

    public List<LaserCutter> getAllLaserCutter() {
        List<Production> productions = productionRepo.findAllWithOperations();
        return productions.stream()
                .map(this::convertToLaserCutter)
                .collect(Collectors.toList());
    }

    private LaserCutter convertToLaserCutter(Production production) {
        LaserCutter laserCutter = new LaserCutter();
        laserCutter.setRefKey(production.getRefKey());
        laserCutter.setNumber(production.getProductionId());
        laserCutter.setPriority(production.getPriority());
        double time = production.getOperations().stream()
                .filter(operation ->
                        operation.getNomenclature().equals("СПЦФ трудочаса рабочего лазер") ||
                        operation.getNomenclature().equals("СПЦФ ПР рез деталей на лазерном резчике"))
                .mapToDouble(Operation::getTime)
                .sum() * 60;
        laserCutter.setTime(time);

        return laserCutter;
    }
}
