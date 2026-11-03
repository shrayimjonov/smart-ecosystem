package uz.uzkassa.smartpossupply.domain.waybill;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.waybill.WaybillTruckDTO;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode
@Embeddable
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Carriages implements Serializable {

    @Serial
    private static final long serialVersionUID = 549684354L;

    String regNo;

    String model;

    public WaybillTruckDTO toDTO() {
        return new WaybillTruckDTO(getRegNo(), getModel());
    }

    @Override
    public String toString() {
        return "Carriages{" +
                "regNo='" + regNo + '\'' +
                ", model='" + model + '\'' +
                '}';
    }
}
