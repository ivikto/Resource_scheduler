package org.example.service;

import org.example.TestConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
@Import({TestConfiguration.class})
class RequestServiceTest {

    @Autowired
    public OdataUrlService odataUrlService;

    @Test
    void doRequest() {

        String url = "http://localhost:8080/odata";

        when(odataUrlService.getUrl()).thenReturn(url);

    }

    @Test
    void operationsNomenclatureNameLoad() {
    }

    @Test
    void getNameOfNomenclature() {
    }
}