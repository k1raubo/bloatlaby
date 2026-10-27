package pl.edu.pg.eti.kask.pzw.permit.model;

import pl.edu.pg.eti.kask.pzw.district.entity.District;
import pl.edu.pg.eti.kask.pzw.district.model.DistrictModel;
import pl.edu.pg.eti.kask.pzw.permit.entity.Permit;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Permit fields which can be set by the user in the create and edit forms.
 */
public class PermitEditModel implements Serializable {

    private LocalDate validFrom;

    private BigDecimal price;

    private boolean boatFishing;

    private DistrictModel district;

    public static PermitEditModel from(Permit permit) {
        PermitEditModel model = new PermitEditModel();
        model.setValidFrom(permit.getValidFrom());
        model.setPrice(permit.getPrice());
        model.setBoatFishing(permit.isBoatFishing());
        model.setDistrict(DistrictModel.from(permit.getDistrict()));
        return model;
    }

    /**
     * @param id identifier of the created or updated permit
     * @return permit entity referencing the selected district only by its identifier
     */
    public Permit toEntity(UUID id) {
        District reference = new District();
        reference.setId(district.getId());
        Permit permit = new Permit();
        permit.setId(id);
        permit.setValidFrom(validFrom);
        permit.setPrice(price);
        permit.setBoatFishing(boatFishing);
        permit.setDistrict(reference);
        return permit;
    }

    public LocalDate getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(LocalDate validFrom) {
        this.validFrom = validFrom;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public boolean isBoatFishing() {
        return boatFishing;
    }

    public void setBoatFishing(boolean boatFishing) {
        this.boatFishing = boatFishing;
    }

    public DistrictModel getDistrict() {
        return district;
    }

    public void setDistrict(DistrictModel district) {
        this.district = district;
    }

}
