package uz.uzkassa.cts.handlers.export.excel;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.device.RegistryListDTO;
import uz.uzkassa.cts.filter.CompanyDeviceListFilter;
import uz.uzkassa.cts.service.DeviceService;

import java.util.List;

@Component
@Slf4j
public class RegistryDeviceExportExcelHandler extends AbstractExportExcelHandler<CompanyDeviceListFilter> {

    private final DeviceService deviceService;

    public RegistryDeviceExportExcelHandler(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @Override
    public String getFileName(CompanyDeviceListFilter filter) {
        return "Registry_devices.xlsx";
    }

    @Override
    protected List<String> getHeaders() {
        return List.of("ККМ", "ФМ", "Клиент", "Торговая точка", "Филиал", "Дата регистрации", "Статус", "ККМ статус");
    }

    @Override
    protected XSSFWorkbook getXSSFWorkbook(CompanyDeviceListFilter filter) {
        XSSFWorkbook workbook = createWorkbook();
        XSSFSheet sheet = createSheet(workbook, getFileName(filter));
        createHeader(getHeaders(), sheet);
        filter.setPage(0);
        filter.setSize(EXCEL_EXPORT_MAX_LIMIT);
        Page<RegistryListDTO> result = deviceService.listRegister(filter);
        int rowIndex = 0;
        for (RegistryListDTO item : result.getContent()) {
            Row row = sheet.createRow(++rowIndex);
            int cellIndex = 0;
            row.createCell(cellIndex++).setCellValue(item.getDeviceSerial());
            row.createCell(cellIndex++).setCellValue(item.getFiscalModuleSerial());
            row.createCell(cellIndex++).setCellValue(item.getCustomer());
            row.createCell(cellIndex++).setCellValue(item.getCustomerBranch());
            row.createCell(cellIndex++).setCellValue(item.getBranch());
            row.createCell(cellIndex++).setCellValue(item.getRegistrationDate().toString());
            row.createCell(cellIndex++).setCellValue(item.getStatus());
            row.createCell(cellIndex).setCellValue(item.getDeviceStatus());
        }
        return workbook;
    }
}
