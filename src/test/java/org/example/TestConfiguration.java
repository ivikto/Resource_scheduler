package org.example;

import org.example.repo.ResourcesRepo;
import org.example.repo.ScheduledOperationRepo;
import org.example.repo.OperationsTypeRepo;
import org.example.service.OperationsService;
import org.example.service.Runner;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;

public class TestConfiguration {

    @Mock
    private OperationsService operationsService;

    @Mock
    private OperationsTypeRepo operationsTypeRepo;

    @Mock
    private ResourcesRepo resourcesRepo;

    @Mock
    private Runner runner;

    @Mock
    private ScheduledOperationRepo scheduledOperationRepo;

    @Bean
    public OperationsTypeRepo operationsTypeRepo() {
        return Mockito.mock(OperationsTypeRepo.class);
    }

    @Bean
    public ResourcesRepo resourcesRepo() {
        return Mockito.mock(ResourcesRepo.class);
    }

    @Bean
    public Runner runner() {
        return Mockito.mock(Runner.class);
    }

    @Bean
    public ScheduledOperationRepo scheduledOperationRepo() {
        return Mockito.mock(ScheduledOperationRepo.class);
    }
    @Bean
    public OperationsService operationsService() {
        return Mockito.mock(OperationsService.class);
    }

}
