package uz.uzkassa.cts.handlers.export.excel;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.domain.SaleInvoice;
import uz.uzkassa.cts.enums.ContractStatus;
import uz.uzkassa.cts.enums.XfileStatus;
import uz.uzkassa.cts.filter.TradeInInvoiceFilter;
import uz.uzkassa.cts.repository.SaleInvoiceRepository;
import uz.uzkassa.cts.utils.DateUtil;

import java.util.List;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class SaleInvoiceExportExcelHandler extends AbstractExportExcelHandler<TradeInInvoiceFilter> {

    SaleInvoiceRepository saleInvoiceRepository;

    @Override
    public String getFileName(TradeInInvoiceFilter filter) {
        return "Sale_Invoices.xlsx";
    }

    @Override
    protected List<String> getHeaders() {
        return List.of("ИНН", "Клиент", "Договор", "Дата договора", "Счет-фактура", "Дата счет-фактуры", "Сумма", "Статус", "Статус x-file");
    }

    @Override
    protected XSSFWorkbook getXSSFWorkbook(TradeInInvoiceFilter filter) {
        XSSFWorkbook workbook = createWorkbook();
        XSSFSheet sheet = createSheet(workbook, getFileName(filter));
        createHeader(getHeaders(), sheet);
        filter.setPage(0);
        filter.setSize(EXCEL_EXPORT_MAX_LIMIT);
        List<SaleInvoice> result = saleInvoiceRepository.getResultList(filter).getList();
        int rowIndex = 0;
        for (SaleInvoice saleInvoice : result) {
            Row row = sheet.createRow(++rowIndex);
            int cellIndex = 0;
            row.createCell(cellIndex++).setCellValue(saleInvoice.getSaleContract().getCustomer().getTin());
            row.createCell(cellIndex++).setCellValue(saleInvoice.getSaleContract().getCustomer().getName());
            row.createCell(cellIndex++).setCellValue(saleInvoice.getSaleContract().getNumber());
            row.createCell(cellIndex++).setCellValue(DateUtil.getLocalDateTimeAsStringWithoutSecond(saleInvoice.getSaleContract().getCreatedDate()));
            row.createCell(cellIndex++).setCellValue(saleInvoice.getNumber());
            row.createCell(cellIndex++).setCellValue(DateUtil.getLocalDateTimeAsStringWithoutSecond(saleInvoice.getInvoiceDate()));
            row.createCell(cellIndex++).setCellValue(Optional.ofNullable(saleInvoice.getTotalSellPrice()).map(Object::toString).orElse(""));
            row.createCell(cellIndex++).setCellValue(Optional.ofNullable(saleInvoice.getStatus()).map(ContractStatus::getName).orElse(""));
            row.createCell(cellIndex).setCellValue(Optional.ofNullable(saleInvoice.getXfileStatus()).map(XfileStatus::getName).orElse(""));
        }
        return workbook;
    }
}
