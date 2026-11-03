package uz.uzkassa.smartpossupply.service.handlers.excel.export;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.planroute.PlanRouteReportDTO;
import uz.uzkassa.smartpossupply.filters.PlanRouteFilter;
import uz.uzkassa.smartpossupply.service.PlanRouteService;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class SalesReportByAgentExcelHandler extends AbstractExcelHandler<PlanRouteFilter> {

    PlanRouteService planRouteService;

    @Override
    public String getFileName(PlanRouteFilter filter) {
        return "SalesReport.xlsx";
    }

    @Override
    protected LinkedList<String> getHeaders() {
        LinkedList<String> header = new LinkedList<>();
        header.add("Торговый агент");
        header.add("Клиент название + ИНН");
        header.add("Т.Т. Клиента");
        header.add("Стоимость без НДС");
        header.add("НДС сумма");
        header.add("Стоимость с НДС");
        return header;
    }

    @Override
    protected XSSFWorkbook getXSSFWorkbook(PlanRouteFilter filter) {
        XSSFWorkbook workbook = createWorkbook();
        XSSFSheet sheet = createSheet(workbook, getFileName(filter));
        createHeader(getHeaders(), sheet);
        List<PlanRouteReportDTO> productReport = planRouteService.reportList(filter);
        int rowIndex = 0;
        for (PlanRouteReportDTO productReportListDTO : productReport) {
            Row row = sheet.createRow(++rowIndex);
            row.createCell(0).setCellValue(productReportListDTO.getAgent().getName());
            row.createCell(1).setCellValue(productReportListDTO.getCustomer().getName().concat(" ".concat(productReportListDTO.getCustomer().getTin())));
            row.createCell(2).setCellValue(Optional.ofNullable(productReportListDTO.getBranch() != null ? productReportListDTO.getBranch().getName() : null).orElse(""));
            row.createCell(3).setCellValue(Optional.ofNullable(productReportListDTO.getTotalPriceWithoutVat()).orElse(BigDecimal.ZERO).toString());
            row.createCell(4).setCellValue(Optional.ofNullable(productReportListDTO.getTotalVatPrice()).orElse(BigDecimal.ZERO).toString());
            row.createCell(5).setCellValue(Optional.ofNullable(productReportListDTO.getTotalPrice()).orElse(BigDecimal.ZERO).toString());
        }
        return workbook;
    }
}
