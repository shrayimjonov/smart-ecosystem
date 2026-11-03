package uz.uzkassa.cts.dto.accessory;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonCodeDTO;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.enums.AccessoryType;
import uz.uzkassa.cts.enums.CommonStatus;

import java.math.BigDecimal;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccessoryDetailDTO extends CommonCodeDTO {

    CommonDTO model;

    AccessoryType type;

    String description;

    BigDecimal price;

    BigDecimal rentPrice;

    CommonCodeDTO catalog;

    CommonCodeDTO apackage;

    String codeForUI;

    BigDecimal bonus;

    CommonStatus status;
}
