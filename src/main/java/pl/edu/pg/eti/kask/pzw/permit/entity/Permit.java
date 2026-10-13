package pl.edu.pg.eti.kask.pzw.permit.entity;

import pl.edu.pg.eti.kask.pzw.district.entity.District;
import pl.edu.pg.eti.kask.pzw.user.entity.User;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class Permit {

    private UUID id;

    private LocalDate validFrom;

    private BigDecimal price;

    private boolean boatFishing;

    private District district;

    private User user;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public District getDistrict() {
        return district;
    }

    public void setDistrict(District district) {
        this.district = district;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
