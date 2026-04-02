package ru.sturov.naujava.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.sturov.naujava.auth.dto.UserRegistrationRequest;
import ru.sturov.naujava.auth.entity.AppUser;
import ru.sturov.naujava.auth.entity.AppUserRole;
import ru.sturov.naujava.auth.repository.AppUserRepository;

@SpringBootTest
@DisplayName("AppUserService")
class AppUserServiceImplTest {

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        appUserRepository.deleteAll();
    }

    @Test
    @DisplayName("Регистрирует пользователя с ролью USER и шифрует пароль")
    void shouldRegisterUserAndEncodePassword() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setUsername("student");
        request.setPassword("1234");

        AppUser savedUser = appUserService.registerUser(request);

        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo("student");
        assertThat(savedUser.getRole()).isEqualTo(AppUserRole.USER);
        assertThat(savedUser.getEnabled()).isTrue();
        assertThat(savedUser.getCreatedAt()).isNotNull();
        assertThat(savedUser.getPassword()).isNotEqualTo("1234");
        assertThat(passwordEncoder.matches("1234", savedUser.getPassword())).isTrue();
    }

    @Test
    @DisplayName("Проверка дублирования логинов")
    void shouldRejectDuplicateUsername() {
        UserRegistrationRequest first = new UserRegistrationRequest();
        first.setUsername("student");
        first.setPassword("1234");
        appUserService.registerUser(first);

        UserRegistrationRequest duplicate = new UserRegistrationRequest();
        duplicate.setUsername("student");
        duplicate.setPassword("5678");

        assertThatThrownBy(() -> appUserService.registerUser(duplicate))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Пользователь с таким логином уже существует");
    }
}
