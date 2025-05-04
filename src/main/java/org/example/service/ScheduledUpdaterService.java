package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledUpdaterService {

    private final RequestService requestService;
    private final DataLoaderService dataLoaderService;


    @Scheduled(fixedRate = 10 * 60 * 1000)
    public void updateProductions() {
        try {
            long startTime = System.currentTimeMillis();
            requestService.doRequest();
            log.info("Updated productions at {}", new Date());
            dataLoaderService.operationsLoad();
            log.info("Updating operations at: {} ", new Date());
            long endTime = System.currentTimeMillis();
            log.info("Updated data at: {} sek", (endTime - startTime) / 1000);

            log.info("Do request to Api");
            String response = requestService.request("http://127.0.0.1:8081/api/get_info_productions");
            log.info("Api response: {}", response);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Update database filed {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("Update database filed {}", e.getMessage(), e);
        }
    }
}
