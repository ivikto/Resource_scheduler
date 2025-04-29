package org.example.controller;

import org.example.TestConfiguration;
import org.example.controllers.MainController;
import org.example.entity.Resources;
import org.example.entity.operations_type.OperationKit;
import org.example.repo.OperationKitRepo;
import org.example.repo.ResourcesRepo;
import org.example.service.OperationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@AutoConfigureMockMvc
@WebMvcTest(MainController.class)
@Import({TestConfiguration.class})
class MainControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OperationKitRepo operationKitRepo;

    @Autowired
    private ResourcesRepo resourcesRepo;

    @Autowired
    private OperationService operationService;


    @Test
    void test() throws Exception {

        List<OperationKit> mockOperations = List.of(new OperationKit(), new OperationKit());
        List<Resources> mockResources = List.of(new Resources());
        Map<String, Double> mockMap = Map.of("Operation1", 120.0);

        when(operationKitRepo.findByNotInTimeLine()).thenReturn(mockOperations);
        when(resourcesRepo.findAll()).thenReturn(mockResources);
        when(operationService.timeSumForOperations()).thenReturn(180.0);
        when(operationService.timeForAllOperations()).thenReturn(mockMap);


        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attribute("operations", mockOperations))
                .andExpect(model().attribute("resources", mockResources))
                .andExpect(model().attribute("timeTotalSum", 3.0))
                .andExpect(model().attribute("map", mockMap))
                .andExpect(model().attribute("operationsCount", mockOperations.size()));

    }
}
