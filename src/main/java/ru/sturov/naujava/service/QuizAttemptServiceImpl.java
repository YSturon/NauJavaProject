package ru.sturov.naujava.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sturov.naujava.dto.AttemptSubmission;
import ru.sturov.naujava.entity.AnswerOption;
import ru.sturov.naujava.entity.AttemptAnswer;
import ru.sturov.naujava.entity.Question;
import ru.sturov.naujava.entity.Quiz;
import ru.sturov.naujava.entity.QuizAttempt;
import ru.sturov.naujava.repository.AnswerOptionRepository;
import ru.sturov.naujava.repository.QuestionRepository;
import ru.sturov.naujava.repository.QuizAttemptRepository;
import ru.sturov.naujava.repository.QuizRepository;

/**
 * Реализация транзакционного сервиса отправки попытки прохождения теста.
 *
 * <p>В рамках одной транзакции:
 *
 * <ul>
 *   <li>загружается тест;
 *   <li>проверяются вопросы и выбранные варианты;
 *   <li>формируются ответы пользователя;
 *   <li>подсчитывается итоговый балл;
 *   <li>сохраняется {@link QuizAttempt}.
 * </ul>
 */
@Service
public class QuizAttemptServiceImpl implements QuizAttemptService {

    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final AnswerOptionRepository answerOptionRepository;
    private final QuizAttemptRepository quizAttemptRepository;

    public QuizAttemptServiceImpl(
            QuizRepository quizRepository,
            QuestionRepository questionRepository,
            AnswerOptionRepository answerOptionRepository,
            QuizAttemptRepository quizAttemptRepository) {
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
        this.answerOptionRepository = answerOptionRepository;
        this.quizAttemptRepository = quizAttemptRepository;
    }

    @Override
    @Transactional
    public QuizAttempt submitAttempt(Long quizId, String userName, List<AttemptSubmission> submissions) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new IllegalArgumentException("Тест не найден: " + quizId));

        QuizAttempt attempt = new QuizAttempt();
        attempt.setQuiz(quiz);
        attempt.setUserName(userName);
        attempt.setStartedAt(LocalDateTime.now());
        attempt.setFinishedAt(LocalDateTime.now());
        attempt.setScore(0);

        List<AttemptAnswer> answers = new ArrayList<>();
        int score = 0;

        for (AttemptSubmission submission : submissions) {
            Question question = questionRepository.findById(submission.getQuestionId())
                    .orElseThrow(() -> new IllegalArgumentException("Вопрос не найден: " + submission.getQuestionId()));

            AnswerOption option = answerOptionRepository.findById(submission.getSelectedOptionId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Вариант ответа не найден: " + submission.getSelectedOptionId()));

            if (!question.getQuiz().getId().equals(quizId)) {
                throw new IllegalArgumentException("Вопрос не относится к указанному тесту");
            }

            if (!option.getQuestion().getId().equals(question.getId())) {
                throw new IllegalStateException("Выбранный вариант ответа не относится к вопросу");
            }

            AttemptAnswer answer = new AttemptAnswer();
            answer.setAttempt(attempt);
            answer.setQuestion(question);
            answer.setSelectedOption(option);
            answer.setCorrect(Boolean.TRUE.equals(option.getCorrect()));

            if (Boolean.TRUE.equals(option.getCorrect())) {
                score++;
            }

            answers.add(answer);
        }

        attempt.setScore(score);
        attempt.setAnswers(answers);

        return quizAttemptRepository.save(attempt);
    }
}
