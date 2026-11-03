package uz.uzkassa.cts.handlers.export.excel;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.domain.CompanyDevice;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.CompanyDeviceRepository;
import uz.uzkassa.cts.utils.DateUtil;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CompanyDeviceExportExcelHandler extends AbstractExportExcelHandler<BaseFilter> {

    CompanyDeviceRepository companyDeviceRepository;

    @Override
    public String getFileName(BaseFilter filter) {
        return "Company_Devices.xlsx";
    }

    @Override
    protected List<String> getHeaders() {
        return List.of("Серийный номер", "Дата регистрации", "Статус", "Создатель", "Компания");
    }

    @Override
    protected XSSFWorkbook getXSSFWorkbook(BaseFilter filter) {
        XSSFWorkbook workbook = createWorkbook();
        XSSFSheet sheet = createSheet(workbook, getFileName(filter));
        createHeader(getHeaders(), sheet);
        filter.setPage(0);
        filter.setSize(EXCEL_EXPORT_MAX_LIMIT);
        List<CompanyDevice> result = companyDeviceRepository.getResultList(filter).getList();
        int rowIndex = 0;
        for (CompanyDevice companyDevice : result) {
            Row row = sheet.createRow(++rowIndex);
            int cellIndex = 0;
            row.createCell(cellIndex++).setCellValue(companyDevice.getDevice().getSerialNumber());
            row.createCell(cellIndex++).setCellValue(DateUtil.getLocalDateTimeAsStringWithoutSecond(companyDevice.getCreatedDate()));
            row.createCell(cellIndex++).setCellValue(companyDevice.getStatus().getName());
            row.createCell(cellIndex++).setCellValue(companyDevice.getCreator().getName());
            row.createCell(cellIndex).setCellValue(companyDevice.getCompany().getName());
        }
        return workbook;
    }
}
