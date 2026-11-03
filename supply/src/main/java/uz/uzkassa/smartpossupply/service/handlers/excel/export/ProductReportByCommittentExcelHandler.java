package uz.uzkassa.smartpossupply.service.handlers.excel.export;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.report.CompanyCommittentReportDTO;
import uz.uzkassa.smartpossupply.filters.BalanceReportFilter;
import uz.uzkassa.smartpossupply.service.CompanyCommittentService;

import java.util.LinkedList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ProductReportByCommittentExcelHandler extends AbstractExcelHandler<BalanceReportFilter> {

    CompanyCommittentService companyCommittentService;

    @Override
    public String getFileName(BalanceReportFilter filter) {
        return "ProductReportByCommittent.xlsx";
    }

    @Override
    protected LinkedList<String> getHeaders() {
        LinkedList<String> header = new LinkedList<>();
        header.add("Название товара");
        header.add("Штрихкод товара");
        header.add("ИКПУ");
        header.add("Комитент");
        header.add("Филиал");
        header.add("Склад");
        header.add("Кол-во");
        header.add("Цена");
        header.add("Сумма");
        return header;
    }

    @Override
    protected XSSFWorkbook getXSSFWorkbook(BalanceReportFilter filter) {
        XSSFWorkbook workbook = createWorkbook();
        XSSFSheet sheet = createSheet(workbook, getFileName(filter));
        createHeader(getHeaders(), sheet);
        List<CompanyCommittentReportDTO> productReport = companyCommittentService.getReport(filter).getContent();
        int rowIndex = 0;
        for (CompanyCommittentReportDTO productReportListDTO : productReport) {
            Row row = sheet.createRow(++rowIndex);
            row.createCell(0).setCellValue(productReportListDTO.getProduct().getName());
            row.createCell(1).setCellValue(productReportListDTO.getProduct().getBarcode());
            row.createCell(2).setCellValue(productReportListDTO.getProduct().getVatBarcode());
            row.createCell(3).setCellValue(productReportListDTO.getCommittent().getName().concat(" ").concat(productReportListDTO.getCommittent().getTin()));
            row.createCell(4).setCellValue(productReportListDTO.getBranch().getName());
            row.createCell(5).setCellValue(productReportListDTO.getWarehouse().getName());
            row.createCell(6).setCellValue(productReportListDTO.getQty().toString());
            row.createCell(7).setCellValue(productReportListDTO.getPrice().toString());
            row.createCell(8).setCellValue(productReportListDTO.getTotalPrice().toString());
        }
        return workbook;
    }
}
