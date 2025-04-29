package org.example.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
@PropertySource("classpath:request.properties")
public class AuthConfig {

    @Value(value = "${login}")
    private String login; // Логин и пароль
    @Value(value = "${pass}")
    private String pass; // Логин и пароль
    @Getter
    private String authInfo = login + ":" + pass;
    @Getter
    private String encodedAuth = Base64.getEncoder().encodeToString(authInfo.getBytes(StandardCharsets.UTF_8));


    @PostConstruct
    public void init() {
        // Инициализируем поля после внедрения значений
        this.authInfo = login + ":" + pass;
        this.encodedAuth = Base64.getEncoder().encodeToString(authInfo.getBytes(StandardCharsets.UTF_8));
    }

}
