package ru.sturov.naujava.report.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.sturov.naujava.auth.entity.AppUser;
import ru.sturov.naujava.auth.entity.AppUserRole;
import ru.sturov.naujava.auth.repository.AppUserRepository;
import ru.sturov.naujava.entity.Category;
import ru.sturov.naujava.repository.AnswerOptionRepository;
import ru.sturov.naujava.repository.AttemptAnswerRepository;
import ru.sturov.naujava.repository.CategoryRepository;
import ru.sturov.naujava.repository.QuestionRepository;
import ru.sturov.naujava.repository.QuizAttemptRepository;
import ru.sturov.naujava.repository.QuizRepository;
import ru.sturov.naujava.report.dto.ReportContentView;
import ru.sturov.naujava.report.entity.Report;
import ru.sturov.naujava.report.entity.ReportStatus;
import ru.sturov.naujava.report.repository.ReportRepository;

@SpringBootTest
@DisplayName("ReportService")
class ReportServiceTest {

    @Autowired
    private ReportService reportService;

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerOptionRepository answerOptionRepository;

    @Autowired
    private QuizAttemptRepository quizAttemptRepository;

    @Autowired
    private AttemptAnswerRepository attemptAnswerRepository;

    @BeforeEach
    void setUp() {
        clearDatabase();

        createUser("admin-report", AppUserRole.ADMIN);
        createUser("user-report", AppUserRole.USER);

        createCategory("Java Core", "Базовые темы Java", true);
        createCategory("Spring", "Фреймворк Spring", true);
    }

    @Test
    @DisplayName("Создает отчет в статусе CREATED")
    void shouldCreateReportWithCreatedStatus() {
        Long reportId = reportService.createReport();

        Report report = reportRepository.findById(reportId).orElseThrow();

        assertThat(report.getStatus()).isEqualTo(ReportStatus.CREATED);
        assertThat(report.getContent())
                .contains("<html")
                .contains("Отчет формируется")
                .contains("CREATED");
    }

    @Test
    @DisplayName("Возвращает HTML-страницу ожидания для несформированного отчета")
    void shouldReturnPendingHtmlWhenReportIsNotReady() {
        Long reportId = reportService.createReport();

        ReportContentView contentView = reportService.getReportContent(reportId).orElseThrow();

        assertThat(contentView.status()).isEqualTo(ReportStatus.CREATED);
        assertThat(contentView.htmlContent())
                .contains("Отчет формируется")
                .contains("CREATED");
    }

    @Test
    @DisplayName("Асинхронно формирует HTML-отчет и сохраняет его в БД")
    void shouldGenerateHtmlReportAsynchronously() {
        Long reportId = reportService.createReport();

        reportService.generateReportAsync(reportId).join();

        Report report = reportRepository.findById(reportId).orElseThrow();

        assertThat(report.getStatus()).isEqualTo(ReportStatus.COMPLETED);
        assertThat(report.getContent())
                .contains("<html")
                .contains("Ключевые показатели")
                .contains("Количество зарегистрированных пользователей")
                .contains("Список категорий")
                .contains("Java Core")
                .contains("Spring")
                .contains("Общее время формирования, мс");
    }

    private void clearDatabase() {
        reportRepository.deleteAll();
        attemptAnswerRepository.deleteAll();
        quizAttemptRepository.deleteAll();
        answerOptionRepository.deleteAll();
        questionRepository.deleteAll();
        quizRepository.deleteAll();
        categoryRepository.deleteAll();
        appUserRepository.deleteAll();
    }

    private AppUser createUser(String username, AppUserRole role) {
        AppUser user = new AppUser();
        user.setUsername(username);
        user.setPassword("encoded-password");
        user.setRole(role);
        user.setEnabled(true);
        user.setCreatedAt(LocalDateTime.now());
        return appUserRepository.save(user);
    }

    private Category createCategory(String name, String description, boolean active) {
        Category category = new Category();
        category.setName(name);
        category.setDescription(description);
        category.setActive(active);
        return categoryRepository.save(category);
    }
}