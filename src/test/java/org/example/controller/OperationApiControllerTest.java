package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.example.TestConfiguration;
import org.example.controllers.OperationApiController;
import org.example.entity.operations_type.OperationKit;
import org.example.entity.timeline.ScheduledOperation;
import org.example.repo.ScheduledOperationRepo;
import org.example.service.OperationKitService;
import org.example.service.OperationSplitService;
import org.example.service.ScheduledOperationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WebMvcTest(OperationApiController.class)
@Import({TestConfiguration.class})
class OperationApiControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ScheduledOperationRepo scheduledOperationRepo;
    @Autowired
    private OperationKitService operationKitService;
    @Autowired
    private ScheduledOperationService scheduledOperationService;
    @Autowired
    private OperationSplitService operationSplitService;

    @Test
    void loadOperationTest() throws Exception {

        List<ScheduledOperation> dbOperationsMock = List.of(new ScheduledOperation(), new ScheduledOperation());

        when(scheduledOperationRepo.findAll()).thenReturn(dbOperationsMock);

        mockMvc.perform(get("/api/load-operations"))
                .andExpect(status().isOk());
    }

    @Test
    void getAllOperationsTest() throws Exception {
        List<OperationKit> operationKitList = List.of(new OperationKit(), new OperationKit());

        when(operationKitService.loadOperationsType()).thenReturn(operationKitList);
        mockMvc.perform(get("/api/operations"))
                .andExpect(status().isOk());

        assertEquals(operationKitList, operationKitService.loadOperationsType());
    }

    @Test
    void getAllOperationsFailTest() throws Exception {

        when(operationKitService.loadOperationsType()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/operations"))
                .andExpect(status().isNotFound());
    }

    @Test
    void addOperationInTimeLineTest() throws Exception {
        int id = 100;

        mockMvc.perform(get("/api/addInTimeLine/{id}", id))
                .andExpect(status().isOk());
    }

    @Test
    void operationSplitTest() throws Exception {
        int id = 100;
        int count = 5;
        List<Integer> durations = List.of(5, 6, 3);

        mockMvc.perform(post("/api/splitOperation/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(durations))
                        .param("count", String.valueOf(count)))

                .andExpect(status().isOk());

        verify(operationSplitService).splitOperations(id, durations);
    }

    @Test
    void operationDeleteTest() throws Exception {
        int id = 100;

        mockMvc.perform(delete("/api/delete/{id}", id))
                .andExpect(status().isOk());
    }

    @Test
    void scheduledOperationSaveTest() throws Exception {

        String json = "{\"operations\": \"{\"1\":[]}";
        mockMvc.perform(post("/api/save-operations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }

    @Test
    void scheduledOperationLoadTest() throws Exception {

        when(scheduledOperationService.loadOperations()).thenReturn(new StringBuilder());

        mockMvc.perform(get("/api/load-operations"))
                .andExpect(status().isOk());
    }

    @Test
    void scheduledOperationDeleteTest() throws Exception {
        String operationId = "100";
        Map<String, String> request = new HashMap<>();

        mockMvc.perform(delete("/api/deleteFromTimeLine/{id}", operationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}
