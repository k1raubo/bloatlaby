package pl.edu.pg.eti.kask.pzw.district.dto;

import pl.edu.pg.eti.kask.pzw.district.entity.District;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Single district without its permits.
 */
public record GetDistrictResponse(UUID id, String name, BigDecimal annualFee, Integer waterCount) {

    public static GetDistrictResponse from(District district) {
        return new GetDistrictResponse(district.getId(), district.getName(), district.getAnnualFee(),
                district.getWaterCount());
    }

}
