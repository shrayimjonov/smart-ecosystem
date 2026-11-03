package uz.uzkassa.smartpossupply.dto.company;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.enums.DeliveryType;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 07.09.2023
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class DeliveryTypeListDTO {

    DeliveryType deliveryType;

    Long hours;

    public DeliveryTypeListDTO(DeliveryType deliveryType, Long hours) {
        this.deliveryType = deliveryType;
        this.hours = hours;
    }
}
