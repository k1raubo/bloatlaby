package pl.edu.pg.eti.kask.pzw.permit.dto;

import pl.edu.pg.eti.kask.pzw.permit.entity.Permit;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * @param owner owner of the permit, {@code null} if it has none
 */
public record GetPermitResponse(UUID id, LocalDate validFrom, BigDecimal price, boolean boatFishing,
                                District district, Owner owner) {

    public record District(UUID id, String name) {
    }

    public record Owner(UUID id, String login) {
    }

    public static GetPermitResponse from(Permit permit) {
        return new GetPermitResponse(permit.getId(), permit.getValidFrom(), permit.getPrice(), permit.isBoatFishing(),
                new District(permit.getDistrict().getId(), permit.getDistrict().getName()),
                permit.getUser() == null ? null : new Owner(permit.getUser().getId(), permit.getUser().getLogin()));
    }

}
