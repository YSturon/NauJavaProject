package ru.sturov.naujava.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.sturov.naujava.dto.AttemptSubmission;
import ru.sturov.naujava.entity.AnswerOption;
import ru.sturov.naujava.entity.Category;
import ru.sturov.naujava.entity.Question;
import ru.sturov.naujava.entity.Quiz;
import ru.sturov.naujava.entity.QuizAttempt;
import ru.sturov.naujava.repository.AnswerOptionRepository;
import ru.sturov.naujava.repository.AttemptAnswerRepository;
import ru.sturov.naujava.repository.CategoryRepository;
import ru.sturov.naujava.repository.QuestionRepository;
import ru.sturov.naujava.repository.QuizAttemptRepository;
import ru.sturov.naujava.repository.QuizRepository;

@SpringBootTest
@DisplayName("QuizAttemptService")
class QuizAttemptServiceTest {

    @Autowired
    private QuizAttemptService quizAttemptService;

    @Autowired
    private QuizAttemptRepository quizAttemptRepository;

    @Autowired
    private AttemptAnswerRepository attemptAnswerRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerOptionRepository answerOptionRepository;

    private Quiz savedQuiz;
    private Question savedQuestion;
    private AnswerOption savedCorrectOption;

    @BeforeEach
    void setUp() {
        clearDatabase();

        Category savedCategory = createCategory("Базы данных", "Основы работы с базами данных");
        savedQuiz = createQuiz(savedCategory, "SQL-тест", "Проверка базовых знаний SQL", 20);
        savedQuestion =
                createQuestion(savedCategory, savedQuiz, "Как расшифровывается SQL?", 1, "Structured Query Language");

        savedCorrectOption = createOption(savedQuestion, "Structured Query Language", true, 1);
        createOption(savedQuestion, "Simple Query Language", false, 2);
    }

    @Test
    @DisplayName("Сохраняет попытку и ответы при корректной отправке")
    void shouldSaveAttemptAndAnswersWhenSubmissionIsValid() {
        QuizAttempt attempt = quizAttemptService.submitAttempt(
                savedQuiz.getId(),
                "Студент 1",
                List.of(new AttemptSubmission(savedQuestion.getId(), savedCorrectOption.getId())));

        assertThat(attempt.getScore()).isEqualTo(1);
        assertThat(attempt.getAnswers()).hasSize(1);
        assertThat(quizAttemptRepository.count()).isEqualTo(1);
        assertThat(attemptAnswerRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Откатывает транзакцию, если вариант ответа не относится к вопросу")
    void shouldRollbackTransactionWhenOptionDoesNotBelongToQuestion() {
        Category otherCategory = createCategory("Другое", "Другая категория");
        Quiz otherQuiz = createQuiz(otherCategory, "Другой тест", "Посторонний тест", 10);
        Question otherQuestion =
                createQuestion(otherCategory, otherQuiz, "Посторонний вопрос", 3, "Постороннее объяснение");
        final AnswerOption savedForeignOption = createOption(otherQuestion, "Посторонний вариант", true, 1);

        assertThatThrownBy(() -> quizAttemptService.submitAttempt(
                        savedQuiz.getId(),
                        "Студент 2",
                        List.of(new AttemptSubmission(savedQuestion.getId(), savedForeignOption.getId()))))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Выбранный вариант ответа не относится к вопросу");

        assertThat(quizAttemptRepository.count()).isZero();
        assertThat(attemptAnswerRepository.count()).isZero();
    }

    private void clearDatabase() {
        quizAttemptRepository.deleteAll();
        answerOptionRepository.deleteAll();
        questionRepository.deleteAll();
        quizRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    private Category createCategory(String name, String description) {
        Category category = new Category();
        category.setName(name);
        category.setDescription(description);
        category.setActive(true);
        return categoryRepository.save(category);
    }

    private Quiz createQuiz(Category category, String title, String description, int timeLimitMinutes) {
        Quiz quiz = new Quiz();
        quiz.setTitle(title);
        quiz.setDescription(description);
        quiz.setTimeLimitMinutes(timeLimitMinutes);
        quiz.setCategory(category);
        return quizRepository.save(quiz);
    }

    private Question createQuestion(
            Category category, Quiz quiz, String text, int difficultyLevel, String explanation) {
        Question question = new Question();
        question.setText(text);
        question.setDifficultyLevel(difficultyLevel);
        question.setExplanation(explanation);
        question.setCategory(category);
        question.setQuiz(quiz);
        return questionRepository.save(question);
    }

    private AnswerOption createOption(Question question, String text, boolean correct, int optionOrder) {
        AnswerOption option = new AnswerOption();
        option.setText(text);
        option.setCorrect(correct);
        option.setOptionOrder(optionOrder);
        option.setQuestion(question);
        return answerOptionRepository.save(option);
    }
}
