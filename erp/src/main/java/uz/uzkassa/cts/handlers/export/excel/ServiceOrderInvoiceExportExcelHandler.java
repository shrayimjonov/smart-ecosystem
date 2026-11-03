package uz.uzkassa.cts.handlers.export.excel;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.domain.ServiceOrderInvoice;
import uz.uzkassa.cts.enums.ContractStatus;
import uz.uzkassa.cts.enums.XfileStatus;
import uz.uzkassa.cts.filter.ServiceOrderInvoiceFilter;
import uz.uzkassa.cts.repository.ServiceOrderInvoiceRepository;
import uz.uzkassa.cts.utils.DateUtil;

import java.util.List;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ServiceOrderInvoiceExportExcelHandler extends AbstractExportExcelHandler<ServiceOrderInvoiceFilter> {

    ServiceOrderInvoiceRepository serviceOrderInvoiceRepository;

    @Override
    public String getFileName(ServiceOrderInvoiceFilter filter) {
        return "Service_Order_Invoices.xlsx";
    }

    @Override
    protected List<String> getHeaders() {
        return List.of("Номер", "ИНН", "Клиент", "Договор", "Дата договора", "Счет-фактура", "Дата счет-фактуры", "Сумма", "Статус", "Статус x-file");
    }

    @Override
    protected XSSFWorkbook getXSSFWorkbook(ServiceOrderInvoiceFilter filter) {
        XSSFWorkbook workbook = createWorkbook();
        XSSFSheet sheet = createSheet(workbook, getFileName(filter));
        createHeader(getHeaders(), sheet);
        filter.setPage(0);
        filter.setSize(EXCEL_EXPORT_MAX_LIMIT);
        List<ServiceOrderInvoice> result = serviceOrderInvoiceRepository.getResultList(filter).getList();
        int rowIndex = 0;
        for (ServiceOrderInvoice serviceOrderInvoice : result) {
            Row row = sheet.createRow(++rowIndex);
            int cellIndex = 0;
            row.createCell(cellIndex++).setCellValue(serviceOrderInvoice.getId());
            row.createCell(cellIndex++).setCellValue(serviceOrderInvoice.getContract().getCustomer().getTin());
            row.createCell(cellIndex++).setCellValue(serviceOrderInvoice.getContract().getCustomer().getName());
            row.createCell(cellIndex++).setCellValue(serviceOrderInvoice.getContract().getNumber());
            row.createCell(cellIndex++).setCellValue(DateUtil.getLocalDateTimeAsStringWithoutSecond(serviceOrderInvoice.getContract().getCreatedDate()));
            row.createCell(cellIndex++).setCellValue(serviceOrderInvoice.getNumber());
            row.createCell(cellIndex++).setCellValue(DateUtil.getLocalDateTimeAsStringWithoutSecond(serviceOrderInvoice.getInvoiceDate()));
            row.createCell(cellIndex++).setCellValue(Optional.ofNullable(serviceOrderInvoice.getTotalSellPrice()).map(Object::toString).orElse(""));
            row.createCell(cellIndex++).setCellValue(Optional.ofNullable(serviceOrderInvoice.getStatus()).map(ContractStatus::getName).orElse(""));
            row.createCell(cellIndex).setCellValue(Optional.ofNullable(serviceOrderInvoice.getXfileStatus()).map(XfileStatus::getName).orElse(""));
        }
        return workbook;
    }
}
