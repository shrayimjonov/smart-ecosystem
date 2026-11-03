package uz.uzkassa.smartpossupply.service.handlers.excel.export;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.financialoperation.FinancialOperationListDTO;
import uz.uzkassa.smartpossupply.filters.FinancialOperationFilter;
import uz.uzkassa.smartpossupply.service.FinancialOperationService;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class FinancialOperationListExcelHandler extends AbstractExcelHandler<FinancialOperationFilter> {

    FinancialOperationService financialOperationService;

    @Override
    public String getFileName(FinancialOperationFilter filter) {
        return "FinancialOperations.xlsx";
    }

    @Override
    protected LinkedList<String> getHeaders() {
        LinkedList<String> header = new LinkedList<>();
        header.add("Фин. операция");
        header.add("Клиент");
        header.add("Сумма");
        header.add("Отгрузка");
        header.add("№ документа");
        header.add("Способ оплаты");
        header.add("Дата создания");
        header.add("Комментарий");
        return header;
    }

    @Override
    protected XSSFWorkbook getXSSFWorkbook(FinancialOperationFilter filter) {
        XSSFWorkbook workbook = createWorkbook();
        XSSFSheet sheet = createSheet(workbook, getFileName(filter));
        createHeader(getHeaders(), sheet);
        List<FinancialOperationListDTO> finOperations = financialOperationService.getList(filter).getContent();
        int rowIndex = 0;
        for (FinancialOperationListDTO financialOperationListDTO : finOperations) {
            Row row = sheet.createRow(++rowIndex);
            row.createCell(0).setCellValue(financialOperationListDTO.getOperationNumber());
            row.createCell(1).setCellValue(Optional.ofNullable(financialOperationListDTO.getCustomer().getName()).orElse("").concat(" ").concat(Optional.ofNullable(financialOperationListDTO.getCustomer().getTin()).orElse("")));
            row.createCell(2).setCellValue(financialOperationListDTO.getAmount().toString());
            row.createCell(3).setCellValue(financialOperationListDTO.getStockOut() != null ? financialOperationListDTO.getStockOut().getName() : "");
            row.createCell(4).setCellValue(financialOperationListDTO.getFactura() != null ? financialOperationListDTO.getFactura().getName() : "");
            row.createCell(5).setCellValue(financialOperationListDTO.getPaymentType() != null ? financialOperationListDTO.getPaymentType().getName() : "");
            row.createCell(6).setCellValue(financialOperationListDTO.getCreatedDate() != null ? financialOperationListDTO.getCreatedDate().toString() : "");
            row.createCell(7).setCellValue(StringUtils.isNotEmpty(financialOperationListDTO.getDescription()) ? financialOperationListDTO.getDescription() : "");

        }
        return workbook;
    }
}
