package pl.edu.pg.eti.kask.pzw.permit.dto;

import pl.edu.pg.eti.kask.pzw.permit.entity.Permit;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record GetPermitResponse(UUID id, LocalDate validFrom, BigDecimal price, boolean boatFishing,
                                District district) {

    public record District(UUID id, String name) {
    }

    public static GetPermitResponse from(Permit permit) {
        return new GetPermitResponse(permit.getId(), permit.getValidFrom(), permit.getPrice(), permit.isBoatFishing(),
                new District(permit.getDistrict().getId(), permit.getDistrict().getName()));
    }

}
