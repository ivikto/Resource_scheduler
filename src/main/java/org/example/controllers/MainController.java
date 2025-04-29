package org.example.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.Resources;
import org.example.entity.operations_type.OperationKit;
import org.example.repo.ResourcesRepo;
import org.example.repo.OperationKitRepo;
import org.example.service.OperationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MainController {

    private final OperationKitRepo operationKitRepo;
    private final ResourcesRepo resourcesRepo;
    private final OperationService operationService;

    @GetMapping("/")
    public String index(Model model) {
        List<OperationKit> operations = operationKitRepo.findByNotInTimeLine();
        List<Resources> resources = resourcesRepo.findAll();

        // Получаем общую сумму часов и переводим в минуты
        Double timeTotalSum = operationService.timeSumForOperations() / 60;
        // Получаем сумму часов для каждой операции
        Map<String, Double> mapWithTimeOfOperations = operationService.timeForAllOperations();

        // Добавляем атрибуты в модель
        model
                .addAttribute("operations", operations)
                .addAttribute("resources", resources)
                .addAttribute("timeTotalSum", timeTotalSum)
                .addAttribute("map", mapWithTimeOfOperations)
                .addAttribute("operationsCount", operations.size());

        return "index";
    }

}
