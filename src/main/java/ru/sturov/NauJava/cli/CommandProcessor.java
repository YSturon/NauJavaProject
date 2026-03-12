package ru.sturov.naujava.cli;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.sturov.naujava.entity.Question;
import ru.sturov.naujava.service.QuestionService;

import java.util.List;

@Component
public class CommandProcessor {

    private final QuestionService questionService;

    @Autowired
    public CommandProcessor(QuestionService questionService) {
        this.questionService = questionService;
    }

    public void processCommand(String input) {
        try {
            String[] cmd = input.split(";", -1);
            String operation = cmd[0].trim();

            switch (operation) {
                case "create" -> handleCreate(cmd);
                case "read" -> handleRead(cmd);
                case "updateText" -> handleUpdateText(cmd);
                case "updateAnswer" -> handleUpdateAnswer(cmd);
                case "delete" -> handleDelete(cmd);
                case "list" -> handleList();
                case "listByTopic" -> handleListByTopic(cmd);
                case "quiz" -> handleQuiz(cmd);
                default -> System.out.println("Неизвестная команда.");
            }
        } catch (Exception ex) {
            System.out.println("Ошибка: " + ex.getMessage());
        }
    }

    private void handleCreate(String[] cmd) {
        requireLength(cmd, 6, "create;id;topic;text;difficulty;correctAnswer");

        questionService.createQuestion(
                Long.parseLong(cmd[1].trim()),
                cmd[2].trim(),
                cmd[3].trim(),
                cmd[4].trim(),
                cmd[5].trim()
        );

        System.out.println("Вопрос успешно создан.");
    }

    private void handleRead(String[] cmd) {
        requireLength(cmd, 2, "read;id");

        Question question = questionService.findById(Long.parseLong(cmd[1].trim()));
        System.out.println(question);
    }

    private void handleUpdateText(String[] cmd) {
        requireLength(cmd, 3, "updateText;id;newText");

        questionService.updateQuestionText(
                Long.parseLong(cmd[1].trim()),
                cmd[2].trim()
        );

        System.out.println("Текст вопроса успешно обновлен.");
    }

    private void handleUpdateAnswer(String[] cmd) {
        requireLength(cmd, 3, "updateAnswer;id;newCorrectAnswer");

        questionService.updateCorrectAnswer(
                Long.parseLong(cmd[1].trim()),
                cmd[2].trim()
        );

        System.out.println("Правильный ответ успешно обновлен.");
    }

    private void handleDelete(String[] cmd) {
        requireLength(cmd, 2, "delete;id");

        questionService.deleteById(Long.parseLong(cmd[1].trim()));
        System.out.println("Вопрос успешно удален.");
    }

    private void handleList() {
        List<Question> questions = questionService.findAll();

        if (questions.isEmpty()) {
            System.out.println("Список вопросов пуст.");
            return;
        }

        questions.forEach(System.out::println);
    }

    private void handleListByTopic(String[] cmd) {
        requireLength(cmd, 2, "listByTopic;topic");

        List<Question> questions = questionService.findByTopic(cmd[1].trim());
        if (questions.isEmpty()) {
            System.out.println("По указанной теме вопросы не найдены.");
            return;
        }

        questions.forEach(System.out::println);
    }

    private void handleQuiz(String[] cmd) {
        requireLength(cmd, 2, "quiz;topic");
        questionService.runQuizByTopic(cmd[1].trim());
    }

    private void requireLength(String[] cmd, int expectedLength, String usage) {
        if (cmd.length < expectedLength) {
            throw new IllegalArgumentException("Неверный формат команды. Используйте: " + usage);
        }
    }
}