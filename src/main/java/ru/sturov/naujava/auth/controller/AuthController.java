package ru.sturov.naujava.auth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.sturov.naujava.auth.dto.UserRegistrationRequest;
import ru.sturov.naujava.auth.service.AppUserService;

/** WEB-контроллер для страниц логина и регистрации. */
@Controller
public class AuthController {

    private final AppUserService appUserService;

    public AuthController(AppUserService appUserService) {
        this.appUserService = appUserService;
    }
    /** Возвращает страницу входа в систему. */
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
    /**
     * Возвращает страницу регистрации и подготавливает объект формы.
     *
     * @param model модель представления
     * @return имя thymeleaf-шаблона
     */
    @GetMapping("/registration")
    public String registrationPage(Model model) {
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new UserRegistrationRequest());
        }
        return "registration";
    }
    /**
     * Обрабатывает регистрацию нового пользователя.
     *
     * @param form данные формы регистрации
     * @param model модель представления
     * @return редирект на логин или повторное отображение формы с ошибкой
     */
    @PostMapping("/registration")
    public String registerUser(@ModelAttribute("form") UserRegistrationRequest form, Model model) {
        try {
            appUserService.registerUser(form);
            return "redirect:/login?registered";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("message", ex.getMessage());
            return "registration";
        }
    }
}
