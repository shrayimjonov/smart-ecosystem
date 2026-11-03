package uz.uzkassa.cts.handlers.export.excel;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.domain.hrms.Attendance;
import uz.uzkassa.cts.filter.AttendanceFilter;
import uz.uzkassa.cts.repository.AttendanceRepository;
import uz.uzkassa.cts.utils.DateUtil;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AttendanceExportExcelHandler extends AbstractExportExcelHandler<AttendanceFilter> {

    AttendanceRepository attendanceRepository;

    @Override
    public String getFileName(AttendanceFilter filter) {
        return "Attendances_" + filter.getMonth() + "_" + filter.getYear() + ".xlsx";
    }

    @Override
    protected List<String> getHeaders() {
        return null;
    }

    private List<String> getHeaders(AttendanceFilter filter) {
        List<String> headers = new ArrayList<>();
        headers.add("Сотрудник");
        for (int day = 1; day <= YearMonth.of(filter.getYear(), filter.getMonth()).lengthOfMonth(); day++) {
            headers.add(String.valueOf(day));
        }
        headers.add("Норма дни");
        headers.add("Актуальные дни");
        headers.add("Норма часы");
        headers.add("Актуальные часы");
        return headers;
    }

    @Override
    protected XSSFWorkbook getXSSFWorkbook(AttendanceFilter filter) {
        XSSFWorkbook workbook = createWorkbook();
        XSSFSheet sheet = createSheet(workbook, getFileName(filter));
        createHeader(getHeaders(filter), sheet);
        filter.setPage(0);
        filter.setSize(EXCEL_EXPORT_MAX_LIMIT);
        List<Attendance> result = attendanceRepository.getResultList(filter).getList();
        int rowIndex = 0;
        for (Attendance attendance : result) {
            Row row = sheet.createRow(++rowIndex);
            int cellIndex = 0;
            row.createCell(cellIndex++).setCellValue(attendance.getEmployee().getFullName());
            attendance.getItems().forEach(attendanceItem -> row.createCell(attendanceItem.getAttendanceDay()).setCellValue(attendanceItem.getHolidayId() != null ? attendanceItem.getHoliday().getName() : attendanceItem.getLeaveRequestId() != null ? attendanceItem.getLeaveRequest().getName() : DateUtil.convertMinutesToHoursAsString(attendanceItem.getActualHours())));
            cellIndex += attendance.getItems().size();
            row.createCell(cellIndex++).setCellValue(attendance.getNormalDays());
            row.createCell(cellIndex++).setCellValue(attendance.getActualDays());
            row.createCell(cellIndex++).setCellValue(DateUtil.convertMinutesToHoursAsString(attendance.getMonthlyPlannedHours()));
            row.createCell(cellIndex).setCellValue(DateUtil.convertMinutesToHoursAsString(attendance.getMonthlyActualHours()));
        }
        return workbook;
    }
}
