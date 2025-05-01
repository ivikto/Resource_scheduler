package org.example.service;

import org.example.repo.StatusRepo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@PropertySource("classpath:request.properties")
class OdataUrlServiceTest {

    @Autowired
    private OdataUrlService odataUrlService;

    @Autowired
    private StatusRepo statusRepo;

    @Value("${baseUrl}")
    private String odataUrl;
    @Value("${numUrl}")
    private String numUrl;
    @Value("${prodUrl}")
    private String prodUrl;

    @TestConfiguration
    static class MockConfig {
        @Bean
        @Primary
        StatusRepo mockStatusRepo() {
            StatusRepo mock = mock(StatusRepo.class);
            when(mock.findRefKeyByName("В работе"))
                    .thenReturn("4f5e06a1-5f73-11ed-a1fd-d2166770609f");
            return mock;
        }
    }

    @Test
    @DisplayName("URL get test")
    void getUrlTest() {

        assertEquals(odataUrl + prodUrl, odataUrlService.getUrl());
        assertTrue(odataUrlService.getUrl("test").contains(numUrl));
    }


}