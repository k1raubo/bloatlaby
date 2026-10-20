package pl.edu.pg.eti.kask.pzw.permit.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import pl.edu.pg.eti.kask.pzw.district.entity.District;
import pl.edu.pg.eti.kask.pzw.user.entity.User;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "permits")
public class Permit {

    @Id
    private UUID id;

    @Column(name = "valid_from", nullable = false)
    private LocalDate validFrom;

    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "boat_fishing")
    private boolean boatFishing;

    @ManyToOne
    @JoinColumn(name = "district", nullable = false)
    private District district;

    /**
     * Relationship with the user is not mapped yet.
     */
    @Transient
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
