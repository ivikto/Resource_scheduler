package org.example.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.Resources;
import org.example.entity.operationsType.OperationType;
import org.example.repo.ResourcesRepo;
import org.example.repo.operationsRepo.OperationsTypeRepo;
import org.example.service.Runner;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MainController {

    private final OperationsTypeRepo operationsTypeRepo;
    private final ResourcesRepo resourcesRepo;
    private final Runner runner;

    @GetMapping("/")
    public String index(Model model) {
        List<OperationType> operations = operationsTypeRepo.findByNotInTimeLine();
        List<Resources> resources = resourcesRepo.findAll();

        // Получаем общую сумму часов
        Double timeTotalSum = runner.timeSumForOperations();
        // Получаем сумму часов для каждой операции
        Map<String, Double> mapWithTimeOfOperations = runner.timeForAllOperations();


        model.addAttribute("operations", operations);
        model.addAttribute("resources", resources);
        model.addAttribute("timeTotalSum", timeTotalSum / 60);
        model.addAttribute("map", mapWithTimeOfOperations);


        return "index";
    }

    @GetMapping("/timeline")
    public String timeline(Model model) {
        List<OperationType> operations = operationsTypeRepo.findAll();
        model.addAttribute("operations", operations);

        return "timeline";
    }
}
