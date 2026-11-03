package uz.uzkassa.cts.handlers.export.excel;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.domain.BranchDevice;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.BranchDeviceRepository;
import uz.uzkassa.cts.utils.DateUtil;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class BranchDeviceExportExcelHandler extends AbstractExportExcelHandler<BaseFilter> {

    BranchDeviceRepository branchDeviceRepository;

    @Override
    public String getFileName(BaseFilter filter) {
        return "Branch_Devices.xlsx";
    }

    @Override
    protected List<String> getHeaders() {
        return List.of("Серийный номер", "Дата регистрации", "Статус", "Создатель", "Компания", "Филиал");
    }

    @Override
    protected XSSFWorkbook getXSSFWorkbook(BaseFilter filter) {
        XSSFWorkbook workbook = createWorkbook();
        XSSFSheet sheet = createSheet(workbook, getFileName(filter));
        createHeader(getHeaders(), sheet);
        filter.setPage(0);
        filter.setSize(EXCEL_EXPORT_MAX_LIMIT);
        List<BranchDevice> result = branchDeviceRepository.getResultList(filter).getList();
        int rowIndex = 0;
        for (BranchDevice branchDevice : result) {
            Row row = sheet.createRow(++rowIndex);
            int cellIndex = 0;
            row.createCell(cellIndex++).setCellValue(branchDevice.getDevice().getSerialNumber());
            row.createCell(cellIndex++).setCellValue(DateUtil.getLocalDateTimeAsStringWithoutSecond(branchDevice.getCreatedDate()));
            row.createCell(cellIndex++).setCellValue(branchDevice.getStatus().getName());
            row.createCell(cellIndex++).setCellValue(branchDevice.getCreator().getName());
            row.createCell(cellIndex++).setCellValue(branchDevice.getBranch().getCompany().getName());
            row.createCell(cellIndex).setCellValue(branchDevice.getBranch().getName());
        }
        return workbook;
    }
}
