package ru.sturov.naujava.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.sturov.naujava.entity.Category;
import ru.sturov.naujava.entity.Question;
import ru.sturov.naujava.entity.Quiz;

@SpringBootTest
@DisplayName("QuestionRepository")
class QuestionRepositoryTest {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private QuizRepository quizRepository;

    private Category savedCategory;
    private Quiz savedQuiz;

    @BeforeEach
    void setUp() {
        clearDatabase();

        savedCategory = createCategory("Java Core", "Базовые темы Java");
        savedQuiz = createQuiz(savedCategory, "Основы Java", "Проверка базовых знаний", 30);

        createQuestion(
                savedCategory,
                savedQuiz,
                "Что такое JVM?",
                2,
                "Виртуальная машина Java");
    }

    @Test
    @DisplayName("Ищет вопрос по уровню сложности и названию категории через Query Method")
    void shouldFindQuestionByDifficultyAndCategoryNameUsingQueryMethod() {
        List<Question> result = questionRepository.findByDifficultyLevelAndCategoryName(2, "Java Core");

        assertThat(result)
                .hasSize(1)
                .extracting(Question::getText)
                .containsExactly("Что такое JVM?");
    }

    @Test
    @DisplayName("Ищет вопрос по названию теста через JPQL")
    void shouldFindQuestionByQuizTitleUsingJpql() {
        List<Question> result = questionRepository.findByQuizTitle("Основы Java");

        assertThat(result)
                .hasSize(1)
                .extracting(Question::getExplanation)
                .containsExactly("Виртуальная машина Java");
    }

    @Test
    @DisplayName("Ищет вопрос по уровню сложности и названию категории через Criteria API")
    void shouldFindQuestionByDifficultyAndCategoryNameUsingCriteria() {
        List<Question> result =
                questionRepository.searchByDifficultyLevelAndCategoryNameUsingCriteria(2, "Java Core");

        assertThat(result)
                .hasSize(1)
                .extracting(Question::getText)
                .containsExactly("Что такое JVM?");
    }

    @Test
    @DisplayName("Ищет вопрос по названию теста через Criteria API")
    void shouldFindQuestionByQuizTitleUsingCriteria() {
        List<Question> result = questionRepository.searchByQuizTitleUsingCriteria("Основы Java");

        assertThat(result)
                .hasSize(1)
                .extracting(Question::getExplanation)
                .containsExactly("Виртуальная машина Java");
    }

    private void clearDatabase() {
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
            Category category,
            Quiz quiz,
            String text,
            int difficultyLevel,
            String explanation) {
        Question question = new Question();
        question.setText(text);
        question.setDifficultyLevel(difficultyLevel);
        question.setExplanation(explanation);
        question.setCategory(category);
        question.setQuiz(quiz);
        return questionRepository.save(question);
    }
}
