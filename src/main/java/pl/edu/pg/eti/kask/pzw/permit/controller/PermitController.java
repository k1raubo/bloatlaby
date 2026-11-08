package pl.edu.pg.eti.kask.pzw.permit.controller;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import pl.edu.pg.eti.kask.pzw.permit.dto.GetPermitResponse;
import pl.edu.pg.eti.kask.pzw.permit.dto.GetPermitsResponse;
import pl.edu.pg.eti.kask.pzw.permit.dto.PatchPermitRequest;
import pl.edu.pg.eti.kask.pzw.permit.dto.PutPermitRequest;
import pl.edu.pg.eti.kask.pzw.permit.entity.Permit;
import pl.edu.pg.eti.kask.pzw.permit.service.PermitService;

import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * Permits (elements) as a REST resource nested under their district. The district is always taken from the address,
 * and a permit addressed through a district it does not belong to is reported as not found.
 */
@Path("/districts/{districtId}/permits")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
public class PermitController {

    private final PermitService permitService;

    /**
     * Required by CDI to create a client proxy.
     */
    protected PermitController() {
        this(null);
    }

    @Inject
    public PermitController(PermitService permitService) {
        this.permitService = permitService;
    }

    @GET
    public GetPermitsResponse getPermits(@PathParam("districtId") UUID districtId) {
        try {
            return GetPermitsResponse.from(permitService.findAllByDistrict(districtId));
        } catch (NoSuchElementException e) {
            throw new NotFoundException();
        }
    }

    @GET
    @Path("/{id}")
    public GetPermitResponse getPermit(@PathParam("districtId") UUID districtId, @PathParam("id") UUID id) {
        return permitService.find(districtId, id).map(GetPermitResponse::from).orElseThrow(NotFoundException::new);
    }

    /**
     * Creates a permit for the district from the address under the given identifier.
     *
     * @return 201 with the address of the new permit
     */
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response putPermit(@PathParam("districtId") UUID districtId, @PathParam("id") UUID id,
                              @Valid @NotNull PutPermitRequest request, @Context UriInfo uriInfo) {
        try {
            permitService.create(request.toEntity(id, districtId));
        } catch (NoSuchElementException e) {
            throw new NotFoundException();
        } catch (IllegalArgumentException e) {
            throw new ClientErrorException(Response.Status.CONFLICT);
        }
        return Response.created(uriInfo.getAbsolutePath()).build();
    }

    /**
     * Updates only the fields present in the request; the permit stays in its district.
     */
    @PATCH
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void patchPermit(@PathParam("districtId") UUID districtId, @PathParam("id") UUID id,
                            @Valid @NotNull PatchPermitRequest request) {
        Permit permit = permitService.find(districtId, id).orElseThrow(NotFoundException::new);
        request.applyTo(permit);
        try {
            permitService.update(permit);
        } catch (NoSuchElementException e) {
            throw new NotFoundException();
        }
    }

    @DELETE
    @Path("/{id}")
    public void deletePermit(@PathParam("districtId") UUID districtId, @PathParam("id") UUID id) {
        permitService.find(districtId, id).orElseThrow(NotFoundException::new);
        try {
            permitService.delete(id);
        } catch (NoSuchElementException e) {
            throw new NotFoundException();
        }
    }

}
