package uz.uzkassa.smartpossupply.dto.order.telegram;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.GrandTotalDTO;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TelegramOrderDTO extends GrandTotalDTO {

    Long id;

    String description;

    List<TelegramOrderProductDTO> products = new ArrayList<>();

    Long customerId;

}
