package pl.edu.pg.eti.kask.pzw.configuration;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;

/**
 * Provides a single, thread-safe JSON-B instance shared by the whole application.
 */
@ApplicationScoped
public class JsonbProducer {

    @Produces
    @ApplicationScoped
    public Jsonb jsonb() {
        return JsonbBuilder.create();
    }

    public void close(@Disposes Jsonb jsonb) throws Exception {
        jsonb.close();
    }

}
