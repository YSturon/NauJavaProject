package ru.sturov.naujava.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info =
                @Info(
                        title = "Система тестирования и оценки знаний",
                        version = "1.0",
                        description = "Практическая работа 5: Spring Web, REST API"))
public class OpenApiConfig {}
