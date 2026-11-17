package pl.edu.pg.eti.kask.pzw.security;

/**
 * Thrown by business logic when the caller is not allowed to perform an operation: either nobody is logged in, or the
 * caller lacks the required role or does not own the object.
 */
public class AccessDeniedException extends RuntimeException {

    public AccessDeniedException(String message) {
        super(message);
    }

}
