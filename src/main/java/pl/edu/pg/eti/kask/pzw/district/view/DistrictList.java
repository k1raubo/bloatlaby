package pl.edu.pg.eti.kask.pzw.district.view;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import pl.edu.pg.eti.kask.pzw.district.model.DistrictModel;
import pl.edu.pg.eti.kask.pzw.district.service.DistrictService;

import java.util.List;

/**
 * Backing bean of the district list view.
 */
@Named
@RequestScoped
public class DistrictList {

    private final DistrictService districtService;

    private List<DistrictModel> districts;

    /**
     * Required by CDI to create a client proxy.
     */
    protected DistrictList() {
        this(null);
    }

    @Inject
    public DistrictList(DistrictService districtService) {
        this.districtService = districtService;
    }

    /**
     * @return all districts, fetched once per request
     */
    public List<DistrictModel> getDistricts() {
        if (districts == null) {
            districts = districtService.findAll().stream().map(DistrictModel::from).toList();
        }
        return districts;
    }

    /**
     * Deletes the district together with its permits.
     *
     * @return redirect to the refreshed list (POST-redirect-GET)
     */
    public String delete(DistrictModel district) {
        districtService.delete(district.getId());
        return "/district/district_list.xhtml?faces-redirect=true";
    }

}
