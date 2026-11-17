package pl.edu.pg.eti.kask.pzw.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import pl.edu.pg.eti.kask.pzw.user.entity.User;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Registration of a new user. The identifier comes from the resource address; the role cannot be chosen, every new
 * user is a regular user.
 */
public record PutUserRequest(

        @NotBlank(message = "Podaj login.")
        @Size(min = 3, max = 30, message = "Login musi mieć od 3 do 30 znaków.")
        @Pattern(regexp = "[A-Za-z0-9_.-]*", message = "Login może zawierać tylko litery, cyfry oraz znaki _ . -")
        String login,

        @NotBlank(message = "Podaj hasło.")
        @Size(min = 4, max = 100, message = "Hasło musi mieć od 4 do 100 znaków.")
        String password,

        @Past(message = "Data urodzenia musi być w przeszłości.")
        LocalDate birthday) {

    public User toEntity(UUID id) {
        User user = new User();
        user.setId(id);
        user.setLogin(login);
        user.setBirthday(birthday);
        return user;
    }

}
