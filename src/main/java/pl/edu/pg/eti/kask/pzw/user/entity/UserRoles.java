package pl.edu.pg.eti.kask.pzw.user.entity;

/**
 * Security role names, equal to the names of {@link Role} constants. Annotations such as {@code @RolesAllowed} need
 * compile-time constants, which enum names are not.
 */
public final class UserRoles {

    public static final String ADMIN = "ADMIN";

    public static final String USER = "USER";

    private UserRoles() {
    }

}
