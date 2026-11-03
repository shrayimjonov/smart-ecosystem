package uz.uzkassa.smartpossupply.dto.product;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.enums.Vat;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductReportListDTO extends CommonDTO {

    String barcode;

    String vatBarcode;

    CommonDTO branch;

    CommonDTO warehouse;

    BigDecimal basePrice;

    BigDecimal vatPrice;

    BigDecimal price;

    BigDecimal qty;

    CommonDTO entity;

    public ProductReportListDTO(Long id, String name, String barcode, String vatBarcode,
                                Long branchId, String branchName, Long warehouseId,
                                String warehouseName, BigDecimal basePrice, BigDecimal qty,
                                String vat, Long entityId, String entityNumber
    ) {
        this.id = id;
        this.name = name;
        this.barcode = barcode;
        this.vatBarcode = vatBarcode;
        this.basePrice = Optional.ofNullable(basePrice).orElse(BigDecimal.ZERO);
        this.qty = qty;
        this.branch = branchId != null ? new CommonDTO(branchId, branchName) : null;
        this.warehouse = warehouseId != null ? new CommonDTO(warehouseId, warehouseName) : null;
        this.vatPrice = vat != null ? Optional.ofNullable(Vat.valueOf(vat).getAmount()).orElse(BigDecimal.ZERO).multiply(this.basePrice).divide(BigDecimal.valueOf(100L), RoundingMode.CEILING) : BigDecimal.ZERO;
        this.price = this.basePrice.add(this.vatPrice);
        this.entity = entityId != null ? new CommonDTO(entityId, entityNumber) : null;
    }
}
