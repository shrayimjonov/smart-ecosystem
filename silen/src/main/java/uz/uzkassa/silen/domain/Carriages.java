package uz.uzkassa.silen.domain;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.silen.dto.waybill.TruckDTO;

@Embeddable
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Carriages {
    String regNo;
    String model;

    public TruckDTO toDTO() {
        return new TruckDTO(getRegNo(), getModel());
    }
}
