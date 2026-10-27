package pl.edu.pg.eti.kask.pzw.permit.view;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import pl.edu.pg.eti.kask.pzw.district.model.DistrictModel;
import pl.edu.pg.eti.kask.pzw.district.service.DistrictService;
import pl.edu.pg.eti.kask.pzw.permit.model.PermitEditModel;
import pl.edu.pg.eti.kask.pzw.permit.service.PermitService;
import pl.edu.pg.eti.kask.pzw.view.ViewResponses;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Backing bean of the new permit form. View scoped, so the form model survives validation errors.
 */
@Named
@ViewScoped
public class PermitCreate implements Serializable {

    private final PermitService permitService;

    private final DistrictService districtService;

    /**
     * Optional identifier of the preselected district, set from the {@code district} view parameter.
     */
    private String districtId;

    private PermitEditModel permit;

    private List<DistrictModel> districts;

    /**
     * Required by CDI to create a client proxy.
     */
    protected PermitCreate() {
        this(null, null);
    }

    @Inject
    public PermitCreate(PermitService permitService, DistrictService districtService) {
        this.permitService = permitService;
        this.districtService = districtService;
    }

    /**
     * Prepares an empty form, preselecting the district passed as the view parameter. Responds with 404 if that
     * district does not exist.
     */
    public void init() {
        districts = districtService.findAll().stream().map(DistrictModel::from).toList();
        permit = new PermitEditModel();
        permit.setValidFrom(LocalDate.now());
        if (districtId != null) {
            Optional<DistrictModel> district = ViewResponses.parseId(districtId)
                    .flatMap(districtService::find)
                    .map(DistrictModel::from);
            if (district.isEmpty()) {
                ViewResponses.notFound();
                return;
            }
            permit.setDistrict(district.get());
        }
    }

    /**
     * @return redirect to the district of the new permit (POST-redirect-GET)
     */
    public String save() {
        permitService.create(permit.toEntity(UUID.randomUUID()));
        return "/district/district_view.xhtml?faces-redirect=true&id=" + permit.getDistrict().getId();
    }

    public String getDistrictId() {
        return districtId;
    }

    public void setDistrictId(String districtId) {
        this.districtId = districtId;
    }

    public PermitEditModel getPermit() {
        return permit;
    }

    public List<DistrictModel> getDistricts() {
        return districts;
    }

}
