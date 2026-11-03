package uz.uzkassa.cts.handlers.export.excel;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.domain.BranchFiscalModule;
import uz.uzkassa.cts.domain.CompanyFiscalModule;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.BranchFiscalModuleRepository;
import uz.uzkassa.cts.repository.CompanyFiscalModuleRepository;
import uz.uzkassa.cts.utils.DateUtil;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class BranchFiscalModuleExportExcelHandler extends AbstractExportExcelHandler<BaseFilter> {

    BranchFiscalModuleRepository branchFiscalModuleRepository;

    @Override
    public String getFileName(BaseFilter filter) {
        return "Branch_Fiscal_Modules.xlsx";
    }

    @Override
    protected List<String> getHeaders() {
        return List.of("Серийный номер", "Дата регистрации", "Кем создан", "Статус", "Компания", "Филиал");
    }

    @Override
    protected XSSFWorkbook getXSSFWorkbook(BaseFilter filter) {
        XSSFWorkbook workbook = createWorkbook();
        XSSFSheet sheet = createSheet(workbook, getFileName(filter));
        createHeader(getHeaders(), sheet);
        filter.setPage(0);
        filter.setSize(EXCEL_EXPORT_MAX_LIMIT);
        List<BranchFiscalModule> result = branchFiscalModuleRepository.getResultList(filter).getList();
        int rowIndex = 0;
        for (BranchFiscalModule branchFiscalModule : result) {
            Row row = sheet.createRow(++rowIndex);
            int cellIndex = 0;
            row.createCell(cellIndex++).setCellValue(branchFiscalModule.getFiscalModule().getSerialNumber());
            row.createCell(cellIndex++).setCellValue(DateUtil.getLocalDateTimeAsStringWithoutSecond(branchFiscalModule.getCreatedDate()));
            row.createCell(cellIndex++).setCellValue(branchFiscalModule.getCreator().getFullName());
            row.createCell(cellIndex++).setCellValue(branchFiscalModule.getStatus().getName());
            row.createCell(cellIndex++).setCellValue(branchFiscalModule.getBranch().getCompany().getName());
            row.createCell(cellIndex).setCellValue(branchFiscalModule.getBranch().getName());
        }
        return workbook;
    }
}
