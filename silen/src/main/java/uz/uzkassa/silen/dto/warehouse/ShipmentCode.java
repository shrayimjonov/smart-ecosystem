package uz.uzkassa.silen.dto.warehouse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.uzkassa.silen.enumeration.PackageType;

import java.io.Serializable;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentCode implements Serializable {
    private PackageType unit;
    private String unitName;
    private Integer capacity;
    private Integer count;

    public Integer getCount() {
        return count == null ? 0 : count;
    }

    public String getUnitName() {
        return unit != null ? unit.getNameRu() : null;
    }
}
