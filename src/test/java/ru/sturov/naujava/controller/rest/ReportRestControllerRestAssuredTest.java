package ru.sturov.naujava.controller.rest;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

import io.restassured.RestAssured;
import io.restassured.filter.cookie.CookieFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.sturov.naujava.auth.entity.AppUser;
import ru.sturov.naujava.auth.entity.AppUserRole;
import ru.sturov.naujava.auth.repository.AppUserRepository;
import ru.sturov.naujava.entity.Category;
import ru.sturov.naujava.report.repository.ReportRepository;
import ru.sturov.naujava.repository.CategoryRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("ReportRestController RestAssured")
class ReportRestControllerRestAssuredTest {

    private static final Pattern CSRF_INPUT_PATTERN = Pattern.compile(
            "<input[^>]*type=\"hidden\"[^>]*name=\"([^\"]+)\"[^>]*value=\"([^\"]+)\"[^>]*>",
            Pattern.CASE_INSENSITIVE);

    @LocalServerPort
    private int port;

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        reportRepository.deleteAll();
        categoryRepository.deleteAll();
        appUserRepository.deleteAll();

        createUser("admin", "admin", AppUserRole.ADMIN);
        createUser("user", "user", AppUserRole.USER);
        createCategory("Java Core", "Базовая категория для отчета", true);
    }

    @AfterEach
    void tearDown() {
        RestAssured.reset();
    }

    @Test
    @DisplayName("Создает отчет по POST /api/reports для администратора")
    void shouldCreateReportForAdmin() {
        CookieFilter cookieFilter = loginAs("admin", "admin");

        Response response = given().filter(cookieFilter).accept(ContentType.JSON).when().post("/api/reports");

        response.then().statusCode(202).contentType(ContentType.JSON).body("status", equalTo("CREATED"));

        long reportId = response.jsonPath().getLong("reportId");

        assertThat(reportId).isPositive();
        assertThat(response.jsonPath().getString("contentUrl")).isEqualTo("/api/reports/" + reportId);
        assertThat(response.getHeader("Location")).endsWith("/api/reports/" + reportId);
    }

    @Test
    @DisplayName("Возвращает 404 и HTML-страницу для несуществующего отчета")
    void shouldReturnNotFoundForUnknownReport() {
        CookieFilter cookieFilter = loginAs("admin", "admin");

        given().filter(cookieFilter)
                .when()
                .get("/api/reports/{reportId}", 999999L)
                .then()
                .statusCode(404)
                .contentType("text/html;charset=UTF-8")
                .body(containsString("999999"))
                .body(containsString("Отчет не найден"));
    }

    @Test
    @DisplayName("Возвращает 403 при попытке пользователя USER создать отчет")
    void shouldForbidRegularUserFromCreatingReport() {
        CookieFilter cookieFilter = loginAs("user", "user");

        given().filter(cookieFilter).when().post("/api/reports").then().statusCode(403);
    }

    @Test
    @DisplayName("Перенаправляет анонимного пользователя на страницу логина")
    void shouldRedirectAnonymousUserToLoginPage() {
        given().redirects().follow(false)
                .when()
                .get("/api/reports/{reportId}", 1L)
                .then()
                .statusCode(302)
                .header("Location", containsString("/login"));
    }

    private CookieFilter loginAs(String username, String password) {
        CookieFilter cookieFilter = new CookieFilter();

        Response loginPageResponse = given().filter(cookieFilter)
                .redirects()
                .follow(false)
                .when()
                .get("/login");

        loginPageResponse.then()
                .statusCode(200)
                .contentType(containsString("text/html"));

        CsrfField csrfField = extractCsrfField(loginPageResponse.asString());

        given().filter(cookieFilter)
                .contentType(ContentType.URLENC)
                .formParam(csrfField.name(), csrfField.value())
                .formParam("username", username)
                .formParam("password", password)
                .redirects()
                .follow(false)
                .when()
                .post("/login")
                .then()
                .statusCode(302)
                .header("Location", containsString("/view/categories/list"));

        return cookieFilter;
    }

    private CsrfField extractCsrfField(String html) {
        Matcher matcher = CSRF_INPUT_PATTERN.matcher(html);

        assertThat(matcher.find()).as("CSRF hidden input should be present on login page").isTrue();

        return new CsrfField(matcher.group(1), matcher.group(2));
    }

    private Category createCategory(String name, String description, boolean active) {
        Category category = new Category();
        category.setName(name);
        category.setDescription(description);
        category.setActive(active);
        return categoryRepository.save(category);
    }

    private AppUser createUser(String username, String rawPassword, AppUserRole role) {
        AppUser user = new AppUser();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole(role);
        user.setEnabled(true);
        user.setCreatedAt(LocalDateTime.now());
        return appUserRepository.save(user);
    }

    private record CsrfField(String name, String value) {}
}
