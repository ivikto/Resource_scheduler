package org.example.controllers;

import lombok.RequiredArgsConstructor;
import org.example.entity.Resources;
import org.example.entity.operationsType.OperationType;
import org.example.repo.OperationRepo;
import org.example.repo.ResourcesRepo;
import org.example.repo.operationsRepo.OperationsTypeRepo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;


@Controller
@RequiredArgsConstructor
public class MainController {

    private final OperationsTypeRepo operationsTypeRepo;
    private final ResourcesRepo resourcesRepo;

    @GetMapping("/")
    public String index(Model model) {
        List<OperationType> operations = operationsTypeRepo.findByNotInTimeLine();
        List<Resources> resources = resourcesRepo.findAll();
        model.addAttribute("operations", operations);
        model.addAttribute("resources", resources);

        return "index";
    }

    @GetMapping("/timeline")
    public String timeline(Model model) {
        List<OperationType> operations = operationsTypeRepo.findAll();
        model.addAttribute("operations", operations);

        return "timeline";
    }
}
