package uz.uzkassa.cts.handlers.export.excel;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.domain.CompanyFiscalModule;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.CompanyFiscalModuleRepository;
import uz.uzkassa.cts.utils.DateUtil;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CompanyFiscalModuleExportExcelHandler extends AbstractExportExcelHandler<BaseFilter> {

    CompanyFiscalModuleRepository companyFiscalModuleRepository;

    @Override
    public String getFileName(BaseFilter filter) {
        return "Company_Fiscal_Modules.xlsx";
    }

    @Override
    protected List<String> getHeaders() {
        return List.of("Серийный номер", "Дата регистрации", "Кем создан", "Статус", "Компания");
    }

    @Override
    protected XSSFWorkbook getXSSFWorkbook(BaseFilter filter) {
        XSSFWorkbook workbook = createWorkbook();
        XSSFSheet sheet = createSheet(workbook, getFileName(filter));
        createHeader(getHeaders(), sheet);
        filter.setPage(0);
        filter.setSize(EXCEL_EXPORT_MAX_LIMIT);
        List<CompanyFiscalModule> result = companyFiscalModuleRepository.getResultList(filter).getList();
        int rowIndex = 0;
        for (CompanyFiscalModule companyFiscalModule : result) {
            Row row = sheet.createRow(++rowIndex);
            int cellIndex = 0;
            row.createCell(cellIndex++).setCellValue(companyFiscalModule.getFiscalModule().getSerialNumber());
            row.createCell(cellIndex++).setCellValue(DateUtil.getLocalDateTimeAsStringWithoutSecond(companyFiscalModule.getCreatedDate()));
            row.createCell(cellIndex++).setCellValue(companyFiscalModule.getCreator().getFullName());
            row.createCell(cellIndex++).setCellValue(companyFiscalModule.getStatus().getName());
            row.createCell(cellIndex).setCellValue(companyFiscalModule.getCompany().getName());
        }
        return workbook;
    }
}
