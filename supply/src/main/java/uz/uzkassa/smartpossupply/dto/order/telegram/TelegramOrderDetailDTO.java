package uz.uzkassa.smartpossupply.dto.order.telegram;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.product.TelegramProductListDTO;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TelegramOrderDetailDTO {

    Long id;

    List<TelegramProductListDTO> products;
}
