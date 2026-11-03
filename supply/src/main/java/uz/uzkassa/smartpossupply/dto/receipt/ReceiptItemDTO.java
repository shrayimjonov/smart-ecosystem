package uz.uzkassa.smartpossupply.dto.receipt;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 20.12.2022 16:02
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReceiptItemDTO extends ReceiptItemBaseDTO {

    Long productId;

    Long unitId;

    Long categoryId;
}
