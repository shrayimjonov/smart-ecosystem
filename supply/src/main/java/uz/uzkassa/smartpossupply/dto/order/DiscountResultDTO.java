package uz.uzkassa.smartpossupply.dto.order;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.discount.DiscountListDTO;
import uz.uzkassa.smartpossupply.dto.product.ProductCommonDTO;
import uz.uzkassa.smartpossupply.dto.unit.UnitDTO;
import uz.uzkassa.smartpossupply.enums.DiscountValueType;
import uz.uzkassa.smartpossupply.enums.ProductType;
import uz.uzkassa.smartpossupply.enums.Vat;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DiscountResultDTO {

    ProductCommonDTO product;

    UnitDTO unit;

    Vat vat;

    BigDecimal totalBrutto;
    BigDecimal totalNetto;

    BigDecimal brutto;
    BigDecimal netto;

    DiscountListDTO discount;

    CommonDTO discountItemProduct;

    BigDecimal discountPercent;

    BigDecimal discountPrice;

    BigDecimal totalDiscountPrice;

    BigDecimal qty;

    BigDecimal qtyInWarehouse;

    BigDecimal reservationQty;

    BigDecimal catalogPrice;

    BigDecimal priceWithoutVat;

    BigDecimal totalPrice;

    BigDecimal totalPriceWithoutVat;

    BigDecimal vatRate;

    BigDecimal vatPrice;

    BigDecimal totalVatPrice;

    BigDecimal exciseRate;

    BigDecimal exciseSum;

    DiscountValueType valueType;

    BigDecimal limit;

    String boxBarcode;

    String blockBarcode;


    String capacity;

    boolean hasMark;

    ProductType productType;

    BigDecimal qtyInPackage;

    BigDecimal qtyInBox;

    BigDecimal qtyInPallet;

    CommonDTO warehouse;

    String packageName;

    String packageCode;

    BigDecimal qtyInOrders;
}