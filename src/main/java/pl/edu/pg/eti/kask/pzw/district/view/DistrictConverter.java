package pl.edu.pg.eti.kask.pzw.district.view;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;
import pl.edu.pg.eti.kask.pzw.district.model.DistrictModel;
import pl.edu.pg.eti.kask.pzw.district.service.DistrictService;
import pl.edu.pg.eti.kask.pzw.view.ViewResponses;

/**
 * Converts between district identifiers sent by HTML forms and district models. Registered for all components
 * bound to {@link DistrictModel} values. Managed by CDI, so the district service can be injected.
 */
@FacesConverter(forClass = DistrictModel.class, managed = true)
public class DistrictConverter implements Converter<DistrictModel> {

    private final DistrictService districtService;

    @Inject
    public DistrictConverter(DistrictService districtService) {
        this.districtService = districtService;
    }

    @Override
    public DistrictModel getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return ViewResponses.parseId(value)
                .flatMap(districtService::find)
                .map(DistrictModel::from)
                .orElse(null);
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, DistrictModel value) {
        return value == null ? "" : value.getId().toString();
    }

}
