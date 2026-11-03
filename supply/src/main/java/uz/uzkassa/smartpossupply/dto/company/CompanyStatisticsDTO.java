package uz.uzkassa.smartpossupply.dto.company;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class CompanyStatisticsDTO {

    Long totalBranch;

    Long totalWarehouse;

    Long totalOrder;

    Long totalStockIncoming;

    Long totalStockOutgoing;

    Long totalInvoice;
}
