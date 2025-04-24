package org.example;

import org.example.service.Request;
import org.example.service.Runner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class AppStarter {

    public static void main(String[] args) {

        ApplicationContext context = SpringApplication.run(AppStarter.class, args);
        Runner runner = context.getBean(Runner.class);
        runner.run();

    }
}