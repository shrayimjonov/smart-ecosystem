package uz.uzkassa.cts.handlers.export.excel;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.domain.ActOfTransmission;
import uz.uzkassa.cts.domain.ActOfTransmissionInvoice;
import uz.uzkassa.cts.domain.ContractForLeasing;
import uz.uzkassa.cts.domain.Customer;
import uz.uzkassa.cts.enums.ContractStatus;
import uz.uzkassa.cts.enums.XfileStatus;
import uz.uzkassa.cts.filter.PaymentScheduleInvoiceFilter;
import uz.uzkassa.cts.repository.ActOfTransmissionInvoiceRepository;
import uz.uzkassa.cts.utils.DateUtil;

import java.util.List;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ActOfTransmissionInvoiceExportExcelHandler extends AbstractExportExcelHandler<PaymentScheduleInvoiceFilter> {

    ActOfTransmissionInvoiceRepository actOfTransmissionInvoiceRepository;

    @Override
    public String getFileName(PaymentScheduleInvoiceFilter filter) {
        return "Act_Of_Transmission_Invoices.xlsx";
    }

    @Override
    protected List<String> getHeaders() {
        return List.of("Контракт", "Акт", "ИНН", "Клиент", "Счет-фактура", "Дата счет-фактуры", "Сумма", "Статус", "Статус x-file");
    }

    @Override
    protected XSSFWorkbook getXSSFWorkbook(PaymentScheduleInvoiceFilter filter) {
        XSSFWorkbook workbook = createWorkbook();
        XSSFSheet sheet = createSheet(workbook, getFileName(filter));
        createHeader(getHeaders(), sheet);
        filter.setPage(0);
        filter.setSize(EXCEL_EXPORT_MAX_LIMIT);
        List<ActOfTransmissionInvoice> result = actOfTransmissionInvoiceRepository.getResultList(filter).getList();
        int rowIndex = 0;
        for (ActOfTransmissionInvoice actOfTransmissionInvoice : result) {
            Row row = sheet.createRow(++rowIndex);
            int cellIndex = 0;
            row.createCell(cellIndex++).setCellValue(Optional.ofNullable(actOfTransmissionInvoice.getActOfTransmission()).map(ActOfTransmission::getContract).map(ContractForLeasing::getNumber).orElse(""));
            row.createCell(cellIndex++).setCellValue(Optional.ofNullable(actOfTransmissionInvoice.getActOfTransmission()).map(ActOfTransmission::getNumber).orElse(""));
            row.createCell(cellIndex++).setCellValue(Optional.ofNullable(actOfTransmissionInvoice.getActOfTransmission()).map(ActOfTransmission::getContract).map(ContractForLeasing::getCustomer).map(Customer::getTin).orElse(""));
            row.createCell(cellIndex++).setCellValue(Optional.ofNullable(actOfTransmissionInvoice.getActOfTransmission()).map(ActOfTransmission::getContract).map(ContractForLeasing::getCustomer).map(Customer::getName).orElse(""));
            row.createCell(cellIndex++).setCellValue(actOfTransmissionInvoice.getNumber());
            row.createCell(cellIndex++).setCellValue(DateUtil.getLocalDateTimeAsStringWithoutSecond(actOfTransmissionInvoice.getInvoiceDate()));
            row.createCell(cellIndex++).setCellValue(Optional.ofNullable(actOfTransmissionInvoice.getTotalSellPrice()).map(Object::toString).orElse(""));
            row.createCell(cellIndex++).setCellValue(Optional.ofNullable(actOfTransmissionInvoice.getStatus()).map(ContractStatus::getName).orElse(""));
            row.createCell(cellIndex).setCellValue(Optional.ofNullable(actOfTransmissionInvoice.getXfileStatus()).map(XfileStatus::getName).orElse(""));
        }
        return workbook;
    }
}
