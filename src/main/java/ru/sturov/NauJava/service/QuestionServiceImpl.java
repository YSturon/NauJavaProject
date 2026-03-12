package ru.sturov.naujava.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.sturov.naujava.config.ApplicationProperties;
import ru.sturov.naujava.dao.QuestionRepository;
import ru.sturov.naujava.entity.Question;

import java.util.List;
import java.util.Scanner;

@Service
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final ApplicationProperties applicationProperties;

    @Autowired
    public QuestionServiceImpl(QuestionRepository questionRepository,
                               ApplicationProperties applicationProperties) {
        this.questionRepository = questionRepository;
        this.applicationProperties = applicationProperties;
    }

    @PostConstruct
    public void printApplicationInfo() {
        System.out.println("Название приложения: " + applicationProperties.getAppName());
        System.out.println("Версия приложения: " + applicationProperties.getAppVersion());
    }

    @Override
    public void createQuestion(Long id, String topic, String text, String difficulty, String correctAnswer) {
        validateId(id);
        validateText(topic, "Тема");
        validateText(text, "Текст вопроса");
        validateText(difficulty, "Сложность");
        validateText(correctAnswer, "Правильный ответ");

        if (questionRepository.existsById(id)) {
            throw new IllegalArgumentException("Вопрос с id=" + id + " уже существует.");
        }

        Question question = new Question(id, topic.trim(), text.trim(), difficulty.trim(), correctAnswer.trim());
        questionRepository.create(question);
    }

    @Override
    public Question findById(Long id) {
        validateId(id);
        Question question = questionRepository.read(id);
        if (question == null) {
            throw new IllegalArgumentException("Вопрос с id=" + id + " не найден.");
        }
        return question;
    }

    @Override
    public void deleteById(Long id) {
        validateId(id);
        if (!questionRepository.existsById(id)) {
            throw new IllegalArgumentException("Вопрос с id=" + id + " не найден.");
        }
        questionRepository.delete(id);
    }

    @Override
    public void updateQuestionText(Long id, String newText) {
        validateId(id);
        validateText(newText, "Новый текст вопроса");

        Question question = findById(id);
        question.setText(newText.trim());
        questionRepository.update(question);
    }

    @Override
    public void updateCorrectAnswer(Long id, String newCorrectAnswer) {
        validateId(id);
        validateText(newCorrectAnswer, "Новый правильный ответ");

        Question question = findById(id);
        question.setCorrectAnswer(newCorrectAnswer.trim());
        questionRepository.update(question);
    }

    @Override
    public List<Question> findAll() {
        return questionRepository.findAll();
    }

    @Override
    public List<Question> findByTopic(String topic) {
        validateText(topic, "Тема");
        return questionRepository.findByTopic(topic.trim());
    }

    @Override
    public int runQuizByTopic(String topic) {
        validateText(topic, "Тема");

        List<Question> questions = questionRepository.findByTopic(topic.trim());
        if (questions.isEmpty()) {
            throw new IllegalArgumentException("По теме \"" + topic + "\" вопросы не найдены.");
        }

        Scanner scanner = new Scanner(System.in);
        int correctAnswers = 0;

        System.out.println("Тема теста: " + topic);
        System.out.println("Количество вопросов: " + questions.size());

        for (Question question : questions) {
            System.out.println("----------------------------------------");
            System.out.println("ID вопроса: " + question.getId());
            System.out.println("Сложность: " + question.getDifficulty());
            System.out.println("Вопрос: " + question.getText());
            System.out.print("Ваш ответ: ");

            String userAnswer = scanner.nextLine();
            if (question.getCorrectAnswer().equalsIgnoreCase(userAnswer.trim())) {
                System.out.println("Верно.");
                correctAnswers++;
            } else {
                System.out.println("Неверно. Правильный ответ: " + question.getCorrectAnswer());
            }
        }

        System.out.println("----------------------------------------");
        System.out.println("Тест завершен.");
        System.out.println("Результат: " + correctAnswers + " из " + questions.size());

        return correctAnswers;
    }

    private void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Идентификатор должен быть положительным числом.");
        }
    }

    private void validateText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Поле \"" + fieldName + "\" не должно быть пустым.");
        }
    }
}