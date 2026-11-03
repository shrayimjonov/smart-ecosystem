package uz.uzkassa.cts.handlers.export.excel;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.domain.OneTimeOrderInvoice;
import uz.uzkassa.cts.enums.ContractStatus;
import uz.uzkassa.cts.enums.XfileStatus;
import uz.uzkassa.cts.filter.OneTimeOrderInvoiceFilter;
import uz.uzkassa.cts.repository.OneTimeOrderInvoiceRepository;
import uz.uzkassa.cts.utils.DateUtil;

import java.util.List;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class OneTimeOrderInvoiceExportExcelHandler extends AbstractExportExcelHandler<OneTimeOrderInvoiceFilter> {

    OneTimeOrderInvoiceRepository oneTimeOrderInvoiceRepository;

    @Override
    public String getFileName(OneTimeOrderInvoiceFilter filter) {
        return "One_Time_Order_Invoices.xlsx";
    }

    @Override
    protected List<String> getHeaders() {
        return List.of("Номер", "ИНН", "Клиент", "Договор", "Дата договора", "Счет-фактура", "Дата счет-фактуры", "Сумма", "Статус", "Статус x-file");
    }

    @Override
    protected XSSFWorkbook getXSSFWorkbook(OneTimeOrderInvoiceFilter filter) {
        XSSFWorkbook workbook = createWorkbook();
        XSSFSheet sheet = createSheet(workbook, getFileName(filter));
        createHeader(getHeaders(), sheet);
        filter.setPage(0);
        filter.setSize(EXCEL_EXPORT_MAX_LIMIT);
        List<OneTimeOrderInvoice> result = oneTimeOrderInvoiceRepository.getResultList(filter).getList();
        int rowIndex = 0;
        for (OneTimeOrderInvoice oneTimeOrderInvoice : result) {
            Row row = sheet.createRow(++rowIndex);
            int cellIndex = 0;
            row.createCell(cellIndex++).setCellValue(oneTimeOrderInvoice.getId());
            row.createCell(cellIndex++).setCellValue(oneTimeOrderInvoice.getContract().getCustomer().getTin());
            row.createCell(cellIndex++).setCellValue(oneTimeOrderInvoice.getContract().getCustomer().getName());
            row.createCell(cellIndex++).setCellValue(oneTimeOrderInvoice.getContract().getNumber());
            row.createCell(cellIndex++).setCellValue(DateUtil.getLocalDateTimeAsStringWithoutSecond(oneTimeOrderInvoice.getContract().getCreatedDate()));
            row.createCell(cellIndex++).setCellValue(oneTimeOrderInvoice.getNumber());
            row.createCell(cellIndex++).setCellValue(DateUtil.getLocalDateTimeAsStringWithoutSecond(oneTimeOrderInvoice.getInvoiceDate()));
            row.createCell(cellIndex++).setCellValue(Optional.ofNullable(oneTimeOrderInvoice.getTotalSellPrice()).map(Object::toString).orElse(""));
            row.createCell(cellIndex++).setCellValue(Optional.ofNullable(oneTimeOrderInvoice.getStatus()).map(ContractStatus::getName).orElse(""));
            row.createCell(cellIndex).setCellValue(Optional.ofNullable(oneTimeOrderInvoice.getXfileStatus()).map(XfileStatus::getName).orElse(""));
        }
        return workbook;
    }
}
