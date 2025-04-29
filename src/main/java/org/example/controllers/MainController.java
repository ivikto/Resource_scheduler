package org.example.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.Resources;
import org.example.entity.operations_type.OperationType;
import org.example.repo.ResourcesRepo;
import org.example.repo.OperationsTypeRepo;
import org.example.service.OperationsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MainController {

    private final OperationsTypeRepo operationsTypeRepo;
    private final ResourcesRepo resourcesRepo;
    private final OperationsService operationsService;

    @GetMapping("/")
    public String index(Model model) {
        List<OperationType> operations = operationsTypeRepo.findByNotInTimeLine();
        List<Resources> resources = resourcesRepo.findAll();

        // Получаем общую сумму часов и переводим в минуты
        Double timeTotalSum = operationsService.timeSumForOperations() / 60;
        // Получаем сумму часов для каждой операции
        Map<String, Double> mapWithTimeOfOperations = operationsService.timeForAllOperations();

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
