package uz.uzkassa.cts.handlers.export.excel;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.domain.hrms.Department;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.DepartmentRepository;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class DepartmentExportExcelHandler extends AbstractExportExcelHandler<BaseFilter> {

    DepartmentRepository departmentRepository;

    @Override
    public String getFileName(BaseFilter filter) {
        return "Departments.xlsx";
    }

    @Override
    protected List<String> getHeaders() {
        return List.of("Название", "Компания", "Филиал", "Статус");
    }

    @Override
    protected XSSFWorkbook getXSSFWorkbook(BaseFilter filter) {
        XSSFWorkbook workbook = createWorkbook();
        XSSFSheet sheet = createSheet(workbook, getFileName(filter));
        createHeader(getHeaders(), sheet);
        filter.setPage(0);
        filter.setSize(EXCEL_EXPORT_MAX_LIMIT);
        List<Department> result = departmentRepository.getResultList(filter).getList();
        int rowIndex = 0;
        for (Department department : result) {
            Row row = sheet.createRow(++rowIndex);
            int cellIndex = 0;
            row.createCell(cellIndex++).setCellValue(department.getName());
            row.createCell(cellIndex++).setCellValue(department.getBranch().getCompany().getName());
            row.createCell(cellIndex++).setCellValue(department.getBranch().getName());
            row.createCell(cellIndex).setCellValue(department.getStatus().getName());
        }
        return workbook;
    }
}
