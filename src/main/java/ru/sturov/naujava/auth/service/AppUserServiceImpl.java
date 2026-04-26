package ru.sturov.naujava.auth.service;

import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import ru.sturov.naujava.auth.dto.UserRegistrationRequest;
import ru.sturov.naujava.auth.entity.AppUser;
import ru.sturov.naujava.auth.entity.AppUserRole;
import ru.sturov.naujava.auth.repository.AppUserRepository;

/**
 * Реализация сервиса регистрации и поиска пользователей.
 *
 * <p>Отвечает за валидацию данных, защиту от дублей и кодирование пароля перед сохранением.
 */
@Service
public class AppUserServiceImpl implements AppUserService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    public AppUserServiceImpl(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
    }
    /**
     * Возвращает пользователя по логину после его нормализации.
     *
     * @param username логин пользователя
     * @return найденный пользователь или пустой результат
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<AppUser> findByUsername(String username) {
        return appUserRepository.findByUsername(username == null ? "" : username.trim());
    }
    /**
     * Регистрирует нового пользователя приложения.
     *
     * @param request данные формы регистрации
     * @return сохранённый пользователь
     */
    @Override
    @Transactional
    public AppUser registerUser(UserRegistrationRequest request) {
        String username =
                request.getUsername() == null ? "" : request.getUsername().trim();
        String password = request.getPassword();

        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            throw new IllegalArgumentException("Логин и пароль обязательны");
        }
        if (password.length() < 4) {
            throw new IllegalArgumentException("Пароль должен быть длиной минимум 4 символа");
        }
        if (appUserRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Пользователь с таким логином уже существует");
        }

        AppUser user = new AppUser();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(AppUserRole.USER);
        user.setEnabled(true);
        user.setCreatedAt(LocalDateTime.now());
        return appUserRepository.save(user);
    }
}
