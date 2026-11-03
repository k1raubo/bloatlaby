package pl.edu.pg.eti.kask.pzw.district.model;

import pl.edu.pg.eti.kask.pzw.district.entity.District;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * District fields which can be set by the user in the create form.
 */
public class DistrictCreateModel implements Serializable {

    private String name;

    private BigDecimal annualFee;

    private Integer waterCount;

    /**
     * @param id identifier of the created district
     */
    public District toEntity(UUID id) {
        District district = new District();
        district.setId(id);
        district.setName(name.strip());
        district.setAnnualFee(annualFee);
        district.setWaterCount(waterCount);
        return district;
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

}
