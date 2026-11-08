package pl.edu.pg.eti.kask.pzw.permit.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import pl.edu.pg.eti.kask.pzw.district.entity.District;
import pl.edu.pg.eti.kask.pzw.permit.entity.Permit;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * New permit. The identifier and the district come from the resource address, so they cannot be set here.
 */
public record PutPermitRequest(

        @NotNull(message = "Podaj datę początku ważności.")
        LocalDate validFrom,

        @PositiveOrZero(message = "Cena nie może być ujemna.")
        @Digits(integer = 8, fraction = 2, message = "Cena może mieć najwyżej 8 cyfr przed i 2 po przecinku.")
        BigDecimal price,

        Boolean boatFishing) {

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
