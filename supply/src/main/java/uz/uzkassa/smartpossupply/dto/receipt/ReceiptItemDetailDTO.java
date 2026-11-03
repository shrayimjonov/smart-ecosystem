package uz.uzkassa.smartpossupply.dto.receipt;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 20.12.2022 16:02
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReceiptItemDetailDTO extends ReceiptItemBaseDTO {

    CommonDTO product;

    CommonDTO unit;
}
