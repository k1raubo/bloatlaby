package pl.edu.pg.eti.kask.pzw.configuration;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import pl.edu.pg.eti.kask.pzw.user.entity.Role;
import pl.edu.pg.eti.kask.pzw.user.entity.User;
import pl.edu.pg.eti.kask.pzw.user.service.UserService;

import java.time.LocalDate;
import java.util.UUID;

public class InitializeData implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {
        UserService userService = (UserService) event.getServletContext().getAttribute(UserService.class.getName());

        userService.create(user("c4804e0f-769e-4ab9-9ebe-0578fb4f00a6", "admin", LocalDate.of(1980, 5, 14), Role.ADMIN));
        userService.create(user("81e1c2a9-7f57-439b-b53d-6db88b071e4e", "kowalski", LocalDate.of(1992, 3, 2), Role.USER));
        userService.create(user("ed6cfb2a-cad7-47dd-9b56-9d1e3c7a4197", "nowak", LocalDate.of(2001, 11, 23), Role.USER));
        userService.create(user("f5875513-bf7b-4ae1-b8a5-5b70a1b90e76", "wisniewska", LocalDate.of(1975, 8, 30), Role.USER));
    }

    private static User user(String id, String login, LocalDate birthday, Role role) {
        User user = new User();
        user.setId(UUID.fromString(id));
        user.setLogin(login);
        user.setBirthday(birthday);
        user.setRole(role);
        return user;
    }

}
