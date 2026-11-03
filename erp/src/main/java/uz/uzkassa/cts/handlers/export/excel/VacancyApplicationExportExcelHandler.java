package uz.uzkassa.cts.handlers.export.excel;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.domain.hrms.VacancyApplication;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.VacancyApplicationRepository;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class VacancyApplicationExportExcelHandler extends AbstractExportExcelHandler<BaseFilter> {

    VacancyApplicationRepository vacancyApplicationRepository;

    @Override
    public String getFileName(BaseFilter filter) {
        return "Vacancy_Applications.xlsx";
    }

    @Override
    protected List<String> getHeaders() {
        return List.of("ФИО", "Компания", "Филиал", "Отдел", "Позиция", "Статус");
    }

    @Override
    protected XSSFWorkbook getXSSFWorkbook(BaseFilter filter) {
        XSSFWorkbook workbook = createWorkbook();
        XSSFSheet sheet = createSheet(workbook, getFileName(filter));
        createHeader(getHeaders(), sheet);
        filter.setPage(0);
        filter.setSize(EXCEL_EXPORT_MAX_LIMIT);
        List<VacancyApplication> result = vacancyApplicationRepository.getResultList(filter).getList();
        int rowIndex = 0;
        for (VacancyApplication vacancyApplication : result) {
            Row row = sheet.createRow(++rowIndex);
            int cellIndex = 0;
            row.createCell(cellIndex++).setCellValue(vacancyApplication.getName());
            row.createCell(cellIndex++).setCellValue(vacancyApplication.getDepartment().getBranch().getCompany().getName());
            row.createCell(cellIndex++).setCellValue(vacancyApplication.getDepartment().getBranch().getName());
            row.createCell(cellIndex++).setCellValue(vacancyApplication.getDepartment().getName());
            row.createCell(cellIndex++).setCellValue(vacancyApplication.getPosition().getName());
            row.createCell(cellIndex).setCellValue(vacancyApplication.getStatus().getName());
        }
        return workbook;
    }
}
