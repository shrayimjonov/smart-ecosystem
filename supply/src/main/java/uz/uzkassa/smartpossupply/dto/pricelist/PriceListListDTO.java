package uz.uzkassa.smartpossupply.dto.pricelist;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.enums.CommonStatus;

import java.time.LocalDate;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 14.02.2023 14:43
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PriceListListDTO extends CommonDTO  {

    String priceListNumber;

    CommonDTO branch;

    CommonStatus status;

    LocalDate expireDate;

    int productCount;
}
