package pl.edu.pg.eti.kask.pzw.view;

import jakarta.faces.context.FacesContext;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Optional;
import java.util.UUID;

/**
 * Helpers shared by view beans.
 */
public final class ViewResponses {

    private ViewResponses() {
    }

    /**
     * @param id identifier passed as a view parameter
     * @return parsed identifier or empty if it is missing or malformed
     */
    public static Optional<UUID> parseId(String id) {
        try {
            return id == null ? Optional.empty() : Optional.of(UUID.fromString(id));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    /**
     * Ends rendering of the current view with the 404 Not Found status.
     */
    public static void notFound() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            context.getExternalContext().responseSendError(404, "Requested resource does not exist");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        context.responseComplete();
    }

}
