package uz.uzkassa.smartpossupply.dto.stock;

import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.reconciliationact.PackageQuantityDTO;
import uz.uzkassa.smartpossupply.dto.product.BlockProductDTO;
import uz.uzkassa.smartpossupply.enums.ProductType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockProductBaseDTO implements Serializable {

    Long stockProductId;

    Long id; //productId

    String name; //product name

    String vatBarCode; //IKPU

    String barcode; //Shtrix code

    BigDecimal qtyInPackage;
    BigDecimal qtyInBox;
    BigDecimal qtyInPallet;

    List<PackageQuantityDTO> packageQuantities;

    String capacity;

    boolean hasMark;

    boolean hasMarking;

    ProductType productType;

    BigDecimal qty; //soni

    BigDecimal sellingPrice; // 1 donasini narxi. QQS bilan

    BigDecimal sellingForeignPrice; // 1 donasini narxi. QQS bilan

    BigDecimal sellingPriceWithoutVat; // 1 donasini narxi QQS siz

    BigDecimal totalSellingPrice; // qty * sellingPrice // n tasini narxi QQS bilan

    BigDecimal totalSellingForeignPrice; // qty * sellingPrice // n tasini narxi QQS bilan

    BigDecimal totalSellingPriceWithoutVat; // qty * sellingPriceWithoutVat // n tasini narxi QQS siz

    BigDecimal purchasePrice; //1 donasini narxi

    BigDecimal purchaseForeignPrice; //1 donasini narxi

    BigDecimal purchasePriceWithoutVat; //1 donasini narxi QQS siz

    BigDecimal totalPurchasePrice; // qty * purchasePrice // n tasini QQS bilan

    BigDecimal totalPurchaseForeignPrice; // qty * purchasePrice // n tasini QQS bilan

    BigDecimal totalPurchasePriceWithoutVat; // qty * purchasePriceWithoutVat // n tasini QQS siz

    BigDecimal markupPrice;

    BigDecimal markupPercent;

    BigDecimal vatRate; //13% 15%

    BigDecimal vatPrice; //1 donasini QQS

    BigDecimal totalVatPrice; //qty * vatPrice // n tasini QQS

    BigDecimal basePrice;

    BigDecimal exciseRate;

    BigDecimal exciseSum;

    List<BlockProductDTO> blockProducts = new ArrayList<>();
}
