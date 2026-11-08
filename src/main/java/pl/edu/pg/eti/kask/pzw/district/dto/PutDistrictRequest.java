package pl.edu.pg.eti.kask.pzw.district.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import pl.edu.pg.eti.kask.pzw.district.entity.District;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * New district. The identifier comes from the resource address. Constraints match the create form.
 */
public record PutDistrictRequest(

        @NotBlank(message = "Podaj nazwę okręgu.")
        @Size(min = 3, max = 100, message = "Nazwa musi mieć od 3 do 100 znaków.")
        String name,

        @PositiveOrZero(message = "Opłata nie może być ujemna.")
        @Digits(integer = 8, fraction = 2, message = "Opłata może mieć najwyżej 8 cyfr przed i 2 po przecinku.")
        BigDecimal annualFee,

        @PositiveOrZero(message = "Liczba łowisk nie może być ujemna.")
        Integer waterCount) {

    public District toEntity(UUID id) {
        District district = new District();
        district.setId(id);
        district.setName(name.strip());
        district.setAnnualFee(annualFee);
        district.setWaterCount(waterCount);
        return district;
    }

}
