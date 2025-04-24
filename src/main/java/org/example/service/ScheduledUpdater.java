package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.service.operationsService.loaders.Loader;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.ScheduledExecutorService;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledUpdater {

    private final Request request;
    private final Loader loader;


    @Scheduled(fixedRate = 5 * 60 * 1000)
    public void updateProductions() {
        try {
            request.doRequest();
            loader.operationsLoad();
            log.info("Updating database at: {} ", new Date());
        } catch (Exception e) {
            log.error("Update database filed {}", e.getMessage(), e);
        }
    }
}
