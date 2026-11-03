package uz.uzkassa.smartpossupply.dto.pricelist;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PriceListCustomerDTO {

    Long id;

    String name;

    Long groupId;
}