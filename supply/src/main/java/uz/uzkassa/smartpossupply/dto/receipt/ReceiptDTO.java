package uz.uzkassa.smartpossupply.dto.receipt;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.enums.ReceiptStatus;

import java.util.Set;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 20.12.2022 15:40
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PROTECTED)
public class ReceiptDTO extends ReceiptBaseDTO {

    Set<ReceiptItemDTO> items;

    ReceiptStatus status;
}
