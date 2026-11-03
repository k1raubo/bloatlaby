package pl.edu.pg.eti.kask.pzw.configuration;

import jakarta.json.bind.JsonbException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Malformed JSON in a request body is the client's fault, so it is reported as 400 instead of 500.
 */
@Provider
public class JsonbExceptionMapper implements ExceptionMapper<JsonbException> {

    @Override
    public Response toResponse(JsonbException exception) {
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

}
