package ru.sturov.naujava.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import ru.sturov.naujava.auth.entity.AppUserRole;

/** Конфигурация Spring Security. */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /** Возвращает кодировщик паролей приложения. */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Настраивает правила доступа, кастомный логин и logout.
     *
     * @param http объект конфигурации безопасности
     * @return собранная цепочка security-фильтров
     * @throws Exception если настройка завершилась ошибкой
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))
                .authorizeHttpRequests(auth -> auth.requestMatchers("/login", "/registration", "/error")
                        .permitAll()
                        .requestMatchers("/api/reports/**")
                        .hasRole(AppUserRole.ADMIN.name())
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**")
                        .hasRole(AppUserRole.ADMIN.name())
                        .anyRequest()
                        .authenticated())
                .formLogin(form -> form.loginPage("/login")
                        .defaultSuccessUrl("/view/categories/list", true)
                        .permitAll())
                .logout(logout -> logout.logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll());
        return http.build();
    }
}
