package pl.edu.pg.eti.kask.pzw.permit.view;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import pl.edu.pg.eti.kask.pzw.district.model.DistrictModel;
import pl.edu.pg.eti.kask.pzw.district.service.DistrictService;
import pl.edu.pg.eti.kask.pzw.permit.entity.Permit;
import pl.edu.pg.eti.kask.pzw.permit.model.PermitEditModel;
import pl.edu.pg.eti.kask.pzw.permit.service.PermitService;
import pl.edu.pg.eti.kask.pzw.view.ViewResponses;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Backing bean of the permit edit form. View scoped, so the edited permit survives postbacks.
 */
@Named
@ViewScoped
public class PermitEdit implements Serializable {

    private final PermitService permitService;

    private final DistrictService districtService;

    /**
     * Permit identifier set from the {@code id} view parameter.
     */
    private String id;

    private UUID permitId;

    private PermitEditModel permit;

    private List<DistrictModel> districts;

    /**
     * Required by CDI to create a client proxy.
     */
    protected PermitEdit() {
        this(null, null);
    }

    @Inject
    public PermitEdit(PermitService permitService, DistrictService districtService) {
        this.permitService = permitService;
        this.districtService = districtService;
    }

    /**
     * Fills the form with the current permit data. Responds with 404 if the permit does not exist.
     */
    public void init() {
        Optional<Permit> found = ViewResponses.parseId(id).flatMap(permitService::find);
        if (found.isEmpty()) {
            ViewResponses.notFound();
            return;
        }
        permitId = found.get().getId();
        permit = PermitEditModel.from(found.get());
        districts = districtService.findAll().stream().map(DistrictModel::from).toList();
    }

    /**
     * @return redirect to the (possibly changed) district of the permit (POST-redirect-GET)
     */
    public String save() {
        permitService.update(permit.toEntity(permitId));
        return "/district/district_view.xhtml?faces-redirect=true&id=" + permit.getDistrict().getId();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public PermitEditModel getPermit() {
        return permit;
    }

    public List<DistrictModel> getDistricts() {
        return districts;
    }

}
