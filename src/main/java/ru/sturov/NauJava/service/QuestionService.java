package ru.sturov.naujava.service;

import ru.sturov.naujava.entity.Question;

import java.util.List;

public interface QuestionService {

    void createQuestion(Long id, String topic, String text, String difficulty, String correctAnswer);

    Question findById(Long id);

    void deleteById(Long id);

    void updateQuestionText(Long id, String newText);

    void updateCorrectAnswer(Long id, String newCorrectAnswer);

    List<Question> findAll();

    List<Question> findByTopic(String topic);

    int runQuizByTopic(String topic);
}