package pl.edu.pg.eti.kask.pzw.configuration;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import pl.edu.pg.eti.kask.pzw.user.repository.AvatarRepository;
import pl.edu.pg.eti.kask.pzw.user.repository.UserRepository;
import pl.edu.pg.eti.kask.pzw.user.service.AvatarService;
import pl.edu.pg.eti.kask.pzw.user.service.UserService;

import java.nio.file.Path;

public class CreateServices implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {
        ServletContext context = event.getServletContext();
        UserRepository userRepository = new UserRepository();
        AvatarRepository avatarRepository = new AvatarRepository(Path.of(context.getInitParameter("avatarDirectory")));

        context.setAttribute(UserService.class.getName(), new UserService(userRepository));
        context.setAttribute(AvatarService.class.getName(), new AvatarService(userRepository, avatarRepository));
    }

}
