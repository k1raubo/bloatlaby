package pl.edu.pg.eti.kask.pzw.user.dto;

import pl.edu.pg.eti.kask.pzw.user.entity.Role;
import pl.edu.pg.eti.kask.pzw.user.entity.User;

import java.time.LocalDate;
import java.util.UUID;

public record GetUserResponse(UUID id, String login, LocalDate birthday, Role role) {

    public static GetUserResponse from(User user) {
        return new GetUserResponse(user.getId(), user.getLogin(), user.getBirthday(), user.getRole());
    }

}
