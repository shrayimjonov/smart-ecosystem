package uz.uzkassa.silen.dto.warehouse;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class ShipmentItemCodeV2DTO implements Serializable {
    @NotBlank(message = "Отгрузка не обозначен")
    private String shipmentId;

    @NotBlank(message = "Код не может быть пустым")
    private String code;

    private boolean trueCheck = true;
}
