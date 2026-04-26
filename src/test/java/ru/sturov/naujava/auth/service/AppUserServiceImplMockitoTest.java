package ru.sturov.naujava.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.sturov.naujava.auth.dto.UserRegistrationRequest;
import ru.sturov.naujava.auth.entity.AppUser;
import ru.sturov.naujava.auth.entity.AppUserRole;
import ru.sturov.naujava.auth.repository.AppUserRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("AppUserService Mockito")
class AppUserServiceImplMockitoTest {

    @Mock
    private AppUserRepository appUserRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AppUserServiceImpl appUserService;

    @Test
    @DisplayName("Нормализует логин при поиске пользователя")
    void shouldNormalizeUsernameWhenFindingUser() {
        AppUser user = new AppUser();
        user.setUsername("student");

        when(appUserRepository.findByUsername("student")).thenReturn(Optional.of(user));

        Optional<AppUser> result = appUserService.findByUsername("  student  ");

        assertThat(result).containsSame(user);
        verify(appUserRepository).findByUsername("student");
    }

    @Test
    @DisplayName("Регистрирует пользователя, обрезает пробелы и шифрует пароль")
    void shouldRegisterUserWithTrimmedUsernameAndEncodedPassword() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setUsername("  student  ");
        request.setPassword("1234");

        when(passwordEncoder.encode("1234")).thenReturn("encoded-1234");
        when(appUserRepository.save(any(AppUser.class))).thenAnswer(invocation -> {
            AppUser user = invocation.getArgument(0);
            user.setId(100L);
            return user;
        });

        AppUser savedUser = appUserService.registerUser(request);

        assertThat(savedUser.getId()).isEqualTo(100L);
        assertThat(savedUser.getUsername()).isEqualTo("student");
        assertThat(savedUser.getPassword()).isEqualTo("encoded-1234");
        assertThat(savedUser.getRole()).isEqualTo(AppUserRole.USER);
        assertThat(savedUser.getEnabled()).isTrue();
        assertThat(savedUser.getCreatedAt()).isNotNull();

        verify(appUserRepository).existsByUsername("student");
        verify(passwordEncoder).encode("1234");

        ArgumentCaptor<AppUser> userCaptor = ArgumentCaptor.forClass(AppUser.class);
        verify(appUserRepository).save(userCaptor.capture());

        AppUser userToSave = userCaptor.getValue();
        assertThat(userToSave.getUsername()).isEqualTo("student");
        assertThat(userToSave.getPassword()).isEqualTo("encoded-1234");
        assertThat(userToSave.getRole()).isEqualTo(AppUserRole.USER);
        assertThat(userToSave.getEnabled()).isTrue();
        assertThat(userToSave.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Выбрасывает исключение при пустом логине или пароле")
    void shouldRejectBlankUsernameOrPassword() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setUsername("   ");
        request.setPassword("");

        assertThatThrownBy(() -> appUserService.registerUser(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Логин и пароль обязательны");

        verify(appUserRepository, never()).existsByUsername(anyString());
        verify(appUserRepository, never()).save(any(AppUser.class));
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    @DisplayName("Выбрасывает исключение при слишком коротком пароле")
    void shouldRejectTooShortPassword() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setUsername("student");
        request.setPassword("123");

        assertThatThrownBy(() -> appUserService.registerUser(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Пароль должен быть длиной минимум 4 символа");

        verify(appUserRepository, never()).save(any(AppUser.class));
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    @DisplayName("Выбрасывает исключение при регистрации пользователя с существующим логином")
    void shouldRejectDuplicateUsername() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setUsername("student");
        request.setPassword("1234");

        when(appUserRepository.existsByUsername("student")).thenReturn(true);

        assertThatThrownBy(() -> appUserService.registerUser(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Пользователь с таким логином уже существует");

        verify(appUserRepository).existsByUsername("student");
        verify(appUserRepository, never()).save(any(AppUser.class));
        verify(passwordEncoder, never()).encode(anyString());
    }
}
