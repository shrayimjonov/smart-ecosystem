package uz.uzkassa.smartpossupply.dto.order;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SalesAndCustomerReportDTO {
    String categoryName;
    String customerName;
    String customerTin;
    String customerBranch;
    String productName;
    String barcode;
    String contractNumber;
    BigDecimal contractPrice;
    BigDecimal quantity;
    String serialNumber;
    BigDecimal basePrice;
    BigDecimal markupPercent;
    BigDecimal markupAmount;
    BigDecimal priceWithoutVat;
    BigDecimal vatPrice;
    BigDecimal priceWithVat;
    String branch;
    String warehouse;

}
