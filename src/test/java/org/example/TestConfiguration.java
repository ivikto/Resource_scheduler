package org.example;

import org.example.repo.ResourcesRepo;
import org.example.repo.ScheduledOperationRepo;
import org.example.repo.OperationKitRepo;
import org.example.service.*;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;

public class TestConfiguration {

    @Mock
    private OperationService operationService;

    @Mock
    private OperationKitRepo operationKitRepo;

    @Mock
    private ResourcesRepo resourcesRepo;

    @Mock
    private Runner runner;

    @Mock
    private ScheduledOperationRepo scheduledOperationRepo;

    @Mock
    private OperationKitService operationKitService;

    @Mock
    private OperationSplitService operationSplitService;

    @Mock
    private OperationParserService operationParserService;

    @Mock
    private ScheduledOperationService scheduledOperationService;

    @Mock
    private DataLoaderService dataLoaderService;

    @Mock
    private OdataUrlService odataUrlService;

    @Bean
    public OperationKitRepo operationsTypeRepo() {
        return Mockito.mock(OperationKitRepo.class);
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
    public OperationService operationsService() {
        return Mockito.mock(OperationService.class);
    }

    @Bean
    public OperationKitService operationsKitService() {
        return Mockito.mock(OperationKitService.class);
    }

    @Bean
    public OperationSplitService operationSplitService() {
        return Mockito.mock(OperationSplitService.class);
    }

    @Bean
    public OperationParserService operationParserService() {
        return Mockito.mock(OperationParserService.class);
    }

    @Bean
    public ScheduledOperationService scheduledOperationService() {
        return Mockito.mock(ScheduledOperationService.class);
    }

    @Bean
    public DataLoaderService dataLoaderService() {
        return Mockito.mock(DataLoaderService.class);
    }

    @Bean
    public OperationService operationService() {
        return Mockito.mock(OperationService.class);
    }

    @Bean
    public OdataUrlService odataUrlService() {
        return Mockito.mock(OdataUrlService.class);
    }

}
