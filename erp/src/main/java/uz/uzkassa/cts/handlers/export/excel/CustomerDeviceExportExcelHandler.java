package uz.uzkassa.cts.handlers.export.excel;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.domain.CustomerDevice;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.CustomerDeviceRepository;
import uz.uzkassa.cts.utils.DateUtil;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CustomerDeviceExportExcelHandler extends AbstractExportExcelHandler<BaseFilter> {

    CustomerDeviceRepository customerDeviceRepository;

    @Override
    public String getFileName(BaseFilter filter) {
        return "Customer_Devices.xlsx";
    }

    @Override
    protected List<String> getHeaders() {
        return List.of("Серийный номер", "Дата регистрации", "Статус", "Создатель", "Компания", "Филиал", "Клиент");
    }

    @Override
    protected XSSFWorkbook getXSSFWorkbook(BaseFilter filter) {
        XSSFWorkbook workbook = createWorkbook();
        XSSFSheet sheet = createSheet(workbook, getFileName(filter));
        createHeader(getHeaders(), sheet);
        filter.setPage(0);
        filter.setSize(EXCEL_EXPORT_MAX_LIMIT);
        List<CustomerDevice> result = customerDeviceRepository.getResultList(filter).getList();
        int rowIndex = 0;
        for (CustomerDevice customerDevice : result) {
            Row row = sheet.createRow(++rowIndex);
            int cellIndex = 0;
            row.createCell(cellIndex++).setCellValue(customerDevice.getDevice().getSerialNumber());
            row.createCell(cellIndex++).setCellValue(DateUtil.getLocalDateTimeAsStringWithoutSecond(customerDevice.getCreatedDate()));
            row.createCell(cellIndex++).setCellValue(customerDevice.getStatus().getName());
            row.createCell(cellIndex++).setCellValue(customerDevice.getCreator().getName());
            row.createCell(cellIndex++).setCellValue(customerDevice.getCustomer().getCompany().getName());
            row.createCell(cellIndex++).setCellValue(customerDevice.getCustomer().getBranch().getName());
            row.createCell(cellIndex).setCellValue(customerDevice.getCustomer().getName());
        }
        return workbook;
    }
}
