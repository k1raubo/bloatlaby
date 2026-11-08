package pl.edu.pg.eti.kask.pzw.configuration;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ElementKind;
import jakarta.validation.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.Comparator;
import java.util.List;

/**
 * Invalid request bodies (constraints on the request DTOs) are reported as 400 with the list of broken constraints,
 * so the client knows which fields to fix.
 */
@Provider
public class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    /**
     * @param field   name of the invalid field, or {@code null} when the whole body is invalid (e.g. missing)
     * @param message description of the broken constraint
     */
    public record Violation(String field, String message) {
    }

    public record ViolationsResponse(List<Violation> violations) {
    }

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        List<Violation> violations = exception.getConstraintViolations().stream()
                .map(violation -> new Violation(field(violation), violation.getMessage()))
                .sorted(Comparator.comparing(Violation::field, Comparator.nullsFirst(Comparator.naturalOrder())))
                .toList();
        return Response.status(Response.Status.BAD_REQUEST)
                .type(MediaType.APPLICATION_JSON)
                .entity(new ViolationsResponse(violations))
                .build();
    }

    /**
     * Property path of a violation on a resource method is {@code method.parameter[.field]}, the field is its last
     * node unless the parameter itself is invalid.
     */
    private static String field(ConstraintViolation<?> violation) {
        Path.Node last = null;
        for (Path.Node node : violation.getPropertyPath()) {
            last = node;
        }
        return last != null && last.getKind() == ElementKind.PROPERTY ? last.getName() : null;
    }

}
