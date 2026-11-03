package uz.uzkassa.smartpossupply.dto.receipt;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.company.CompanyBaseDTO;
import uz.uzkassa.smartpossupply.enums.ReceiptStatus;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 23.12.2022 11:46
 **/
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReceiptDetailDTO extends ReceiptBaseDTO {

    Long id;

    String uid;

    CompanyBaseDTO company;

    CommonDTO branch;

    CommonDTO cashier;

    CommonDTO shift;

    LocalDateTime createdDate;

    ReceiptStatus status;

    Set<ReceiptItemDetailDTO> items = new HashSet<>();

    @JsonIgnore
    public void addItem(ReceiptItemDetailDTO receiptItem) {
        items.add(receiptItem);
    }
}
