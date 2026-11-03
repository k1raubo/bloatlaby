package pl.edu.pg.eti.kask.pzw.permit.dto;

import pl.edu.pg.eti.kask.pzw.permit.entity.Permit;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Partial permit update: fields left out ({@code null}) keep their current values. The district cannot be changed.
 */
public record PatchPermitRequest(LocalDate validFrom, BigDecimal price, Boolean boatFishing) {

    public void applyTo(Permit permit) {
        if (validFrom != null) {
            permit.setValidFrom(validFrom);
        }
        if (price != null) {
            permit.setPrice(price);
        }
        if (boatFishing != null) {
            permit.setBoatFishing(boatFishing);
        }
    }

}
