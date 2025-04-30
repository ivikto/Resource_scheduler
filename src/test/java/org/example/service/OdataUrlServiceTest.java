package org.example.service;

import org.example.repo.StatusRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.PropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {OdataUrlService.class, StatusRepo.class})
@PropertySource("classpath:request.properties")
class OdataUrlServiceTest {


    private OdataUrlService odataUrlService;

    @MockBean
    private StatusRepo statusRepo;

    @Value("${baseUrl}")
    private String odataUrl;
    @Value("${numUrl}")
    private String numUrl;
    @Value("${prodUrl}")
    private String prodUrl;

    @BeforeEach
    void setUp() {
        // Настраиваем mock-репозиторий
        statusRepo = Mockito.mock(StatusRepo.class);
        when(statusRepo.findRefKeyByName("В работе"))
                .thenReturn("4f5e06a1-5f73-11ed-a1fd-d2166770609f");

        odataUrlService = new OdataUrlService(statusRepo, odataUrl);
    }


    @Test
    void getUrlTest() {

        assertEquals(odataUrl + prodUrl, odataUrlService.getUrl());
        assertTrue(odataUrlService.getUrl("test").contains(numUrl));
    }

    @Test
    void testGetUrl() {
    }

    @Test
    void makeNumUrl() {
    }
}