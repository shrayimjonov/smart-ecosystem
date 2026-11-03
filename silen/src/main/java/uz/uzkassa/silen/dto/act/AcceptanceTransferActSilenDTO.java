package uz.uzkassa.silen.dto.act;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AcceptanceTransferActSilenDTO {

    String actNumber;

    LocalDate actDate;

    List<AcceptanceTransferActProductSilenDTO> products;

    String description;

    Long customerId;

    Long storeId;

    String contractId;

    BigDecimal comission;

    BigDecimal totalCost;

    BigDecimal totalDiscount;

    BigDecimal totalPayment;

    Long supplierBranchId;
}
