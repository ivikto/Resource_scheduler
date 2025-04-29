package org.example.service;

import org.example.TestConfiguration;
import org.example.controllers.OperationApiController;
import org.example.entity.operations_type.OperationKit;
import org.example.utils.SpringClassUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
@Import({TestConfiguration.class})
class DataLoaderServiceTest {

    @Autowired
    private OperationService operationService;


    @Test
    void operationsLoad() {
        List<Class<?>> subClassList = SpringClassUtils.findSubclasses(OperationKit.class, "org.example.entity");

    }

    @Test
    void shutdown() {
    }

    @Test
    void resourceLoad() {
    }
}