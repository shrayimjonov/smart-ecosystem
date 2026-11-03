package uz.uzkassa.cts.handlers.export.excel;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.domain.customer.CustomerRequest;
import uz.uzkassa.cts.filter.CustomerRequestFilter;
import uz.uzkassa.cts.repository.CustomerRequestRepository;
import uz.uzkassa.cts.utils.DateUtil;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CustomerRequestExportExcelHandler extends AbstractExportExcelHandler<CustomerRequestFilter> {

    CustomerRequestRepository customerRequestRepository;

    @Override
    public String getFileName(CustomerRequestFilter filter) {
        return "Customer_Requests.xlsx";
    }

    @Override
    protected List<String> getHeaders() {
        return List.of("Название организации", "ИНН", "Номер", "Дата", "Тип", "Статус по звонку", "Время обработки", "Статус по результату обзвона");
    }

    @Override
    protected XSSFWorkbook getXSSFWorkbook(CustomerRequestFilter filter) {
        XSSFWorkbook workbook = createWorkbook();
        XSSFSheet sheet = createSheet(workbook, getFileName(filter));
        createHeader(getHeaders(), sheet);
        filter.setPage(0);
        filter.setSize(EXCEL_EXPORT_MAX_LIMIT);
        List<CustomerRequest> result = customerRequestRepository.getResultList(filter).getList();
        int rowIndex = 0;
        for (CustomerRequest customerRequest : result) {
            Row row = sheet.createRow(++rowIndex);
            int cellIndex = 0;
            row.createCell(cellIndex++).setCellValue(customerRequest.getCustomer().getName());
            row.createCell(cellIndex++).setCellValue(customerRequest.getCustomer().getTin());
            row.createCell(cellIndex++).setCellValue(customerRequest.getCustomer().getPhone());
            row.createCell(cellIndex++).setCellValue(DateUtil.getLocalDateTimeAsStringWithoutSecond(customerRequest.getCreatedDate()));
            row.createCell(cellIndex++).setCellValue(customerRequest.getRequestType().getName());
            row.createCell(cellIndex++).setCellValue(customerRequest.getCallStatus().getName());
            row.createCell(cellIndex++).setCellValue(DateUtil.getLocalDateTimeAsStringWithoutSecond(customerRequest.getResolvedDate()));
            row.createCell(cellIndex).setCellValue(customerRequest.getStatus().getName());
        }
        return workbook;
    }
}
