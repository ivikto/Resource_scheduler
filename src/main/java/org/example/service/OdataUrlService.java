package org.example.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.example.repo.StatusRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@Slf4j
@PropertySource("classpath:request.properties")
public class OdataUrlService {

    private final StatusRepo statusRepo;

    private final String baseUrl;
    private static final String DOC_TYPE = "Document_ЗаказНаПроизводство";

    private final String statusRefKey;

    public OdataUrlService(StatusRepo statusRepo, @Value("${baseUrl}") String baseUrl) {
        this.statusRepo = statusRepo;
        this.statusRefKey = initStatusRefKey();
        this.baseUrl = baseUrl;
    }

    public String getUrl() {
        return makeProdUrl();
    }

    public String getUrl(String refKey) {
        return makeNumUrl(refKey);
    }

    // Ссылка на все Заказы на производство в статусе "В работе", Изготовитель "Цех, Пометка на удаление "false"
    private String makeProdUrl() {
        String filterValue = "СостояниеЗаказа_Key";
        String filter = "?$filter=";
        String filterByValue = URLEncoder.encode(filterValue, StandardCharsets.UTF_8);
        String type = URLEncoder.encode(DOC_TYPE, StandardCharsets.UTF_8);
        String guid = " eq guid'";
        String delMark = "DeletionMark eq false";

        String url = baseUrl + type + filter + filterByValue + guid + statusRefKey +
                "' and "
                + delMark
                + "&$format=json";

        return url.replace(" ", "%20").replace("'", "%27");
    }

    // Ссылка на номенклатуру с фильтром по Ref_Key
    public String makeNumUrl(String refKey) {
        String filterValue = "Ref_Key";
        String filter = "?$filter=";
        String filterByValue = URLEncoder.encode(filterValue, StandardCharsets.UTF_8);
        String type = URLEncoder.encode("Catalog_Номенклатура", StandardCharsets.UTF_8);
        String guid = " eq guid'";

        String url = baseUrl + type + filter + filterByValue + guid + refKey + "'&$format=json";

        return url.replace(" ", "%20").replace("'", "%27");
    }

    private String initStatusRefKey() {
        try {
            String refKey = statusRepo.findRefKeyByName("В работе");
            if (refKey == null) {
                throw new IllegalStateException("Статус 'В работе' не найден в базе данных");
            }
            return refKey;
        } catch (Exception e) {
            log.error("Ошибка при получении refKey для статуса 'В работе'", e);
            throw new IllegalStateException("Не удалось получить refKey для статуса 'В работе'", e);
        }
    }
}
