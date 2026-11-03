package pl.edu.pg.eti.kask.pzw.district.view;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import pl.edu.pg.eti.kask.pzw.district.model.DistrictCreateModel;
import pl.edu.pg.eti.kask.pzw.district.service.DistrictService;

import java.util.UUID;

/**
 * Backing bean of the new district form. Request scoped: the form has no state to keep between requests, so each
 * submission fills a fresh model.
 */
@Named
@RequestScoped
public class DistrictCreate {

    private final DistrictService districtService;

    private final DistrictCreateModel district = new DistrictCreateModel();

    /**
     * Required by CDI to create a client proxy.
     */
    protected DistrictCreate() {
        this(null);
    }

    @Inject
    public DistrictCreate(DistrictService districtService) {
        this.districtService = districtService;
    }

    /**
     * @return redirect to the new district (POST-redirect-GET), or {@code null} to show the form again with an error
     */
    public String save() {
        UUID id = UUID.randomUUID();
        try {
            districtService.create(district.toEntity(id));
        } catch (IllegalArgumentException e) {
            FacesContext.getCurrentInstance().addMessage("district:name",
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Okręg o tej nazwie już istnieje.", null));
            return null;
        }
        return "/district/district_view.xhtml?faces-redirect=true&id=" + id;
    }

    public DistrictCreateModel getDistrict() {
        return district;
    }

}
