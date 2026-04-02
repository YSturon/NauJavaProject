package ru.sturov.naujava.auth.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/** Адаптер между доменной моделью auth-контекста и моделью пользователей Spring Security. */
@Service
public class SecurityUserDetailsService implements UserDetailsService {

    private final AppUserService appUserService;

    public SecurityUserDetailsService(AppUserService appUserService) {
        this.appUserService = appUserService;
    }
    /**
     * Загружает пользователя приложения и преобразует его в {@link UserDetails}.
     *
     * @param username логин пользователя
     * @return пользователь Spring Security
     * @throws UsernameNotFoundException если пользователь не найден
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var appUser = appUserService
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден: " + username));

        return User.builder()
                .username(appUser.getUsername())
                .password(appUser.getPassword())
                .roles(appUser.getRole().name())
                .disabled(!Boolean.TRUE.equals(appUser.getEnabled()))
                .build();
    }
}
