package uz.uzkassa.smartpossupply.dto.order;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.serial.ProductSerialLookUpDTO;
import uz.uzkassa.smartpossupply.dto.unit.UnitDTO;
import uz.uzkassa.smartpossupply.enums.ProductType;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderProductDetailForTransferDTO extends OrderProductBaseDTO {

    Long orderProductId;

    Long id;

    String name;

    String barcode;

    String vatBarcode;

    UnitDTO unit;

    BigDecimal qtyInPackage;

    String capacity;

    boolean hasMark;

    ProductType productType;

    BigDecimal brutto;

    BigDecimal netto;

    ProductSerialLookUpDTO productSerial;

    CommonDTO productContract;

    CommonDTO warehouse;

    String packageCode;

    String packageName;

    BigDecimal totalBrutto;

    BigDecimal totalNetto;
}
