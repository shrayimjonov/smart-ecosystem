package uz.uzkassa.smartpossupply.dto.order.telegram;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.order.OrderProductBaseDTO;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TelegramOrderProductDTO extends OrderProductBaseDTO {

    Long id;

    Long unitId;
}
