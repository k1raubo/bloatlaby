package pl.edu.pg.eti.kask.pzw.security;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.security.enterprise.SecurityContext;
import pl.edu.pg.eti.kask.pzw.user.entity.User;
import pl.edu.pg.eti.kask.pzw.user.entity.UserRoles;

import java.security.Principal;
import java.util.Arrays;

/**
 * Programmatic checks of the current caller (roles and object ownership) used by the business logic, on top of the
 * declarative {@code @RolesAllowed} checks in the REST controllers.
 */
@ApplicationScoped
public class Authorization {

    private final SecurityContext securityContext;

    /**
     * Required by CDI to create a client proxy.
     */
    protected Authorization() {
        this(null);
    }

    @Inject
    public Authorization(SecurityContext securityContext) {
        this.securityContext = securityContext;
    }

    /**
     * @return login of the authenticated caller
     * @throws AccessDeniedException if nobody is logged in
     */
    public String callerLogin() {
        Principal principal = securityContext.getCallerPrincipal();
        if (principal == null) {
            throw new AccessDeniedException("Authentication required");
        }
        return principal.getName();
    }

    public boolean isAdmin() {
        return securityContext.isCallerInRole(UserRoles.ADMIN);
    }

    /**
     * @throws AccessDeniedException if the caller has none of the roles
     */
    public void requireAnyRole(String... roles) {
        if (Arrays.stream(roles).noneMatch(securityContext::isCallerInRole)) {
            throw new AccessDeniedException("One of roles %s required".formatted(Arrays.toString(roles)));
        }
    }

    /**
     * @throws AccessDeniedException if the caller is neither an administrator nor the given owner
     */
    public void requireAdminOrOwner(User owner) {
        requireAnyRole(UserRoles.ADMIN, UserRoles.USER);
        if (!isAdmin() && (owner == null || !owner.getLogin().equals(callerLogin()))) {
            throw new AccessDeniedException("Only the owner or an administrator may access this object");
        }
    }

}
