package pl.edu.pg.eti.kask.pzw.permit.dto;

import pl.edu.pg.eti.kask.pzw.permit.entity.Permit;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record GetPermitsResponse(List<Item> permits) {

    public record Item(UUID id, LocalDate validFrom) {
    }

    public static GetPermitsResponse from(List<Permit> permits) {
        return new GetPermitsResponse(permits.stream()
                .map(permit -> new Item(permit.getId(), permit.getValidFrom()))
                .toList());
    }

}
