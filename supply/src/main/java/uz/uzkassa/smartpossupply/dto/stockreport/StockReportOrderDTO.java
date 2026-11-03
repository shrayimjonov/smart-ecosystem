package uz.uzkassa.smartpossupply.dto.stockreport;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StockReportOrderDTO {
    Long id;
    String orderNumber;
    CommonDTO agent;
    LocalDateTime orderDate;
}
