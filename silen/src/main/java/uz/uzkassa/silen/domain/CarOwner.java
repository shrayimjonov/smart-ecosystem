package uz.uzkassa.silen.domain;

import jakarta.persistence.Embeddable;
import lombok.*;
import uz.uzkassa.silen.dto.waybill.OtherCarOwner;

@Embeddable
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CarOwner {
    String tinOrPinfl;
    String name;

    public OtherCarOwner toDTO() {
        return new OtherCarOwner(getTinOrPinfl(), getName());
    }
}
