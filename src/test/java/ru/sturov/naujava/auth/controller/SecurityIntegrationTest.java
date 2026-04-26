package ru.sturov.naujava.auth.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.sturov.naujava.auth.repository.AppUserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Security integration")
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AppUserRepository appUserRepository;

    @Test
    @DisplayName("Перенаправляет анонимного пользователя на страницу логина")
    void shouldRedirectAnonymousUserToLoginPage() throws Exception {
        mockMvc.perform(get("/view/categories/list"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    @DisplayName("Позволяет открыть страницу регистрации без авторизации")
    void shouldAllowAnonymousAccessToRegistrationPage() throws Exception {
        mockMvc.perform(get("/registration")).andExpect(status().isOk());
    }

    @Test
    @DisplayName("Регистрирует нового пользователя через HTML-форму")
    void shouldRegisterUserViaForm() throws Exception {
        mockMvc.perform(post("/registration")
                        .with(csrf())
                        .param("username", "web-user")
                        .param("password", "1234"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?registered"));

        org.assertj.core.api.Assertions.assertThat(appUserRepository.existsByUsername("web-user"))
                .isTrue();
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    @DisplayName("Запрещает пользователю USER доступ к OpenAPI")
    void shouldForbidSwaggerForRegularUser() throws Exception {
        mockMvc.perform(get("/v3/api-docs")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("Разрешает пользователю ADMIN доступ к OpenAPI")
    void shouldAllowSwaggerForAdmin() throws Exception {
        mockMvc.perform(get("/v3/api-docs")).andExpect(status().isOk());
    }
}
