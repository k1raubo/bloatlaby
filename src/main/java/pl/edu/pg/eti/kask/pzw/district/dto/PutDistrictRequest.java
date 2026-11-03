package pl.edu.pg.eti.kask.pzw.district.dto;

import pl.edu.pg.eti.kask.pzw.district.entity.District;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * New district. The identifier comes from the resource address.
 */
public record PutDistrictRequest(String name, BigDecimal annualFee, Integer waterCount) {

    public boolean isValid() {
        return name != null && !name.isBlank();
    }

    public District toEntity(UUID id) {
        District district = new District();
        district.setId(id);
        district.setName(name.strip());
        district.setAnnualFee(annualFee);
        district.setWaterCount(waterCount);
        return district;
    }

}
