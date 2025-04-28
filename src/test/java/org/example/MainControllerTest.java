package org.example;

import org.example.controllers.MainController;
import org.example.entity.Resources;
import org.example.entity.operations_type.OperationType;
import org.example.repo.ResourcesRepo;
import org.example.repo.OperationsTypeRepo;
import org.example.service.OperationsService;
import org.example.service.Runner;
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
public class MainControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OperationsTypeRepo operationsTypeRepo;

    @Autowired
    private ResourcesRepo resourcesRepo;

    @Autowired
    private Runner runner;

    @Autowired
    private OperationsService operationsService;


    @Test
    void test() throws Exception {

        List<OperationType> mockOperations = List.of(new OperationType(), new OperationType());
        List<Resources> mockResources = List.of(new Resources());
        Map<String, Double> mockMap = Map.of("Operation1", 120.0);

        when(operationsTypeRepo.findByNotInTimeLine()).thenReturn(mockOperations);
        when(resourcesRepo.findAll()).thenReturn(mockResources);
        when(operationsService.timeSumForOperations()).thenReturn(180.0);
        when(operationsService.timeForAllOperations()).thenReturn(mockMap);


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
