package pl.edu.pg.eti.kask.pzw.user.controller;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import pl.edu.pg.eti.kask.pzw.user.dto.GetUserResponse;
import pl.edu.pg.eti.kask.pzw.user.dto.GetUsersResponse;
import pl.edu.pg.eti.kask.pzw.user.service.AvatarService;
import pl.edu.pg.eti.kask.pzw.user.service.UserService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet(urlPatterns = "/api/users/*")
@MultipartConfig(maxFileSize = 1024 * 1024)
public class UserServlet extends HttpServlet {

    private static final String AVATAR_PART = "avatar";

    private static final String ID = "(?<id>[0-9a-fA-F]{8}(-[0-9a-fA-F]{4}){3}-[0-9a-fA-F]{12})";

    private static final Pattern USERS = Pattern.compile("/?");

    private static final Pattern USER = Pattern.compile("/" + ID);

    private static final Pattern AVATAR = Pattern.compile("/" + ID + "/avatar");

    private final Jsonb jsonb = JsonbBuilder.create();

    private UserService userService;

    private AvatarService avatarService;

    @Override
    public void init() {
        userService = (UserService) getServletContext().getAttribute(UserService.class.getName());
        avatarService = (AvatarService) getServletContext().getAttribute(AvatarService.class.getName());
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            super.service(request, response);
        } catch (NoSuchElementException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String path = path(request);
        Matcher user = USER.matcher(path);
        Matcher avatar = AVATAR.matcher(path);
        if (USERS.matcher(path).matches()) {
            sendJson(response, GetUsersResponse.from(userService.findAll()));
        } else if (user.matches()) {
            sendJson(response, GetUserResponse.from(userService.find(id(user)).orElseThrow()));
        } else if (avatar.matches()) {
            byte[] content = avatarService.find(id(avatar));
            response.setContentType(imageType(content));
            response.setContentLength(content.length);
            response.getOutputStream().write(content);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Matcher avatar = AVATAR.matcher(path(request));
        if (!avatar.matches()) {
            rejectReadOnly(request, response);
            return;
        }
        if (request.getContentType() == null || !request.getContentType().startsWith("multipart/form-data")) {
            response.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
            return;
        }
        Part part;
        try {
            part = request.getPart(AVATAR_PART);
        } catch (IllegalStateException e) {
            // upload exceeds @MultipartConfig limits
            response.sendError(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE);
            return;
        }
        if (part == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        byte[] content = part.getInputStream().readAllBytes();
        if (imageType(content) == null) {
            response.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
            return;
        }
        if (avatarService.save(id(avatar), content)) {
            response.setHeader("Location", request.getRequestURL().toString());
            response.setStatus(HttpServletResponse.SC_CREATED);
        } else {
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Matcher avatar = AVATAR.matcher(path(request));
        if (!avatar.matches()) {
            rejectReadOnly(request, response);
            return;
        }
        avatarService.delete(id(avatar));
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    private void rejectReadOnly(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String path = path(request);
        if (USERS.matcher(path).matches() || USER.matcher(path).matches()) {
            response.setHeader("Allow", "GET");
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void sendJson(HttpServletResponse response, Object body) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(jsonb.toJson(body));
    }

    private static String path(HttpServletRequest request) {
        return request.getPathInfo() == null ? "" : request.getPathInfo();
    }

    private static UUID id(Matcher matcher) {
        return UUID.fromString(matcher.group("id"));
    }

    private static String imageType(byte[] content) throws IOException {
        String type = URLConnection.guessContentTypeFromStream(new ByteArrayInputStream(content));
        return type != null && type.startsWith("image/") ? type : null;
    }

}
