package org.example.service.operationsService.loaders;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.Resources;
import org.example.entity.operationsType.*;
import org.example.repo.ResourcesRepo;
import org.example.service.operationsService.OperationsService;
import org.example.utils.SpringClassUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Slf4j
@Service
public class Loader {

    private final ResourcesRepo resourcesRepo;
    private final OperationsService operationsService;
    private final ExecutorService executorService;

    @Autowired
    public Loader(ResourcesRepo resourcesRepo, OperationsService operationsService) {
        this.resourcesRepo = resourcesRepo;
        this.operationsService = operationsService;
        this.executorService = Executors.newFixedThreadPool(1);

    }

    public void operationsLoad() throws InterruptedException {
        List<Class<?>> subClassList = SpringClassUtils.findSubclasses(OperationType.class, "org.example.entity");
        CountDownLatch latch = new CountDownLatch(subClassList.size());

        subClassList.forEach(subClass -> {
            executorService.execute(() -> {
                try {
                    //log.info("Processing class: {}", subClass.getSimpleName());
                    operationsService.getAllOperations(subClass);
                } catch (Exception e) {
                    log.error("Error processing {}: {}", subClass.getSimpleName(), e.getMessage(), e);
                } finally {
                    latch.countDown(); // Уменьшаем счётчик в любом случае
                }
            });
        });

        latch.await(); // Ждём завершения всех задач
    }

    @PreDestroy
    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public void resourceLoad() {
        List<Resources> resources = new ArrayList<>();

        Resources resource1 = Resources.builder()
                .viewName("Лазерный резчик №1")
                .name("LaserCutter")
                .color("#FF5722")
                .build();
        resources.add(resource1);

        Resources resource2 = Resources.builder()
                .viewName("Лазерный резчик №2")
                .name("LaserCutter")
                .color("#FF5722")
                .build();
        resources.add(resource2);

        Resources resource3 = Resources.builder()
                .viewName("Листогиб №1")
                .name("SheetBending")
                .color("#00BCD4")
                .build();
        resources.add(resource3);

        Resources resource4 = Resources.builder()
                .viewName("Листогиб №2")
                .name("SheetBending")
                .color("#00BCD4")
                .build();
        resources.add(resource4);

        Resources resource5 = Resources.builder()
                .viewName("Ленточная пила")
                .name("BandSaw")
                .color("#4CAF50")
                .build();
        resources.add(resource5);

        Resources resource6 = Resources.builder()
                .viewName("Сверление")
                .name("Drilling")
                .color("#2196F3")
                .build();
        resources.add(resource6);

        Resources resource7 = Resources.builder()
                .viewName("Электро-эрозия")
                .name("ElectroErosion")
                .color("#9E9E9E")
                .build();
        resources.add(resource7);

        Resources resource8 = Resources.builder()
                .viewName("Шлифовальный станок")
                .name("GrindingMachine")
                .color("#FF9800")
                .build();
        resources.add(resource8);

        Resources resource9 = Resources.builder()
                .viewName("Лазерная чистка")
                .name("LaserCleaner")
                .color("#9C27B0")
                .build();
        resources.add(resource9);

        Resources resource10 = Resources.builder()
                .viewName("Фрезер 5 осевой")
                .name("MillingMachine")
                .color("#607D8B")
                .build();
        resources.add(resource10);

        Resources resource11 = Resources.builder()
                .viewName("Фрезер 4 осевой")
                .name("MillingMachine")
                .color("#607D8B")
                .build();
        resources.add(resource11);

        Resources resource12 = Resources.builder()
                .viewName("Монтаж №1")
                .name("Montage")
                .color("#795548")
                .build();
        resources.add(resource12);

        Resources resource20 = Resources.builder()
                .viewName("Монтаж №2")
                .name("Montage")
                .color("#795548")
                .build();
        resources.add(resource20);

        Resources resource13 = Resources.builder()
                .viewName("Станция окраски")
                .name("Paint")
                .color("#FFC107")
                .build();
        resources.add(resource13);

        Resources resource14 = Resources.builder()
                .viewName("Трубогиб")
                .name("PipeMachine")
                .color("#009688")
                .build();
        resources.add(resource14);

        Resources resource15 = Resources.builder()
                .viewName("3D печать")
                .name("Printer")
                .color("#E91E63")
                .build();
        resources.add(resource15);

        Resources resource16 = Resources.builder()
                .viewName("Вальцеватель")
                .name("RollingMachine")
                .color("#673AB7")
                .build();
        resources.add(resource16);

        Resources resource17 = Resources.builder()
                .viewName("Токарный станок")
                .name("TurningMachine")
                .color("#8BC34A")
                .build();
        resources.add(resource17);

        Resources resource18 = Resources.builder()
                .viewName("Сварка №1")
                .name("Welding")
                .color("#F44336")
                .build();
        resources.add(resource18);

        Resources resource19 = Resources.builder()
                .viewName("Сварка №2")
                .name("Welding")
                .color("#F44336")
                .build();
        resources.add(resource19);

        System.out.println(resources);

        resourcesRepo.saveAll(resources);
    }
}
