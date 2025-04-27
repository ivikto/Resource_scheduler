package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.operationsType.*;
import org.example.repo.ResourcesRepo;
import org.example.repo.operationsRepo.OperationsTypeRepo;
import org.example.service.operationsService.OperationsService;
import org.example.service.operationsService.loaders.Loader;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class Runner {
    private final Request request;
    private final ScheduledUpdater updater;
    private final OperationsService operationsService;
    private final Loader loader;
    private final OperationsTypeRepo operationsTypeRepo;


    public void run() {
        //updater.updateProductions();

        //loader.operationsLoad();
        //loader.resourceLoad();
    }

    public Double timeSumForOperations() {
        List<OperationType> operations = operationsTypeRepo.findByNotInTimeLine();

        return operations.stream()
                .mapToDouble(o -> o.getTime())
                .sum();
    }

    public Map<String, Double> timeForAllOperations() {
        List<OperationType> operations = operationsTypeRepo.findByNotInTimeLine();

        return operations.stream()
                .collect(Collectors.groupingBy(OperationType::getName,
                        Collectors.summingDouble(op -> op.getTime() / 60.0) ));

    }
}
