package uz.uzkassa.cts.handlers.export.excel;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.domain.Branch;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.BranchRepository;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class BranchExportExcelHandler extends AbstractExportExcelHandler<BaseFilter> {

    BranchRepository branchRepository;

    @Override
    public String getFileName(BaseFilter filter) {
        return "Branches.xlsx";
    }

    @Override
    protected List<String> getHeaders() {
        return List.of("Название", "Родительский филиал", "Название компании", "Регион", "Район", "Статус");
    }

    @Override
    protected XSSFWorkbook getXSSFWorkbook(BaseFilter filter) {
        XSSFWorkbook workbook = createWorkbook();
        XSSFSheet sheet = createSheet(workbook, getFileName(filter));
        createHeader(getHeaders(), sheet);
        filter.setPage(0);
        filter.setSize(EXCEL_EXPORT_MAX_LIMIT);
        List<Branch> result = branchRepository.getResultList(filter).getList();
        int rowIndex = 0;
        for (Branch branch : result) {
            Row row = sheet.createRow(++rowIndex);
            int cellIndex = 0;
            row.createCell(cellIndex++).setCellValue(branch.getName());
            row.createCell(cellIndex++).setCellValue(branch.getParentId() != null ? branch.getParent().getName() : "");
            row.createCell(cellIndex++).setCellValue(branch.getCompany().getName());
            row.createCell(cellIndex++).setCellValue(branch.getAddress().getRegion().getName());
            row.createCell(cellIndex++).setCellValue(branch.getAddress().getDistrict().getName());
            row.createCell(cellIndex).setCellValue(branch.getStatus().getName());
        }
        return workbook;
    }
}
