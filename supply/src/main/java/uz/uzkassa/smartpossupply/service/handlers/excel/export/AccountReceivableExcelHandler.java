package uz.uzkassa.smartpossupply.service.handlers.excel.export;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.customer.AccountReceivableReportDTO;
import uz.uzkassa.smartpossupply.filters.StockFilter;
import uz.uzkassa.smartpossupply.service.FinancialOperationService;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 06.12.2022 18:57
 */
@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AccountReceivableExcelHandler extends AbstractExcelHandler<StockFilter> {

    FinancialOperationService financialOperationService;

    @Override
    public String getFileName(StockFilter filter) {
        return "AccountReceivable.xlsx";
    }

    @Override
    protected LinkedList<String> getHeaders() {
        LinkedList<String> header = new LinkedList<>();
        header.add("Клиент");
        header.add("ИНН");
        header.add("Маршрут");
        header.add("Агент");
        header.add("Экспедитор");
        header.add("Номер отгрузки");
        header.add("Дата отгрузки");
        header.add("№ ЭСФ");
        header.add("Дата ЭСФ");
        header.add("Сумма задолженности");
        header.add("Сумма предоплаты");
        header.add("Итого");
        return header;
    }

    @Override
    protected XSSFWorkbook getXSSFWorkbook(StockFilter filter) {
        XSSFWorkbook workbook = createWorkbook();
        XSSFSheet sheet = createSheet(workbook, getFileName(filter));
        createHeader(getHeaders(), sheet);
        List<AccountReceivableReportDTO> accountReceivableReportList = financialOperationService.getAccountReceivableReportToList(filter);
        int rowIndex = 0;
        for (AccountReceivableReportDTO accountReceivableReportDTO : accountReceivableReportList) {
            Row row = sheet.createRow(++rowIndex);
            row.createCell(0).setCellValue(StringUtils.isNotEmpty(accountReceivableReportDTO.getCustomer().getName()) ? accountReceivableReportDTO.getCustomer().getName() : "");
            row.createCell(1).setCellValue(StringUtils.isNotEmpty(accountReceivableReportDTO.getCustomer().getTin()) ? accountReceivableReportDTO.getCustomer().getTin() : "");
            if (accountReceivableReportDTO.getPlanRoute() != null) {
                row.createCell(2).setCellValue(StringUtils.isNotEmpty(accountReceivableReportDTO.getPlanRoute().getName()) ? accountReceivableReportDTO.getPlanRoute().getName() : "");
            }
            if (accountReceivableReportDTO.getAgent() != null) {
                row.createCell(3).setCellValue(StringUtils.isNotEmpty(accountReceivableReportDTO.getAgent().getName()) ? accountReceivableReportDTO.getAgent().getName() : "");
            }
            if (accountReceivableReportDTO.getExpeditor() != null) {
                row.createCell(4).setCellValue(StringUtils.isNotEmpty(accountReceivableReportDTO.getExpeditor().getName()) ? accountReceivableReportDTO.getExpeditor().getName() : "");
            }
            if (accountReceivableReportDTO.getStockOut() != null) {
                row.createCell(5).setCellValue(StringUtils.isNotEmpty(accountReceivableReportDTO.getStockOut().getName()) ? accountReceivableReportDTO.getStockOut().getName() : "");
                row.createCell(6).setCellValue(StringUtils.isNotEmpty(accountReceivableReportDTO.getOperationDate().toString()) ? accountReceivableReportDTO.getStockOut().getName() : "");
            }
            if (accountReceivableReportDTO.getFactura() != null) {
                row.createCell(7).setCellValue(StringUtils.isNotEmpty(accountReceivableReportDTO.getFactura().getFacturaNo()) ? accountReceivableReportDTO.getFactura().getFacturaNo() : "");
                row.createCell(8).setCellValue(accountReceivableReportDTO.getFactura().getFacturaDate() != null ? accountReceivableReportDTO.getFactura().getFacturaDate().toString() : "");
            }
            row.createCell(9).setCellValue(accountReceivableReportDTO.getTotalCredit() != null ? accountReceivableReportDTO.getTotalCredit().toString() : "");
            row.createCell(10).setCellValue(accountReceivableReportDTO.getTotalDebit() != null ? accountReceivableReportDTO.getTotalDebit().toString() : "");
            row.createCell(11).setCellValue(Optional.ofNullable(accountReceivableReportDTO.getTotalDebit()).orElse(BigDecimal.ZERO).subtract(Optional.ofNullable(accountReceivableReportDTO.getTotalCredit()).orElse(BigDecimal.ZERO)).toString());
        }
        return workbook;
    }
}
