package uz.uzkassa.cts.handlers.export.excel;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.device.CompanyDeviceListNewDTO;
import uz.uzkassa.cts.enums.DevicePaymentType;
import uz.uzkassa.cts.enums.DeviceStatus;
import uz.uzkassa.cts.filter.CompanyDeviceListFilter;
import uz.uzkassa.cts.repository.CompanyDeviceRepository;
import uz.uzkassa.cts.utils.DateUtil;

import java.util.List;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class OldCompanyDeviceExportExcelHandler extends AbstractExportExcelHandler<CompanyDeviceListFilter> {

    CompanyDeviceRepository companyDeviceRepository;

    @Override
    public String getFileName(CompanyDeviceListFilter filter) {
        return "Company_Devices.xlsx";
    }

    @Override
    protected List<String> getHeaders() {
        return List.of("Серийный номер", "ФМ", "ИНН", "Клиент", "Тип оплаты", "Телефон", "ТТ", "Дата регистрации", "Статус", "Статус ККМ");
    }

    @Override
    protected XSSFWorkbook getXSSFWorkbook(CompanyDeviceListFilter filter) {
        XSSFWorkbook workbook = createWorkbook();
        XSSFSheet sheet = createSheet(workbook, getFileName(filter));
        createHeader(getHeaders(), sheet);
        filter.setPage(0);
        filter.setSize(EXCEL_EXPORT_MAX_LIMIT);
        List<CompanyDeviceListNewDTO> result = companyDeviceRepository.getList(filter).getList();
        int rowIndex = 0;
        for (CompanyDeviceListNewDTO companyDevice : result) {
            Row row = sheet.createRow(++rowIndex);
            int cellIndex = 0;
            row.createCell(cellIndex++).setCellValue(companyDevice.getDeviceSerial());
            row.createCell(cellIndex++).setCellValue(companyDevice.getFiscalModuleSerial());
            row.createCell(cellIndex++).setCellValue(companyDevice.getTin());
            row.createCell(cellIndex++).setCellValue(companyDevice.getCustomer());
            row.createCell(cellIndex++).setCellValue(Optional.ofNullable(companyDevice.getPaymentType()).map(DevicePaymentType::getName).orElse(""));
            row.createCell(cellIndex++).setCellValue(companyDevice.getPhone());
            row.createCell(cellIndex++).setCellValue(companyDevice.getCustomerBranch());
            row.createCell(cellIndex++).setCellValue(DateUtil.getLocalDateTimeAsStringWithoutSecond(companyDevice.getRegistrationDate()));
            row.createCell(cellIndex++).setCellValue(Optional.ofNullable(companyDevice.getStatus()).map(DeviceStatus::getName).orElse(""));
            row.createCell(cellIndex).setCellValue(Optional.ofNullable(companyDevice.getDeviceStatus()).map(DeviceStatus::getName).orElse(""));
        }
        return workbook;
    }
}
