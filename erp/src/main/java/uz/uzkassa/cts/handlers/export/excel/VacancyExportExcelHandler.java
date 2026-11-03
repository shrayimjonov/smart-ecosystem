package uz.uzkassa.cts.handlers.export.excel;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.domain.hrms.Vacancy;
import uz.uzkassa.cts.filter.VacancyFilter;
import uz.uzkassa.cts.repository.VacancyRepository;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class VacancyExportExcelHandler extends AbstractExportExcelHandler<VacancyFilter> {

    VacancyRepository vacancyRepository;

    @Override
    public String getFileName(VacancyFilter filter) {
        return "Vacancies.xlsx";
    }

    @Override
    protected List<String> getHeaders() {
        return List.of("ФИО заявителя", "Название", "Отдел", "Должность", "Статус", "Заявка");
    }

    @Override
    protected XSSFWorkbook getXSSFWorkbook(VacancyFilter filter) {
        XSSFWorkbook workbook = createWorkbook();
        XSSFSheet sheet = createSheet(workbook, getFileName(filter));
        createHeader(getHeaders(), sheet);
        filter.setPage(0);
        filter.setSize(EXCEL_EXPORT_MAX_LIMIT);
        List<Vacancy> result = vacancyRepository.getResultList(filter).getList();
        int rowIndex = 0;
        for (Vacancy vacancy : result) {
            Row row = sheet.createRow(++rowIndex);
            int cellIndex = 0;
            row.createCell(cellIndex++).setCellValue(vacancy.getCreator().getName());
            row.createCell(cellIndex++).setCellValue(vacancy.getName());
            row.createCell(cellIndex++).setCellValue(vacancy.getDepartment().getName());
            row.createCell(cellIndex++).setCellValue(vacancy.getPosition().getName());
            row.createCell(cellIndex++).setCellValue(vacancy.getStatus().getName());
            row.createCell(cellIndex).setCellValue(vacancy.getApplication().getName());
        }
        return workbook;
    }
}
