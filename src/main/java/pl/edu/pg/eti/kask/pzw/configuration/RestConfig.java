package pl.edu.pg.eti.kask.pzw.configuration;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

/**
 * Activates Jakarta REST resources under {@code /api}. Users are still handled by
 * {@link pl.edu.pg.eti.kask.pzw.user.controller.UserServlet}, whose more specific mapping takes precedence.
 */
@ApplicationPath("/api")
public class RestConfig extends Application {
}
