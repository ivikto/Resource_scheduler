package org.example.service;

import org.example.entity.operationsType.LaserCutter;
import org.example.entity.operationsType.OperationType;
import org.example.entity.operationsType.SheetBending;
import org.example.service.operationsService.OperationsService;
import org.example.service.operationsService.converters.LaserCutterConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Runner {
    private final Request request;
    private final OperationsService operationsService;

    @Autowired
    public Runner(Request request, OperationsService operationsService) {
        this.request = request;
        this.operationsService = operationsService;
    }


    public void run() {
        //request.doRequest();
        List<LaserCutter> laserOperations = operationsService.getAllOperations(LaserCutter.class);
        List<SheetBending> sheetOperations = operationsService.getAllOperations(SheetBending.class);
        laserOperations.forEach(System.out::println);
        sheetOperations.forEach(System.out::println);
    }
}
