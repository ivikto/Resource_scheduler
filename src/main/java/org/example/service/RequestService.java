package org.example.service;

import lombok.extern.slf4j.Slf4j;
import org.example.config.AuthConfig;
import org.example.entity.Nomenclature;
import org.example.entity.Operation;
import org.example.entity.Production;
import org.example.repo.ProductionRepo;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@PropertySource("classpath:request.properties")
public class RequestService {

    private final AuthConfig auth;
    private final OdataParserService odataParserService;
    private final OdataUrlService odataUrlService;
    private final ProductionRepo productionRepo;
    private final ProductionService productionService;

    public RequestService(AuthConfig auth, OdataParserService odataParserService, OdataUrlService odataUrlService, ProductionRepo productionRepo, ProductionService productionService) {
        this.auth = auth;
        this.odataParserService = odataParserService;
        this.odataUrlService = odataUrlService;
        this.productionRepo = productionRepo;
        this.productionService = productionService;
    }


    public void doRequest() {
        //Получаем ссылку для запроса
        String url = odataUrlService.getUrl();
        //Выполняем запрос
        String response = request(url);
        List<Production> productionList = odataParserService.getProductions(response);
        productionList.forEach(this::getNameOfNomenclature);
        operationsNomenclatureNameLoad();
    }

    public void operationsNomenclatureNameLoad() {
        List<Nomenclature> operationsNomenclatureList = new ArrayList<>();
        List<String> operationsKeys = odataParserService.getAllOperations().stream()
                .map(Operation::getOperationKey)
                .distinct()
                .toList();

        for (String key : operationsKeys) {
            String url = odataUrlService.getUrl(key);
            String response = request(url);

            operationsNomenclatureList.add(odataParserService.getNomenclatureName(response));
        }
        productionService.saveOperationsNomenclature(operationsNomenclatureList);
    }

    public void getNameOfNomenclature(Production production) {
        String url = odataUrlService.makeNumUrl(production.getManufacturedProductRefKey());
        String name = odataParserService.getNomenclatureName(request(url)).getDescription();
        production.setManufacturedProductName(name);
        productionRepo.save(production);

    }

    public String request(String requestUrl) {
        int responseCode;
        StringBuilder response = new StringBuilder();
        String line;
        try {
            URI uri = URI.create(requestUrl);
            URL url = uri.toURL();

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Basic " + auth.getEncodedAuth());
            responseCode = connection.getResponseCode();


            if (responseCode == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
            } else {
                log.error("Request error, Response code: {} ", responseCode);
            }

        } catch (MalformedURLException e) {
            log.error("Не корректный URL: {} ", e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return response.toString();
    }
}
