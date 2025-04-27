package org.example;

import org.example.repo.ResourcesRepo;
import org.example.repo.ScheduledOperationRepo;
import org.example.repo.operationsRepo.OperationsTypeRepo;
import org.example.service.Runner;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;

public class TestConfiguration {

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

}
