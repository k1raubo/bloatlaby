package pl.edu.pg.eti.kask.pzw.configuration;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import pl.edu.pg.eti.kask.pzw.security.AccessDeniedException;

/**
 * Access denied by the business logic: 401 when nobody is logged in (the client should authenticate), 403 when the
 * logged-in caller lacks the role or does not own the object.
 */
@Provider
public class AccessDeniedExceptionMapper implements ExceptionMapper<AccessDeniedException> {

    @Context
    private SecurityContext securityContext;

    @Override
    public Response toResponse(AccessDeniedException exception) {
        if (securityContext.getUserPrincipal() == null) {
            return Response.status(Response.Status.UNAUTHORIZED).header(HttpHeaders.WWW_AUTHENTICATE, "Bearer").build();
        }
        return Response.status(Response.Status.FORBIDDEN).build();
    }

}
