package ru.sturov.naujava.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.sturov.naujava.cli.CommandProcessor;

import java.util.Scanner;

@Configuration
public class CliConfig {

    @Bean
    public CommandLineRunner commandScanner(CommandProcessor commandProcessor) {
        return args -> {
            try (Scanner scanner = new Scanner(System.in)) {
                printHelp();

                while (true) {
                    System.out.print("> ");
                    String input = scanner.nextLine();

                    if ("exit".equalsIgnoreCase(input.trim())) {
                        System.out.println("Завершение работы приложения...");
                        break;
                    }

                    commandProcessor.processCommand(input);
                }
            }
        };
    }

    private void printHelp() {
        System.out.println("Доступные команды:");
        System.out.println("create;id;topic;text;difficulty;correctAnswer");
        System.out.println("read;id");
        System.out.println("updateText;id;newText");
        System.out.println("updateAnswer;id;newCorrectAnswer");
        System.out.println("delete;id");
        System.out.println("list");
        System.out.println("listByTopic;topic");
        System.out.println("quiz;topic");
        System.out.println("exit");
    }
}