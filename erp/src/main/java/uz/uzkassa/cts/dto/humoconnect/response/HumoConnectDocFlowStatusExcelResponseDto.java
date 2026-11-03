package uz.uzkassa.cts.dto.humoconnect.response;

import lombok.*;
import uz.uzkassa.cts.helper.excelhelper.annotaion.XlsxSheet;
import uz.uzkassa.cts.helper.excelhelper.annotaion.XlsxSingleField;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@XlsxSheet(value = "ControversialHumoConnects")
@Builder
public class HumoConnectDocFlowStatusExcelResponseDto {
    @XlsxSingleField(columnIndex = 0, columnName = "Кейс ID")
    private Long caseId;
    @XlsxSingleField(columnIndex = 1, columnName = "Причина")
    private String reason;
}
