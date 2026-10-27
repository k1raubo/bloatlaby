package pl.edu.pg.eti.kask.pzw.permit.view;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import pl.edu.pg.eti.kask.pzw.permit.model.PermitModel;
import pl.edu.pg.eti.kask.pzw.permit.service.PermitService;
import pl.edu.pg.eti.kask.pzw.view.ViewResponses;

import java.util.Optional;

/**
 * Backing bean of the read-only permit details view.
 */
@Named
@RequestScoped
public class PermitView {

    private final PermitService permitService;

    /**
     * Permit identifier set from the {@code id} view parameter.
     */
    private String id;

    private PermitModel permit;

    /**
     * Required by CDI to create a client proxy.
     */
    protected PermitView() {
        this(null);
    }

    @Inject
    public PermitView(PermitService permitService) {
        this.permitService = permitService;
    }

    /**
     * Loads the permit. Responds with 404 if it does not exist.
     */
    public void init() {
        Optional<PermitModel> found = ViewResponses.parseId(id).flatMap(permitService::find).map(PermitModel::from);
        if (found.isEmpty()) {
            ViewResponses.notFound();
            return;
        }
        permit = found.get();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public PermitModel getPermit() {
        return permit;
    }

}
