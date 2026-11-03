package uz.uzkassa.cts.dto.bonus;

import lombok.*;
import uz.uzkassa.cts.helper.excelhelper.annotaion.XlsxSheet;
import uz.uzkassa.cts.helper.excelhelper.annotaion.XlsxSingleField;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@XlsxSheet(value = "Сводка")
@Builder
public class BonusSummaryExcelExportDTO {
    @XlsxSingleField(columnIndex = 0, columnName = "Регион")
    String region;
    @XlsxSingleField(columnIndex = 1, columnName = "Сотрудник")
    String employee;
    @XlsxSingleField(columnIndex = 2, columnName = "Компания сотрудника")
    String company;
    @XlsxSingleField(columnIndex = 3, columnName = "Количество услуг")
    Integer qtyServices;
    @XlsxSingleField(columnIndex = 4, columnName = "Общая сумма")
    BigDecimal totalAmount;
    @XlsxSingleField(columnIndex = 5, columnName = "Общая сумма бонуса")
    BigDecimal totalBonusAmount;
}
