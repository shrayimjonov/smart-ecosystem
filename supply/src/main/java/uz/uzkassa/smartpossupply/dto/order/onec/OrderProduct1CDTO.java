package uz.uzkassa.smartpossupply.dto.order.onec;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderProduct1CDTO implements Serializable {

    String committentName;
    String committentTin;
    String committentVatRegCode;
    Integer committentVatRegStatus;

    Integer lgotaId;
    String lgotaName;
    BigDecimal lgotaVatSum;
    Integer lgotaType;

    String catalogCode;
    String catalogName;
    String barcode;

    String name;
    String serial;

    String packageCode;
    String packageName;

    BigDecimal baseSumma; //basePrice
    BigDecimal profitRate; //markupPercent
    BigDecimal count; //qty
    BigDecimal summa; //price with vat for 1PC
    BigDecimal deliverySum; //totalPrice
    BigDecimal vatRate;
    BigDecimal vatSum; //vatPrice
    BigDecimal exciseRate;
    BigDecimal exciseSum;
    BigDecimal deliverySumWithVat; //totalVatPrice
    LocalDate expiryDate;
    String code1C;
    Long warehouseId;
    String origin;

    boolean hasMark;

    public String getBarcode() {
        return barcode != null ? barcode.trim() : null;
    }

    public String getCatalogCode() {
        return catalogCode != null ? catalogCode.trim() : null;
    }
}
