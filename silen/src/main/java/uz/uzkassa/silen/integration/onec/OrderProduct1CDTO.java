package uz.uzkassa.silen.integration.onec;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
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
    BigDecimal deliverySumWithVat; //totalVatPrice
    LocalDate expiryDate;

    boolean hasMark;
}
