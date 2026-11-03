package pl.edu.pg.eti.kask.pzw.district.dto;

import pl.edu.pg.eti.kask.pzw.district.entity.District;

import java.math.BigDecimal;

/**
 * Partial district update: fields left out ({@code null}) keep their current values.
 */
public record PatchDistrictRequest(String name, BigDecimal annualFee, Integer waterCount) {

    public boolean isValid() {
        return name == null || !name.isBlank();
    }

    public void applyTo(District district) {
        if (name != null) {
            district.setName(name.strip());
        }
        if (annualFee != null) {
            district.setAnnualFee(annualFee);
        }
        if (waterCount != null) {
            district.setWaterCount(waterCount);
        }
    }

}
