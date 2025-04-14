package org.example.service;

import lombok.extern.slf4j.Slf4j;
import org.example.config.AuthConfig;
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

import static org.example.service.JsonParse.set;

@Slf4j
@Component
@PropertySource("classpath:request.properties")
public class Request {

    private final AuthConfig auth;
    private final JsonParse jsonParse;
    private final OdataUrl odataUrl;


    private int responseCode;


    @Autowired
    public Request(AuthConfig auth, JsonParse jsonParse, OdataUrl odataUrl) {
        this.auth = auth;
        this.jsonParse = jsonParse;
        this.odataUrl = odataUrl;

    }

    public void doRequest() {
        String url = odataUrl.getUrl();
        String response = request(url);
        jsonParse.parse(response);
        nomenclatureLoad();


    }

    public void nomenclatureLoad() {
        for (String key : set) {
            System.out.println(key);
            //String url = odataUrl.getUrl(key);
            //String response2 = request(url);
            //jsonParse.parseNum(response2);
        }
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
