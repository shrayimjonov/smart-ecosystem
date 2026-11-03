package uz.uzkassa.cts.handlers.export.excel;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.domain.hrms.Groups;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.GroupRepository;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class GroupExportExcelHandler extends AbstractExportExcelHandler<BaseFilter> {

    GroupRepository groupRepository;

    @Override
    public String getFileName(BaseFilter filter) {
        return "Groups.xlsx";
    }

    @Override
    protected List<String> getHeaders() {
        return List.of("Название", "Компания", "Филиал", "Отдел", "Статус");
    }

    @Override
    protected XSSFWorkbook getXSSFWorkbook(BaseFilter filter) {
        XSSFWorkbook workbook = createWorkbook();
        XSSFSheet sheet = createSheet(workbook, getFileName(filter));
        createHeader(getHeaders(), sheet);
        filter.setPage(0);
        filter.setSize(EXCEL_EXPORT_MAX_LIMIT);
        List<Groups> result = groupRepository.getResultList(filter).getList();
        int rowIndex = 0;
        for (Groups group : result) {
            Row row = sheet.createRow(++rowIndex);
            int cellIndex = 0;
            row.createCell(cellIndex++).setCellValue(group.getName());
            row.createCell(cellIndex++).setCellValue(group.getDepartment().getBranch().getCompany().getName());
            row.createCell(cellIndex++).setCellValue(group.getDepartment().getBranch().getName());
            row.createCell(cellIndex++).setCellValue(group.getDepartment().getName());
            row.createCell(cellIndex).setCellValue(group.getStatus().getName());
        }
        return workbook;
    }
}
