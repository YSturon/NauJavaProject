package ru.sturov.naujava.ui;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.bonigarcia.wdm.WebDriverManager;
import java.time.Duration;
import java.time.LocalDateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.sturov.naujava.auth.entity.AppUser;
import ru.sturov.naujava.auth.entity.AppUserRole;
import ru.sturov.naujava.auth.repository.AppUserRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Tag("ui")
@DisplayName("Login/logout UI")
class LoginLogoutUiTest {

    @LocalServerPort
    private int port;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    void setUp() {
        appUserRepository.deleteAll();
        createUser("admin", "admin", AppUserRole.ADMIN);

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--window-size=1400,1000");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @DisplayName("Позволяет администратору войти и выйти из системы")
    void shouldLoginAndLogoutSuccessfully() {
        driver.get(baseUrl("/login"));

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-testid='login-page-title']")));

        driver.findElement(By.cssSelector("[data-testid='username-input']")).sendKeys("admin");
        driver.findElement(By.cssSelector("[data-testid='password-input']")).sendKeys("admin");
        driver.findElement(By.cssSelector("[data-testid='login-button']")).click();

        wait.until(ExpectedConditions.urlContains("/view/categories/list"));
        wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-testid='category-page-title']")));

        assertThat(driver.getCurrentUrl()).contains("/view/categories/list");
        assertThat(driver.getPageSource()).contains("Список категорий");

        driver.findElement(By.cssSelector("[data-testid='logout-button']")).click();

        wait.until(ExpectedConditions.urlContains("/login?logout"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-testid='logout-message']")));

        assertThat(driver.getCurrentUrl()).contains("/login?logout");
        assertThat(driver.getPageSource()).contains("Вы успешно вышли из системы");
    }

    private String baseUrl(String path) {
        return "http://localhost:" + port + path;
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
}
