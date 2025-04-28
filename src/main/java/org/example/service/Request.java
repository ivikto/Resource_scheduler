package org.example.service;

import lombok.extern.slf4j.Slf4j;
import org.example.config.AuthConfig;
import org.example.entity.Nomenclature;
import org.example.entity.Operation;
import org.example.entity.Production;
import org.example.repo.ProductionRepo;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.stream.Collectors;

@Slf4j
@Component
@PropertySource("classpath:request.properties")
public class Request {

    private final AuthConfig auth;
    private final OdataParser odataParser;
    private final OdataUrl odataUrl;
    private final ProductionRepo productionRepo;
    private final ProductionService productionService;

    private int responseCode;

    public Request(AuthConfig auth, OdataParser odataParser, OdataUrl odataUrl, ProductionRepo productionRepo, ProductionService productionService) {
        this.auth = auth;
        this.odataParser = odataParser;
        this.odataUrl = odataUrl;
        this.productionRepo = productionRepo;
        this.productionService = productionService;
    }

    public void doRequest() {
        //Получаем ссылку для запроса
        String url = odataUrl.getUrl();
        //Выполняем запрос
        String response = request(url);
        List<Production> productionList = null;

        productionList = odataParser.getProductions(response);

        productionList.forEach(production -> {
            getNameOfNomenclature(production);
        });
        operationsNomenclatureNameLoad();
        //getNameOfNomenclature();


    }

    public void operationsNomenclatureNameLoad() {
        List<Nomenclature> operationsNomenclatureList = new ArrayList<>();
        List<String> OperationsKeys = odataParser.getAllOperations().stream()
                .map(o -> o.getOperationKey())
                .distinct()
                .toList();
        int count = 1;
        for (String key : OperationsKeys) {
            System.out.println();
            String url = odataUrl.getUrl(key);
            String response = request(url);
            //log.info("{}. {} : {}", count++, key, odataParser.getNomenclatureName(response));
            operationsNomenclatureList.add(odataParser.getNomenclatureName(response));
        }
        productionService.saveOperationsNomenclature(operationsNomenclatureList);
    }

    public void getNameOfNomenclature(Production production) {
        String url = odataUrl.makeNumUrl(production.getManufacturedProductRefKey());
        String name = odataParser.getNomenclatureName(request(url)).getDescription();
        production.setManufacturedProductName(name);
        productionRepo.save(production);

    }


    private String request(String requestUrl) {
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
