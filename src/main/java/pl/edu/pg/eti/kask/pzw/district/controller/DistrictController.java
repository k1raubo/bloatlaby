package pl.edu.pg.eti.kask.pzw.district.controller;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
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
import pl.edu.pg.eti.kask.pzw.district.dto.GetDistrictResponse;
import pl.edu.pg.eti.kask.pzw.district.dto.GetDistrictsResponse;
import pl.edu.pg.eti.kask.pzw.district.dto.PatchDistrictRequest;
import pl.edu.pg.eti.kask.pzw.district.dto.PutDistrictRequest;
import pl.edu.pg.eti.kask.pzw.district.entity.District;
import pl.edu.pg.eti.kask.pzw.district.service.DistrictService;

import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * Districts (categories) as a REST resource. Responses never include permits; those are available under
 * {@code /districts/{id}/permits} (see {@link pl.edu.pg.eti.kask.pzw.permit.controller.PermitController}).
 */
@Path("/districts")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
public class DistrictController {

    private final DistrictService districtService;

    /**
     * Required by CDI to create a client proxy.
     */
    protected DistrictController() {
        this(null);
    }

    @Inject
    public DistrictController(DistrictService districtService) {
        this.districtService = districtService;
    }

    @GET
    public GetDistrictsResponse getDistricts() {
        return GetDistrictsResponse.from(districtService.findAll());
    }

    @GET
    @Path("/{id}")
    public GetDistrictResponse getDistrict(@PathParam("id") UUID id) {
        return districtService.find(id).map(GetDistrictResponse::from).orElseThrow(NotFoundException::new);
    }

    /**
     * Creates a district under the given identifier (PUT, as the client chooses the resource address).
     *
     * @return 201 with the address of the new district
     */
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response putDistrict(@PathParam("id") UUID id, PutDistrictRequest request, @Context UriInfo uriInfo) {
        if (request == null || !request.isValid()) {
            throw new BadRequestException();
        }
        try {
            districtService.create(request.toEntity(id));
        } catch (IllegalArgumentException e) {
            throw new ClientErrorException(Response.Status.CONFLICT);
        }
        return Response.created(uriInfo.getAbsolutePath()).build();
    }

    /**
     * Updates only the fields present in the request.
     */
    @PATCH
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void patchDistrict(@PathParam("id") UUID id, PatchDistrictRequest request) {
        if (request == null || !request.isValid()) {
            throw new BadRequestException();
        }
        District district = districtService.find(id).orElseThrow(NotFoundException::new);
        request.applyTo(district);
        try {
            districtService.update(district);
        } catch (NoSuchElementException e) {
            throw new NotFoundException();
        } catch (IllegalArgumentException e) {
            throw new ClientErrorException(Response.Status.CONFLICT);
        }
    }

    /**
     * Deletes the district together with all its permits.
     */
    @DELETE
    @Path("/{id}")
    public void deleteDistrict(@PathParam("id") UUID id) {
        try {
            districtService.delete(id);
        } catch (NoSuchElementException e) {
            throw new NotFoundException();
        }
    }

}
