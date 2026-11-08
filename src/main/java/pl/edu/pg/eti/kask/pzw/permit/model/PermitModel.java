package pl.edu.pg.eti.kask.pzw.permit.model;

import pl.edu.pg.eti.kask.pzw.district.model.DistrictModel;
import pl.edu.pg.eti.kask.pzw.permit.entity.Permit;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Permit data presented in views.
 */
public class PermitModel implements Serializable {

    private final UUID id;

    private final LocalDate validFrom;

    private final BigDecimal price;

    private final boolean boatFishing;

    private final DistrictModel district;

    /**
     * Login of the permit's owner, {@code null} if the permit has no owner.
     */
    private final String owner;

    public PermitModel(UUID id, LocalDate validFrom, BigDecimal price, boolean boatFishing, DistrictModel district,
                       String owner) {
        this.id = id;
        this.validFrom = validFrom;
        this.price = price;
        this.boatFishing = boatFishing;
        this.district = district;
        this.owner = owner;
    }

    public static PermitModel from(Permit permit) {
        return new PermitModel(permit.getId(), permit.getValidFrom(), permit.getPrice(), permit.isBoatFishing(),
                DistrictModel.from(permit.getDistrict()),
                permit.getUser() == null ? null : permit.getUser().getLogin());
    }

    public UUID getId() {
        return id;
    }

    public LocalDate getValidFrom() {
        return validFrom;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public boolean isBoatFishing() {
        return boatFishing;
    }

    public DistrictModel getDistrict() {
        return district;
    }

    public String getOwner() {
        return owner;
    }

}
