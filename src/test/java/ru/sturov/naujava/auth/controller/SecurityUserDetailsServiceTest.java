package ru.sturov.naujava.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.sturov.naujava.auth.entity.AppUser;
import ru.sturov.naujava.auth.entity.AppUserRole;
import ru.sturov.naujava.auth.repository.AppUserRepository;

@SpringBootTest
@DisplayName("SecurityUserDetailsService")
class SecurityUserDetailsServiceTest {

    @Autowired
    private SecurityUserDetailsService securityUserDetailsService;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        appUserRepository.deleteAll();

        AppUser admin = new AppUser();
        admin.setUsername("admin-test");
        admin.setPassword(passwordEncoder.encode("secret"));
        admin.setRole(AppUserRole.ADMIN);
        admin.setEnabled(true);
        admin.setCreatedAt(LocalDateTime.now());
        appUserRepository.save(admin);
    }

    @Test
    @DisplayName("Загружает пользователя и преобразует роль в authority Spring Security")
    void shouldLoadUserDetailsWithRole() {
        var userDetails = securityUserDetailsService.loadUserByUsername("admin-test");

        assertThat(userDetails.getUsername()).isEqualTo("admin-test");
        assertThat(userDetails.getPassword()).isNotBlank();
        assertThat(userDetails.getAuthorities()).extracting("authority").containsExactly("ROLE_ADMIN");
        assertThat(userDetails.isEnabled()).isTrue();
    }

    @Test
    @DisplayName("Выбрасывает исключение если пользователь не найден")
    void shouldThrowWhenUserNotFound() {
        assertThatThrownBy(() -> securityUserDetailsService.loadUserByUsername("missing"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("Пользователь не найден: missing");
    }
}
