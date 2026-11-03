package uz.uzkassa.cts.handlers.export.excel;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.domain.hrms.Candidate;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.CandidateRepository;
import uz.uzkassa.cts.utils.DateUtil;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CandidateExportExcelHandler extends AbstractExportExcelHandler<BaseFilter> {

    CandidateRepository candidateRepository;

    @Override
    public String getFileName(BaseFilter filter) {
        return "Candidates.xlsx";
    }

    @Override
    protected List<String> getHeaders() {
        return List.of("ФИО", "Компания", "Филиал", "Телефон", "Позиция", "Вакансия", "Дата создания");
    }

    @Override
    protected XSSFWorkbook getXSSFWorkbook(BaseFilter filter) {
        XSSFWorkbook workbook = createWorkbook();
        XSSFSheet sheet = createSheet(workbook, getFileName(filter));
        createHeader(getHeaders(), sheet);
        filter.setPage(0);
        filter.setSize(EXCEL_EXPORT_MAX_LIMIT);
        List<Candidate> result = candidateRepository.getResultList(filter).getList();
        int rowIndex = 0;
        for (Candidate candidate : result) {
            Row row = sheet.createRow(++rowIndex);
            int cellIndex = 0;
            row.createCell(cellIndex++).setCellValue(candidate.getFullName());
            row.createCell(cellIndex++).setCellValue(candidate.getDepartment().getBranch().getCompany().getName());
            row.createCell(cellIndex++).setCellValue(candidate.getDepartment().getBranch().getName());
            row.createCell(cellIndex++).setCellValue(candidate.getPhone());
            row.createCell(cellIndex++).setCellValue(candidate.getPosition().getName());
            row.createCell(cellIndex++).setCellValue(candidate.getVacancy().getName());
            row.createCell(cellIndex).setCellValue(DateUtil.getLocalDateTimeAsStringWithoutSecond(candidate.getCreatedDate()));
        }
        return workbook;
    }
}
