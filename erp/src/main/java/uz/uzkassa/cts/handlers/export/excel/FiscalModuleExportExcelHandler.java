package uz.uzkassa.cts.handlers.export.excel;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.domain.FiscalModule;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.FiscalModuleRepository;
import uz.uzkassa.cts.utils.DateUtil;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class FiscalModuleExportExcelHandler extends AbstractExportExcelHandler<BaseFilter> {

    FiscalModuleRepository fiscalModuleRepository;

    @Override
    public String getFileName(BaseFilter filter) {
        return "Fiscal_Modules.xlsx";
    }

    @Override
    protected List<String> getHeaders() {
        return List.of("Серийный номер", "Дата регистрации", "Кем создан", "Статус");
    }

    @Override
    protected XSSFWorkbook getXSSFWorkbook(BaseFilter filter) {
        XSSFWorkbook workbook = createWorkbook();
        XSSFSheet sheet = createSheet(workbook, getFileName(filter));
        createHeader(getHeaders(), sheet);
        filter.setPage(0);
        filter.setSize(EXCEL_EXPORT_MAX_LIMIT);
        List<FiscalModule> result = fiscalModuleRepository.getResultList(filter).getList();
        int rowIndex = 0;
        for (FiscalModule fiscalModule : result) {
            Row row = sheet.createRow(++rowIndex);
            int cellIndex = 0;
            row.createCell(cellIndex++).setCellValue(fiscalModule.getSerialNumber());
            row.createCell(cellIndex++).setCellValue(DateUtil.getLocalDateTimeAsStringWithoutSecond(fiscalModule.getCreatedDate()));
            row.createCell(cellIndex++).setCellValue(fiscalModule.getCreator().getFullName());
            row.createCell(cellIndex).setCellValue(fiscalModule.getStatus().getName());
        }
        return workbook;
    }
}
