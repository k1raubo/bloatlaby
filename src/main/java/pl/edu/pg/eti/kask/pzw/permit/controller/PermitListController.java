package pl.edu.pg.eti.kask.pzw.permit.controller;

import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import pl.edu.pg.eti.kask.pzw.permit.dto.GetPermitsResponse;
import pl.edu.pg.eti.kask.pzw.permit.service.PermitService;
import pl.edu.pg.eti.kask.pzw.user.entity.UserRoles;

/**
 * Permits of all districts: every permit for an administrator, only the caller's own permits for a user. Single permits
 * are available under their district (see {@link PermitController}).
 */
@Path("/permits")
@RolesAllowed({UserRoles.ADMIN, UserRoles.USER})
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
public class PermitListController {

    private final PermitService permitService;

    /**
     * Required by CDI to create a client proxy.
     */
    protected PermitListController() {
        this(null);
    }

    @Inject
    public PermitListController(PermitService permitService) {
        this.permitService = permitService;
    }

    @GET
    public GetPermitsResponse getPermits() {
        return GetPermitsResponse.from(permitService.findAll());
    }

}
