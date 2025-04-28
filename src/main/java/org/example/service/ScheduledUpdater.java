package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.service.operations_service.loaders.Loader;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledUpdater {

    private final Request request;
    private final Loader loader;


    @Scheduled(fixedRate = 10 * 60 * 1000)
    public void updateProductions() {
        try {
            long startTime = System.currentTimeMillis();
            request.doRequest();
            log.info("Updated productions at {}", new Date());
            loader.operationsLoad();
            log.info("Updating operations at: {} ", new Date());
            long endTime = System.currentTimeMillis();
            log.info("Updated data at: {} sek", (endTime - startTime) / 1000);
        } catch (Exception e) {
            log.error("Update database filed {}", e.getMessage(), e);
        }
    }
}
