package ru.sturov.naujava.config;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class ApplicationInfoPrinter {

    private final ApplicationProperties applicationProperties;

    public ApplicationInfoPrinter(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    @PostConstruct
    public void printApplicationInfo() {
        System.out.println("Название приложения: " + applicationProperties.getAppName());
        System.out.println("Версия приложения: " + applicationProperties.getAppVersion());
    }
}