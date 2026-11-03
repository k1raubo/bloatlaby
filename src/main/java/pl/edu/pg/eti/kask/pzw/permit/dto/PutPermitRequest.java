package pl.edu.pg.eti.kask.pzw.permit.dto;

import pl.edu.pg.eti.kask.pzw.district.entity.District;
import pl.edu.pg.eti.kask.pzw.permit.entity.Permit;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * New permit. The identifier and the district come from the resource address, so they cannot be set here.
 */
public record PutPermitRequest(LocalDate validFrom, BigDecimal price, Boolean boatFishing) {

    public boolean isValid() {
        return validFrom != null;
    }

    /**
     * @param districtId identifier of the district, resolved to the persisted district by the service
     */
    public Permit toEntity(UUID id, UUID districtId) {
        District district = new District();
        district.setId(districtId);
        Permit permit = new Permit();
        permit.setId(id);
        permit.setValidFrom(validFrom);
        permit.setPrice(price);
        permit.setBoatFishing(Boolean.TRUE.equals(boatFishing));
        permit.setDistrict(district);
        return permit;
    }

}
