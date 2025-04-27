package org.example;

import org.example.controllers.OperationApiController;
import org.example.entity.timeline.ScheduledOperation;
import org.example.repo.ScheduledOperationRepo;
import org.example.repo.operationsRepo.OperationsTypeRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WebMvcTest(OperationApiController.class)
@Import({TestConfiguration.class})
public class OperationApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OperationsTypeRepo operationsTypeRepo;

    @Autowired
    private ScheduledOperationRepo scheduledOperationRepo;

    @Test
    void testLoadOperation() throws Exception {

        List<ScheduledOperation> MockDbOperations = List.of(new ScheduledOperation(), new ScheduledOperation());

        when(scheduledOperationRepo.findAll()).thenReturn(MockDbOperations);

        mockMvc.perform(get("/api/load-operations"))
                .andExpect(status().isOk());
    }

    @Test
    void testSaveOperation() throws Exception {

        List<ScheduledOperation> MockDbOperations = List.of(new ScheduledOperation(), new ScheduledOperation());

        when(scheduledOperationRepo.saveAll(MockDbOperations)).thenReturn(MockDbOperations);

        mockMvc.perform(get("/api/save-operations"))
                .andExpect(status().isOk());
    }
}
