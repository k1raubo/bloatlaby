package pl.edu.pg.eti.kask.pzw.district.entity;

import pl.edu.pg.eti.kask.pzw.permit.entity.Permit;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class District {

    private UUID id;

    private String name;

    private BigDecimal annualFee;

    private Integer waterCount;

    private List<Permit> permits = new ArrayList<>();

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getAnnualFee() {
        return annualFee;
    }

    public void setAnnualFee(BigDecimal annualFee) {
        this.annualFee = annualFee;
    }

    public Integer getWaterCount() {
        return waterCount;
    }

    public void setWaterCount(Integer waterCount) {
        this.waterCount = waterCount;
    }

    public List<Permit> getPermits() {
        return permits;
    }

    public void setPermits(List<Permit> permits) {
        this.permits = permits;
    }

}
