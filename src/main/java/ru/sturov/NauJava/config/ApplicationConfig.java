package ru.sturov.NauJava.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import ru.sturov.naujava.entity.Question;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class ApplicationConfig {

    @Bean
    @Scope(BeanDefinition.SCOPE_SINGLETON)
    public List<Question> questionContainer() {
        return new ArrayList<>();
    }
}