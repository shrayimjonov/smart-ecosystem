package uz.uzkassa.cts.dto.fm;

import lombok.*;
import uz.uzkassa.cts.helper.excelhelper.annotaion.XlsxSheet;
import uz.uzkassa.cts.helper.excelhelper.annotaion.XlsxSingleField;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@XlsxSheet(value = "FMReport")
@Builder
public class FMReportUnCheckedExcelResponseDTO {
    @XlsxSingleField(columnIndex = 0, columnName = "ИНН нет в БД ")
    private String tin;
}
