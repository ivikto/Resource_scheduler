package org.example.controllers;

import lombok.RequiredArgsConstructor;
import org.example.entity.operationsType.OperationType;
import org.example.repo.operationsRepo.OperationsTypeRepo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;


@Controller
@RequiredArgsConstructor
public class MainController {

    private final OperationsTypeRepo operationsTypeRepo;

    @GetMapping("/")
    public String index(Model model) {
        List<OperationType> operations = operationsTypeRepo.findAll();
        model.addAttribute("operations", operations);

        return "index";
    }
}
