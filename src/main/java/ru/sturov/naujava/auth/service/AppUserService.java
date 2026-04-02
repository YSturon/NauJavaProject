package ru.sturov.naujava.auth.service;

import java.util.Optional;
import ru.sturov.naujava.auth.dto.UserRegistrationRequest;
import ru.sturov.naujava.auth.entity.AppUser;

public interface AppUserService {
    Optional<AppUser> findByUsername(String username);

    AppUser registerUser(UserRegistrationRequest request);
}
