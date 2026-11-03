package uz.uzkassa.cts.handlers.export.excel;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.domain.Holiday;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.HolidayRepository;
import uz.uzkassa.cts.utils.DateUtil;

import java.util.LinkedList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;


@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class HolidayExportExcelHandler extends AbstractExportExcelHandler<BaseFilter>  {

    HolidayRepository holidayRepository;

    @Override
    public String getFileName(BaseFilter filter) {
        return "Holidays.xlsx";
    }

    @Override
    protected List<String> getHeaders() {
        List<String> header = new LinkedList<>();
        header.add("Название");
        header.add("Компания");
        header.add("Дата начало");
        header.add("Дата окончания");
        header.add("Выходной");
        header.add("Статус");
        return header;
    }

    @Override
    protected XSSFWorkbook getXSSFWorkbook(BaseFilter filter) {
        XSSFWorkbook workbook = createWorkbook();
        XSSFSheet sheet = createSheet(workbook, getFileName(filter));
        createHeader(getHeaders(), sheet);
        filter.setPage(0);
        filter.setSize(EXCEL_EXPORT_MAX_LIMIT);
        List<Holiday> result = holidayRepository.getResultList(filter).getList();
        int rowIndex = 0;
        for (Holiday holiday : result) {
            Row row = sheet.createRow(++rowIndex);
            int cellIndex = 0;
            row.createCell(cellIndex++).setCellValue(holiday.getName());
            row.createCell(cellIndex++).setCellValue(holiday.getCompany().getName());
            row.createCell(cellIndex++).setCellValue(DateUtil.getLocalDateAsString(holiday.getFromDate()));
            row.createCell(cellIndex++).setCellValue(DateUtil.getLocalDateAsString(holiday.getToDate()));
            row.createCell(cellIndex++).setCellValue(holiday.isDayOff());
            row.createCell(cellIndex).setCellValue(holiday.getStatus().getName());
        }
        return workbook;
    }
}
