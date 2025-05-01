package org.example.service;

import org.example.entity.Production;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.util.List;

import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
class RequestServiceTest {

    @MockitoBean
    public OdataUrlService odataUrlService;

    @MockitoBean
    public OdataParserService odataParserService;

    @MockitoSpyBean
    private RequestService requestService;



    @Test
    void doRequest() {

        String fakeUrl = "https://odata/test";
        String fakeResponse = "response-json";
        Production prod1 = new Production();
        Production prod2 = new Production();
        List<Production> fakeList = List.of(prod1, prod2);

        when(odataUrlService.getUrl()).thenReturn(fakeUrl);
        doReturn(fakeResponse).when(requestService).request(fakeUrl);
        when(odataParserService.getProductions(fakeResponse)).thenReturn(fakeList);

        doNothing().when(requestService).getNameOfNomenclature(any());
        doNothing().when(requestService).operationsNomenclatureNameLoad();

        requestService.doRequest();

        verify(odataUrlService, times(2)).getUrl();

        verify(requestService).request(fakeUrl);
        verify(odataParserService).getProductions(fakeResponse);
        verify(requestService, times(2)).getNameOfNomenclature(any());
        verify(requestService).operationsNomenclatureNameLoad();

    }

    @Test
    void operationsNomenclatureNameLoad() {
    }

    @Test
    void getNameOfNomenclature() {
    }
}