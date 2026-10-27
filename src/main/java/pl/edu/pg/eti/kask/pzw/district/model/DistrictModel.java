package pl.edu.pg.eti.kask.pzw.district.model;

import pl.edu.pg.eti.kask.pzw.district.entity.District;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

/**
 * District data presented in views. Equality is based on the identifier, so the model can be used as a select option.
 */
public class DistrictModel implements Serializable {

    private final UUID id;

    private final String name;

    private final BigDecimal annualFee;

    private final Integer waterCount;

    public DistrictModel(UUID id, String name, BigDecimal annualFee, Integer waterCount) {
        this.id = id;
        this.name = name;
        this.annualFee = annualFee;
        this.waterCount = waterCount;
    }

    public static DistrictModel from(District district) {
        return new DistrictModel(district.getId(), district.getName(), district.getAnnualFee(),
                district.getWaterCount());
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getAnnualFee() {
        return annualFee;
    }

    public Integer getWaterCount() {
        return waterCount;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof DistrictModel other && Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

}
