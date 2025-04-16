package org.example.service;

import org.example.entity.operationsType.*;
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
        //operationsLoad();
    }

    public void operationsLoad() {
        List<BandSaw> bandSawsOperations = operationsService.getAllOperations(BandSaw.class);
        List<LaserCleaner> laserCleanerOperations = operationsService.getAllOperations(LaserCleaner.class);
        List<LaserCutter> laserCutterOperations = operationsService.getAllOperations(LaserCutter.class);
        List<MillingMachine> millingMachineOperations = operationsService.getAllOperations(MillingMachine.class);
        List<Montage> montageOperations = operationsService.getAllOperations(Montage.class);
        List<Paint> paintOperations = operationsService.getAllOperations(Paint.class);
        List<PipeMachine> pipeMachineOperations = operationsService.getAllOperations(PipeMachine.class);
        List<Printer> printerOperations = operationsService.getAllOperations(Printer.class);
        List<RollingMachine> rollingMachinesOperations = operationsService.getAllOperations(RollingMachine.class);
        List<SheetBending> sheetBendingOperations = operationsService.getAllOperations(SheetBending.class);
        List<TurningMachine> turningMachineOperations = operationsService.getAllOperations(TurningMachine.class);
        List<Welding> weldingOperations = operationsService.getAllOperations(Welding.class);
        List<Drilling> drillingOperations = operationsService.getAllOperations(Drilling.class);
        //laserCutterOperations.forEach(System.out::println);

        System.out.println(bandSawsOperations.size() + laserCleanerOperations.size() + laserCutterOperations.size() + millingMachineOperations.size() + montageOperations.size() + paintOperations.size() + pipeMachineOperations.size() + printerOperations.size() + rollingMachinesOperations.size() + sheetBendingOperations.size() + turningMachineOperations.size() + weldingOperations.size() + drillingOperations.size());






    }
}
