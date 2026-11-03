package pl.edu.pg.eti.kask.pzw.district.dto;

import pl.edu.pg.eti.kask.pzw.district.entity.District;

import java.util.List;
import java.util.UUID;

public record GetDistrictsResponse(List<Item> districts) {

    public record Item(UUID id, String name) {
    }

    public static GetDistrictsResponse from(List<District> districts) {
        return new GetDistrictsResponse(districts.stream()
                .map(district -> new Item(district.getId(), district.getName()))
                .toList());
    }

}
