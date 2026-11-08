package pl.edu.pg.eti.kask.pzw.district.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import pl.edu.pg.eti.kask.pzw.district.entity.District;

import java.math.BigDecimal;

/**
 * Partial district update: fields left out ({@code null}) keep their current values. Fields which are present must
 * meet the same constraints as in {@link PutDistrictRequest}.
 */
public record PatchDistrictRequest(

        @Pattern(regexp = ".*\\S.*", message = "Nazwa nie może być pusta.")
        @Size(min = 3, max = 100, message = "Nazwa musi mieć od 3 do 100 znaków.")
        String name,

        @PositiveOrZero(message = "Opłata nie może być ujemna.")
        @Digits(integer = 8, fraction = 2, message = "Opłata może mieć najwyżej 8 cyfr przed i 2 po przecinku.")
        BigDecimal annualFee,

        @PositiveOrZero(message = "Liczba łowisk nie może być ujemna.")
        Integer waterCount) {

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
