package uz.uzkassa.smartpossupply.dto.order;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.reconciliationact.PackageQuantityDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderProductDTO extends OrderProductBaseDTO {

    Long orderProductId;

    Long id;

    Long unitId;

    Long warehouseId;

    BigDecimal markupPercent;
    BigDecimal markupPrice;

    LocalDate expiryDate;
    String code1C;

    Long productSerialId;
    String serialNumber;

    Long productContractId;

    BigDecimal basePrice;

    Long companyCommittentId;

    String committentName;
    String committentTin;
    String committentVatRegCode;
    Integer committentVatRegStatus;

    Long lgotaId;
    BigDecimal lgotaVatSum;

    BigDecimal totalBrutto;
    BigDecimal totalNetto;

    Long discountId;

    Long discountItemProductId;

    BigDecimal discountPercent;

    BigDecimal discountPrice;

    BigDecimal totalDiscountPrice;

    String discountProductUniqueId;

    String parentDiscountProductUniqueId;

    List<PackageQuantityDTO> packageQuantities;
}
