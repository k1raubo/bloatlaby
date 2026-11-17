package pl.edu.pg.eti.kask.pzw.permit.dto;

import pl.edu.pg.eti.kask.pzw.permit.entity.Permit;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record GetPermitsResponse(List<Item> permits) {

    /**
     * @param district name of the permit's district
     * @param owner    login of the permit's owner, {@code null} if it has none
     */
    public record Item(UUID id, LocalDate validFrom, String district, String owner) {
    }

    public static GetPermitsResponse from(List<Permit> permits) {
        return new GetPermitsResponse(permits.stream()
                .map(permit -> new Item(permit.getId(), permit.getValidFrom(), permit.getDistrict().getName(),
                        permit.getUser() == null ? null : permit.getUser().getLogin()))
                .toList());
    }

}
