package uz.uzkassa.silen.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.silen.enumeration.ShipmentStatus;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class ShipmentStatsDTO implements Serializable {
    private ShipmentStatus status;
    private Integer qty;

    public ShipmentStatsDTO(String type, Integer qty) {
        this.status = StringUtils.isNotEmpty(type) ? ShipmentStatus.valueOf(type) : null;
        this.qty = qty;
    }
}
