package pl.edu.pg.eti.kask.pzw.district.view;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import pl.edu.pg.eti.kask.pzw.district.model.DistrictModel;
import pl.edu.pg.eti.kask.pzw.district.service.DistrictService;
import pl.edu.pg.eti.kask.pzw.permit.model.PermitModel;
import pl.edu.pg.eti.kask.pzw.permit.service.PermitService;
import pl.edu.pg.eti.kask.pzw.view.ViewResponses;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Backing bean of the district details view. View scoped, so the loaded district survives postbacks
 * (e.g. deleting a permit).
 */
@Named
@ViewScoped
public class DistrictView implements Serializable {

    private final DistrictService districtService;

    private final PermitService permitService;

    /**
     * District identifier set from the {@code id} view parameter.
     */
    private String id;

    private DistrictModel district;

    private List<PermitModel> permits;

    /**
     * Required by CDI to create a client proxy.
     */
    protected DistrictView() {
        this(null, null);
    }

    @Inject
    public DistrictView(DistrictService districtService, PermitService permitService) {
        this.districtService = districtService;
        this.permitService = permitService;
    }

    /**
     * Loads the district and, with a separate query, its permits. Responds with 404 if the district does not exist.
     */
    public void init() {
        Optional<UUID> districtId = ViewResponses.parseId(id);
        Optional<DistrictModel> found = districtId.flatMap(districtService::find).map(DistrictModel::from);
        if (found.isEmpty()) {
            ViewResponses.notFound();
            return;
        }
        district = found.get();
        permits = permitService.findAllByDistrict(district.getId()).stream().map(PermitModel::from).toList();
    }

    /**
     * @return redirect to this view with refreshed permits (POST-redirect-GET)
     */
    public String deletePermit(PermitModel permit) {
        permitService.delete(permit.getId());
        return "/district/district_view.xhtml?faces-redirect=true&id=" + district.getId();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public DistrictModel getDistrict() {
        return district;
    }

    public List<PermitModel> getPermits() {
        return permits;
    }

}
