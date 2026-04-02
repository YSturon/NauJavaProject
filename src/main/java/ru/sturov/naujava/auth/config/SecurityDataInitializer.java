package ru.sturov.naujava.auth.config;

import java.time.LocalDateTime;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.sturov.naujava.auth.entity.AppUser;
import ru.sturov.naujava.auth.entity.AppUserRole;
import ru.sturov.naujava.auth.repository.AppUserRepository;

@Component
public class SecurityDataInitializer implements CommandLineRunner {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    public SecurityDataInitializer(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        createUserIfMissing("admin", "admin", AppUserRole.ADMIN);
        createUserIfMissing("user", "user", AppUserRole.USER);
    }

    private void createUserIfMissing(String username, String rawPassword, AppUserRole role) {
        if (appUserRepository.existsByUsername(username)) return;
        AppUser user = new AppUser();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole(role);
        user.setEnabled(true);
        user.setCreatedAt(LocalDateTime.now());
        appUserRepository.save(user);
    }
}
