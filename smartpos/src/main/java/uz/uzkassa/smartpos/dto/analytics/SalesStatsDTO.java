package uz.uzkassa.smartpos.dto.analytics;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class SalesStatsDTO {
    Long id;

    String name;

    String vatBarcode;
    String barcode;

    String unit;

    String category;

    BigDecimal price;

    BigDecimal salesCount;

    BigDecimal salesTotal;

    BigDecimal nds;

    BigDecimal ndsPercent;

    BigDecimal discount;

    Long receiptCount;

    String uidList;

    LocalDateTime dateTime;

    Long branchId;

    String branchName;
}
